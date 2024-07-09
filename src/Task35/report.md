## Задание 35

Пример 1: Работа с Room (библиотека для работы с базами данных)

Абстракция: Объект доступа к данным (DAO)

```kotlin

// Абстракция: Таблица базы данных с полями id, name и age.
// Бесконечное количество свойств пользователя в реальном мире отображается
// в класс User с полями, необходимыми для работы системы.
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val age: Int
)



// Абстракция: Интерфейс доступа к данным.
// Взаимодействуем с таблицей базы данных, предоставляя методы для вставки и извлечения данных.
// При этом реализация взаимодействия с самой базой SQLite скрыта за интерфейсом.
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?
}

// Абстракция: База данных.
// Создаем абстракцию базы данных, включая определение DAO и управление версиями базы данных.
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

Пример 2: Работа с ViewModel и LiveData
Абстракция: Управление состоянием UI с помощью ViewModel и LiveData

```kotlin
// ViewModel class
// Абстракция: Управление состоянием UI.
// Этот код абстрагирует бизнес-логику и хранение данных от пользовательского интерфейса, обеспечивая реактивное обновление UI.
class UserViewModel(private val useCase: GetUserUseCase) : ViewModel() {
    val user: LiveData<User> = liveData {
        emit(useCase.getUserById(1) ?: User(0, "Unknown", 0))
    }
}

// Activity class
// Абстракция: Наблюдение за изменениями данных.
// Aбстрагируем обновление пользовательского интерфейса при изменении данных, используя LiveData.
class UserActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.user.observe(this, Observer { user ->
            // Update UI with user data
            findViewById<TextView>(R.id.userName).text = user.name
            findViewById<TextView>(R.id.userAge).text = user.age.toString()
        })
    }
}

```

Пример 3: Взаимодействие с сетевыми сервисами с использованием Retrofit
Абстракция: Работа с сетевыми запросами

```kotlin
// Абстракция: Модель данных сетевого ответа.
// Абстрагируем сетевой ответ в виде объекта данных, который можно использовать в приложении.
data class UserResponse(val id: Int, val name: String, val age: Int)

// Абстракция: Сетевой сервис.
// Абстрагируем взаимодействие с сетевыми API, предоставляя методы для выполнения HTTP-запросов.
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserResponse
}

// Абстракция: Репозиторий данных.
// Абстрагируем источник данных, объединяя локальные и удаленные данные.
class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(id: Int): UserResponse {
        return apiService.getUser(id)
    }
}

```

Пример 4: Управление жизненным циклом Activity
Абстракция: Управление жизненным циклом и состоянием Activity

```kotlin

// Абстракция: Общий функционал Activity.
// Абстрагируем общие операции, такие как настройка макета и инициализация представлений, которые могут использоваться в наследниках.
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView()
    
}

// Конкретная активити
class MainActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        //...
    }
}

```

Пример 5: Обработка событий пользовательского интерфейса с использованием RecyclerView и ViewHolder
Абстракция: Управление элементами списка

```kotlin
// Абстракция: Элемент списка.
// Абстрагируем данные элемента списка, которые будут отображаться в RecyclerView.
data class ListItem(val id: Int, val title: String)

// Абстракция: Адаптер для RecyclerView.
// Абстрагируем отображение данных списка в пользовательском интерфейсе, используя ViewHolder для управления представлениями элементов.
class ListAdapter(private val items: List<ListItem>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
    }

    override fun getItemCount(): Int = items.size
   
}

```
Его идея о "новом семантическом уровне мышления", на
котором можно быть "абсолютно точным", подразумевает, что разработчики должны стремиться к тому, чтобы их код был не
просто работающим, но и понятным на более высоком уровне абстракции.

Применение идеи Дейкстры в практике программирования

1. Четкие абстракции и интерфейсы
   Определение: Разработчик должен создавать четкие и хорошо определенные интерфейсы и абстракции, которые скрывают
   сложность реализации и предоставляют простые и понятные способы взаимодействия.
   Применение: В практике это означает, что при проектировании системы следует уделять внимание интерфейсам классов и
   методам, обеспечивая, чтобы они отражали ясную и интуитивно понятную модель взаимодействия. Например, при разработке
   библиотеки для работы с базами данных важно четко определить интерфейсы для доступа к данным (DAO), скрывая детали
   реализации от пользователя библиотеки.
2. Модульность и разделение ответственности
   Определение: Система должна быть разделена на независимые модули, каждый из которых отвечает за определенную часть
   функциональности.
   Применение: В Android-разработке это может означать четкое разделение кода на слои: UI, бизнес-логика, работа с
   данными и сетью. Каждый слой должен иметь свои четко определенные обязанности и минимально зависеть от других слоев.
   Например, ViewModel должен управлять состоянием UI и взаимодействовать с репозиториями данных, но не заниматься
   непосредственным взаимодействием с пользовательским интерфейсом.
3. Читаемость и поддерживаемость кода
   Определение: Код должен быть написан таким образом, чтобы он был понятен не только автору, но и другим разработчикам,
   которые могут работать с ним в будущем.
   Применение: Это достигается путем использования понятных имен переменных и методов, написания комментариев и
   документации, а также соблюдения единых стилей кодирования. В Android-разработке это может включать в себя
   документирование API интерфейсов и обеспечение наличия комментариев, объясняющих сложные или неочевидные части кода.
4. Тестируемость
   Определение: Код должен быть написан так, чтобы его можно было легко тестировать, что обеспечивает уверенность в его
   корректности и помогает в будущем при рефакторинге.
   Применение: В практике это означает написание модульных тестов и использование зависимостей, которые могут быть легко
   заменены (например, с использованием инъекции зависимостей). В Android-разработке это может включать написание тестов
   для ViewModel и репозиториев, используя библиотеки вроде JUnit и Mockito для упрощения тестирования.

Таким образом, применение принципов Дейкстры в практике Android-разработки помогает создавать более четкий, понятный
и поддерживаемый код, способствующий эффективной командной работе и долгосрочной поддержке проекта .


## Выводы
