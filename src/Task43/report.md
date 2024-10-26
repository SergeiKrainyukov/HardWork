## Задание 43

Примеры тестов на Kotlin, зависящих от реализации

### Пример 1

### Было

```kotlin
interface WeatherService {
    fun getTemperature(city: String): Double
}

class WeatherAppTest {

    private val weatherService: WeatherService = DefaultWeatherService()
    private val weatherApp = WeatherApp(weatherService)

    @Test
    fun testDisplayTemperatureForLondon() {
        val result = weatherApp.displayTemperature("London")
        assertEquals("The temperature in London is 15.0°C", result)
    }

    @Test
    fun testDisplayTemperatureForParis() {
        val result = weatherApp.displayTemperature("Paris")
        assertEquals("The temperature in Paris is 20.0°C", result)
    }

    @Test
    fun testDisplayTemperatureForNewYork() {
        val result = weatherApp.displayTemperature("New York")
        assertEquals("The temperature in New York is 25.0°C", result)
    }
}
```

### Стало

```kotlin
interface WeatherService {
    fun getTemperature(city: String): Double
}

class WeatherApp(private val weatherService: WeatherService) {
    fun displayTemperature(city: String): String {
        val temp = weatherService.getTemperature(city)
        return "The temperature in $city is $temp°C"
    }
}

class WeatherAppTest {
    private val weatherServiceMock: WeatherService = mock(WeatherService::class.java)
    private val weatherApp = WeatherApp(weatherServiceMock)

    @Test
    fun testDisplayTemperatureLondon() {
        `when`(weatherServiceMock.getTemperature("London")).thenReturn(15.0)

        val result = weatherApp.displayTemperature("London")
        assertEquals("The temperature in London is 15.0°C", result)
    }
    
    @Test
    fun testDisplayTemperatureParis() {
        `when`(weatherServiceMock.getTemperature("Paris")).thenReturn(20.0)

        val resultParis = weatherApp.displayTemperature("Paris")
        assertEquals("The temperature in Paris is 20.0°C", resultParis)
    }
    
    
}
```

#### Абстрактный эффект

При вызове displayTemperature() должен вызываться сервис WeatherService и возвращать корректное значение независимо от реализации

#### Явность эффекта

Эффект явно выражен, и изменения в методе getTemperature() не повлияют на тест, если интерфейс останется неизменным.

#### Использование моков

Да, использованы моки для изоляции тестируемого кода от внешних зависимостей.

###  Пример 2

### Было

```kotlin
interface PaymentProcessor {
    fun processPayment(amount: Double): String
}

class CheckoutTest {

    private val paymentProcessor: PaymentProcessor = DefaultPaymentProcessor()
    private val checkout = Checkout(paymentProcessor)

    @Test
    fun testCheckoutSuccessfulPayment() {
        val result = checkout.checkout(100.0)
        assertEquals("Payment of $100.0 processed successfully.", result)
    }

    @Test
    fun testCheckoutZeroPayment() {
        val result = checkout.checkout(0.0)
        assertEquals("Invalid payment amount.", result)
    }

    @Test
    fun testCheckoutNegativePayment() {
        val result = checkout.checkout(-50.0)
        assertEquals("Invalid payment amount.", result)
    }
}
```

### Стало

```kotlin
interface PaymentProcessor {
    fun processPayment(amount: Double): String
}

class Checkout(private val paymentProcessor: PaymentProcessor) {
    fun checkout(amount: Double): String {
        return paymentProcessor.processPayment(amount)
    }
}

class CheckoutTest {
    private val paymentProcessorMock: PaymentProcessor = mock(PaymentProcessor::class.java)
    private val checkout = Checkout(paymentProcessorMock)

    @Test
    fun testCheckout() {
        `when`(paymentProcessorMock.processPayment(100.0)).thenReturn("Payment Successful")

        val result = checkout.checkout(100.0)
        assertEquals("Payment Successful", result)
    }
}
```

#### Абстрактный эффект

При вызове processPayment() должен вызываться PaymentProcessor и возвращать корректное значение независимо от реализации

#### Явность эффекта

Эффект явно выражен; изменения в реализации метода processPayment не повлияют на тест.

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