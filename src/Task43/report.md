## Задание 43

Примеры тестов на Kotlin, зависящих от реализации

### Пример 1

### Было

```kotlin
class User(val name: String, val age: Int)

class UserService {
    fun getUserInfo(user: User): String {
        return "${user.name} is ${user.age} years old"
    }
}

class UserServiceTest {
    @Test
    fun testGetUserInfo() {
        val user = User("Alice", 30)
        assertEquals("Alice is 30 years old", UserService().getUserInfo(user))
    }
}
```

### Стало

```kotlin
interface UserRepository {
    fun getUserInfo(userId: Int): User
}

class UserService(private val userRepository: UserRepository) {
    fun getUserInfo(userId: Int): String {
        val user = userRepository.getUserInfo(userId)
        return "${user.name} is ${user.age} years old"
    }
}

class UserServiceTest {
    @Test
    fun testGetUserInfo() {
    // Мокируем репозиторий пользователей
    val mockUserRepository = mock(UserRepository::class.java)
    val user = User("Alice", 30)

        // Настраиваем поведение мока
        `when`(mockUserRepository.getUserInfo(1)).thenReturn(user)

        val userService = UserService(mockUserRepository)
        assertEquals("Alice is 30 years old", userService.getUserInfo(1))
    }
}
```

#### Абстрактный эффект

Тест проверяет форматирование строки с информацией о пользователе, не завися от конкретной реализации репозитория.

#### Явность эффекта

Эффект явно выражен, и изменения в методе getUserInfo не повлияют на тест, если интерфейс останется неизменным.

#### Использование моков

Да, использованы моки для изоляции тестируемого кода от внешних зависимостей.

###  Пример 2

### Было

```kotlin
class Calculator {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}

class CalculatorTest {
    @Test
    fun testAdd() {
        assertEquals(5, Calculator().add(2, 3))
    }
}
```

### Стало

```kotlin
interface Calculator {
    fun add(a: Int, b: Int): Int
}

class SimpleCalculator : Calculator {
    override fun add(a: Int, b: Int): Int {
        return a + b
    }
}

class CalculatorService(private val calculator: Calculator) {
    fun calculateSum(a: Int, b: Int): Int {
        return calculator.add(a, b)
    }
}

class CalculatorServiceTest {
    @Test
    fun testCalculateSum() {
        val mockCalculator = mock(Calculator::class.java)

        // Настраиваем поведение мока
        `when`(mockCalculator.add(2, 3)).thenReturn(5)

        val calculatorService = CalculatorService(mockCalculator)
        assertEquals(5, calculatorService.calculateSum(2, 3))
    }
}
```

#### Абстрактный эффект

Тест проверяет сложение двух чисел через сервис.

#### Явность эффекта

Эффект явно выражен; изменения в реализации метода add не повлияют на тест.

#### Использование моков

Да, использованы моки для обеспечения независимости теста от конкретной реализации калькулятора.

Пример 3

### Было

```kotlin
class Order(val items: List<String>)

class OrderService {
    fun totalItems(order: Order): Int {
        return order.items.size
    }
}

class OrderServiceTest {
    @Test
    fun testTotalItems() {
        val order = Order(listOf("item1", "item2"))
        assertEquals(2, OrderService().totalItems(order))
    }  
}
```

### Стало

```kotlin
interface OrderRepository {
    fun getOrderItems(orderId: Int): List<String>
}

class OrderService(private val orderRepository: OrderRepository) {
    fun totalItems(orderId: Int): Int {
        return orderRepository.getOrderItems(orderId).size
    }
}

class OrderServiceTest {
@Test
fun testTotalItems() {
val mockOrderRepository = mock(OrderRepository::class.java)

        // Настраиваем поведение мока
        `when`(mockOrderRepository.getOrderItems(1)).thenReturn(listOf("item1", "item2"))

        val orderService = OrderService(mockOrderRepository)
        assertEquals(2, orderService.totalItems(1))
    }
}
```

#### Абстрактный эффект

Тест проверяет количество элементов в заказе.

#### Явность эффекта

Эффект явно выражен и не зависит от конкретной реализации репозитория.

#### Использование моков

Да, использованы моки для изоляции теста от внешних зависимостей.

Пример 4: Тестирование метода с прямым обращением к базе данных

### Было

```kotlin
class Database {
    fun fetchUser(id: Int): User? {
        // Логика доступа к базе данных
        return User("Bob", 25)
    }
}

class DatabaseTest {
    @Test
    fun testFetchUser() {
        val user = Database().fetchUser(1)
        assertEquals("Bob", user?.name)
    }
}
```

### Стало

```kotlin
interface Database {
fun fetchUser(id: Int): User?
}

class UserService(private val database: Database) {
fun getUserName(id: Int): String? {
return database.fetchUser(id)?.name
}
}

class UserServiceTest {
@Test
fun testGetUserName() {
val mockDatabase = mock(Database::class.java)

        // Настраиваем поведение мока
        `when`(mockDatabase.fetchUser(1)).thenReturn(User("Bob", 25))

        val userService = UserService(mockDatabase)
        assertEquals("Bob", userService.getUserName(1))
    }
}
```

#### Абстрактный эффект

Тест проверяет получение имени пользователя из базы данных.

#### Явность эффекта

Эффект явно выражен и не зависит от состояния базы данных.

#### Использование моков

Да, использованы моки для изоляции теста от внешних зависимостей.


Пример 5: Тестирование класса с использованием статических методов

### Было

```kotlin
object Utility {
fun formatDate(date: LocalDate): String {
return date.toString()
}
}

class UtilityTest {
@Test
fun testFormatDate() {
val date = LocalDate.of(2024, 10, 19)
assertEquals("2024-10-19", Utility.formatDate(date))
}
}
```

### Стало

```kotlin
interface DateFormatter {
    fun format(date: LocalDate): String
}

class SimpleDateFormatter : DateFormatter {
    override fun format(date: LocalDate): String {
        return date.toString()
    }
}

class DateService(private val dateFormatter: DateFormatter) {
    fun formatDate(date: LocalDate): String {
        return dateFormatter.format(date)
    }
}

class DateServiceTest {
    @Test
    fun testFormatDate() {
        val mockDateFormatter = mock(DateFormatter::class.java)

        // Настраиваем поведение мока
        `when`(mockDateFormatter.format(LocalDate.of(2024, 10, 19))).thenReturn("2024-10-19")

        val dateService = DateService(mockDateFormatter)
        assertEquals("2024-10-19", dateService.formatDate(LocalDate.of(2024, 10, 19)))
    }
}
```

#### Абстрактный эффект

Тест проверяет форматирование даты.

#### Явность эффекта

Эффект явно выражен и не зависит от конкретной реализации форматирования даты.

#### Использование моков

Да, использованы моки для изоляции теста от конкретной реализации форматировщика даты.


### Рефлексия

Всю жизнь использую моки в тестах для проверки определенной логики. Сразу понял, что
это правильный и удобный подход, и очень плохо представляю себе, как вообще можно тестировать
сразу реализацию без моков, считаю это очень странным подходом.
Благодаря занятию появилось формальное представление о том, почему именно надо использовать моки для тестов.
Тесты должны доказывать определенные свойства программы, что позволяет утверждать о ее корректности.
Про использование assert в коде постоянно забываю, никак не введу это в рабочую практику,
но буду стараться.