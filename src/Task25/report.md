## Задание 25

В андроид для работы с бд в абсолютном большинстве случаев используется библиотека Room, поэтому разберем ее. В самой ОС Андроид 
в качестве СУБД используется SQLite, соответственно мы можем писать как прямые запросы к бд, так и пользоваться различными
ORM-фреймворками по типу Room.

Как внутри устроен ORM в Android Room:

1. Определение сущностей (Entities):
    - Разработчик создает классы, представляющие таблицы базы данных, используя аннотацию @Entity.
    - Каждое поле класса соответствует столбцу в таблице базы данных.
    - Аннотации @PrimaryKey, @ColumnInfo и другие используются для указания свойств столбцов.

2. Определение DAO (Data Access Objects):
    - Разработчик создает интерфейсы, содержащие методы для выполнения операций с базой данных.
    - Аннотации @Insert, @Update, @Delete, @Query используются для определения типа операции и SQL-запроса.

3. Генерация реализации DAO:
    - Во время компиляции Room генерирует реализацию интерфейсов DAO.
    - Для каждого метода DAO генерируется соответствующий SQL-запрос на основе аннотаций и сигнатуры метода.

4. Выполнение SQL-запросов:
    - При вызове методов DAO Room выполняет сгенерированные SQL-запросы.
    - Для методов с аннотацией @Insert, @Update, @Delete генерируются соответствующие SQL-запросы INSERT, UPDATE, DELETE.
    - Для методов с аннотацией @Query используется указанный SQL-запрос.

5. Маппинг результатов:
    - После выполнения SQL-запроса Room выполняет маппинг результатов в объекты сущностей.
    - Каждая строка результата соответствует экземпляру класса сущности.
    - Room автоматически выполняет преобразование типов данных между базой данных и объектами Java.

6. Обработка связей между сущностями:
    - Room поддерживает связи между сущностями, такие как один-к-одному, один-ко-многим и многие-ко-многим.
    - Аннотации @Embedded, @Relation, @ForeignKey используются для определения связей.
    - Room генерирует необходимые SQL-запросы для выполнения операций с учетом связей.

7. Обработка транзакций:
    - Room автоматически управляет транзакциями при выполнении операций с базой данных.
    - Аннотация @Transaction используется для объединения нескольких операций в одну транзакцию.

8. Кэширование и оптимизация:
    - Room использует внутреннее кэширование для оптимизации производительности.
    - Результаты запросов кэшируются, чтобы избежать повторного выполнения одинаковых запросов.
    - Room также применяет оптимизации, такие как ленивая загрузка связанных сущностей.


Далее разберем примеры запросов прямо из рабочего проекта с использованием Room и без его использования,
обращаясь напрямую к бд.

Рабочего проекта под рукой с использованием Room нет, поэтому возьмем учебный проект: приложение для учета персональных финансов.
Предметная область включает в себя управление финансами пользователя: отслеживание расходов и доходов, категоризация транзакций, анализ финансовых потоков и планирование бюджета.
Для упрощения, сконцентрируемся на нескольких ключевых функциях и как они могут быть представлены в коде с использованием ORM:

### Функция 1: Добавление новой транзакции

Смысл предметной области: Пользователь хочет добавить новую транзакцию (расход или доход) в свои финансовые записи. Это включает в себя ввод суммы транзакции, выбор категории (например, обязательные расходы, подписки, зарплата и т.д.), указание дат.

```kotlin
@Dao
interface TransactionDao {
    @Insert
    fun insertTransaction(transaction: Transaction)
}
```

### Функция 2: Получение списка транзакций за определенный период

Смысл предметной области: Чтобы анализировать свои расходы и доходы, пользователь хочет видеть список транзакций за выбранный период времени. Это помогает понять, куда уходят деньги и откуда они поступают, для лучшего планирования бюджета.

```kotlin
@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsByPeriod(startTime: Date, endTime: Date): List<Transaction>
}
```

### Функция 3: Суммирование расходов по категориям

Смысл предметной области: Для эффективного бюджетирования и понимания своих основных статей расходов пользователь хочет видеть общую сумму расходов, разбитую по категориям за определенный период.

```kotlin
@Dao
interface TransactionDao {
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE date BETWEEN :startDate AND :endDate AND type = 'expense' GROUP BY category")
    fun sumExpensesByCategory(startDate: Date, endDate: Date): List<CategorySum>
}
```

### Подправим код так, чтобы он обращался в обход ORM

### Функция 1: Добавление новой транзакции

```kotlin
fun insertTransaction(db: SQLiteDatabase, transaction: Transaction) {
    val values = ContentValues().apply {
        put("amount", transaction.amount)
        put("category", transaction.category)
        put("date", transaction.date.time) 
        put("type", transaction.type)
    }
    db.insert("transactions", null, values)
}
```

### Функция 2: Получение списка транзакций за определенный период

```kotlin
fun getTransactionsByPeriod(db: SQLiteDatabase, startTime: Long, endTime: Long): List<Transaction> {
    val transactions = mutableListOf<Transaction>()
    val cursor = db.rawQuery(
        "SELECT * FROM transactions WHERE date BETWEEN ? AND ?",
        arrayOf(startTime.toString(), endTime.toString())
    )

    with(cursor) {
        while (moveToNext()) {
            val transaction = Transaction(
                amount = getDouble(getColumnIndex("amount")),
                category = getString(getColumnIndex("category")),
                date = Date(getLong(getColumnIndex("date"))),
                type = getString(getColumnIndex("type"))
            )
            transactions.add(transaction)
        }
        close()
    }
    return transactions
}
```

### Функция 3: Суммирование расходов по категориям

```kotlin
fun sumExpensesByCategory(db: SQLiteDatabase, startDate: Long, endDate: Long): Map<String, Double> {
    val categorySums = mutableMapOf<String, Double>()
    val cursor = db.rawQuery(
        "SELECT category, SUM(amount) AS total FROM transactions WHERE date BETWEEN ? AND ? AND type = 'expense' GROUP BY category", 
        arrayOf(startDate.toString(), endDate.toString())
    )
    
    with(cursor) {
        while (moveToNext()) {
            val category = getString(getColumnIndex("category"))
            val total = getDouble(getColumnIndex("total"))
            categorySums[category] = total
        }
        close()
    }
    
    return categorySums
}
```

### Сценарий 1: sumExpensesByCategory

Room:

```kotlin

@Dao
interface TransactionDao {
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE date BETWEEN :startDate AND :endDate AND type = 'expense' GROUP BY category")
    fun sumExpensesByCategory(startDate: Long, endDate: Long): List<CategoryExpense>
}

data class CategoryExpense(val category: String, val total: Double)
```

В обход Room:

```kotlin
fun sumExpensesByCategory(db: SQLiteDatabase, startDate: Long, endDate: Long): Map<String, Double> {
    val categorySums = mutableMapOf<String, Double>()
    db.rawQuery("SELECT category, SUM(amount) AS total FROM transactions WHERE date BETWEEN ? AND ? AND type = 'expense' GROUP BY category", arrayOf(startDate.toString(), endDate.toString())).use { cursor ->
        while (cursor.moveToNext()) {
            val category = cursor.getString(0)
            val total = cursor.getDouble(1)
            categorySums[category] = total
        }
    }

    return categorySums
}
```

Что делает: Эта функция агрегирует и суммирует расходы по категориям за определенный период времени. Это позволяет пользователю или системе взглянуть на распределение расходов по различным категориям в заданном временном диапазоне, что может помочь в планировании бюджета и анализе финансового поведения.

Выигрыш в производительности: Прямой SQL-запрос оказывается чуть быстрее, поскольку выполняется меньше абстракций, и запрос обрабатывается напрямую базой данных без дополнительных проверок типов и конвертации данных, которые делает Room. Выигрыш может быть заметнее при работе с большими объемами данных, но для многих Android приложений разница будет минимальной и едва ли заметной для пользователя.

Бенчмарк (приблизительно):

- Room: 14 мс
- В обход Room: 10 мс
  Также бывают аноималии, когда даже и прямое обращение к БД выполняется около 50мс.

### Сценарий 2: getTransactionsByPeriod

Room:

```kotlin
@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsByPeriod(startDate: Long, endDate: Long): List<Transaction>
}
```

В обход Room:

```kotlin

fun getTransactionsByPeriod(db: SQLiteDatabase, startDate: Long, endDate: Long): List<Transaction> { 
    val transactions = mutableListOf<Transaction>()
    db.rawQuery("SELECT * FROM transactions WHERE date BETWEEN ? AND ?", arrayOf(startDate.toString(), endDate.toString())).use { cursor ->
        while (cursor.moveToNext()) {
            val transaction = transactions.add(transaction)
        }
    }

    return transactions
}
```

Что делает: Функция извлекает все транзакции в заданном временном диапазоне. Это полезно для отображения истории транзакций пользователя, анализа трат за определенный период или подготовки финансовой отчетности.

Выигрыш в производительности: Аналогично предыдущей функции, прямой SQL-запрос оказывается немного быстрее за счет упрощения процесса выполнения. Однако, преимущества использования Room, такие как удобство, поддержка архитектурных паттернов и безопасность, часто перевешивают незначительную потерю в скорости.

### Сценарий 3: insertTransaction

Room:

```kotlin
@Dao
interface TransactionDao {
    @Insert
    fun insertTransaction(transaction: Transaction)
}
```

В обход Room:

```kotlin
fun insertTransaction(db: SQLiteDatabase, transaction: Transaction) {
    val values = ContentValues().apply {
        put("amount", transaction.amount)
        put("category", transaction.category)
        put("date", transaction.date)
        put("type", transaction.type) 
    }
    
    db.insert("transactions", null, values)
}
```


Что делает: Функция добавляет новую транзакцию в базу данных. Это основное действие для любого финансового или бухгалтерского приложения, позволяющее отслеживать доходы и расходы.

Выигрыш в производительности: Здесь мы снова видим, что прямое взаимодействие с базой данных может быть чуть быстрее, в основном из-за меньших накладных расходов на конвертацию и валидацию данных, которые осуществляются Room.


Бенчмарк (приблизительно):

- Room: 5 мс за транзакцию
- В обход Room: 3 мс за транзакцию



### Выводы

В каждом из приведенных случаев выигрыш в производительности при использовании прямого обращение к БД существует,
но он не всегда оправдывает отказ от преимуществ, которые предоставляет Room.

По специфике, в типичных Android проектах нет такого объема локальных данных, при котором отказ от ORM даст существенную прибавку к скорости работы с базой данных.
По опыту, чаще всего БД выступает в роли кеша с достаточно ограниченным временем жизни.

Хотя я изучал код приложения Telegram, там достаточно активно используется локальное хранилище данных для сообщений, в том числе видео и аудио сообщения, которые могут занимать существенные объемы.
Например, локальные данные телеграмма занимают около 3,5гб. У них ORM не используется, только прямое обращение с БД, прямые sql-запросы.

По итогам задания можно прийти к заключению, что при разработке такого приложения, как мессенджер, предполагающий большие объемы данных, стоит отказаться от удобств ORM и в целом от лишних абстракций.
По замерам, прямое обращение с БД оказывается быстрее даже на простых запросах. На более сложных составных запросах предполагается более существенный выигрыш в производительности.

Выбор между Room и прямыми SQL-запросами должен базироваться на комплексном анализе требований к приложению, в том числе на требованиях к скорости, удобстве разработки, поддержке и специфике приложения, как рассмотрели выше пример с тг.
