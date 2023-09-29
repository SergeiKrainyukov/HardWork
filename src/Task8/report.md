# Задание 8

## Решение

В Котлин есть очень удобный механизм делегации, что по своей сути является поддержкой
миксинов. Рассмотрим на следующем примере.

```kotlin
// Миксин с базовым поведением
interface Printable {
    fun print()
}

// Реализация базового поведения
class BasicPrintable : Printable {
    override fun print() {
        println("Printing...")
    }
}

// Миксин с дополнительным поведением
interface Colorable {
    fun setColor(color: String)
}

// Реализация дополнительного поведения
class BasicColorable : Colorable {
    override fun setColor(color: String) {
        println("Setting color to $color")
    }
}

// Класс, который использует миксины
class Printer : Printable by BasicPrintable(), Colorable by BasicColorable()

fun main() {
    val printer = Printer()

    printer.print() // Вывод: Printing...
    printer.setColor("Red") // Вывод: Setting color to Red
}
```

В данном примере класс Printer реализует два интерфейса Printable и Colorable с помощью делегирования. 
Базовое поведение для каждого интерфейса реализовано в отдельных классах BasicPrintable и BasicColorable. 
Класс Printer использует эти классы для добавления соответствующего поведения.

Таким образом, мы получаем своеобразную замену множественному наследованию, о чем говорилось в статье.


## Резюме и рефлексия по теме

Котлин предоставляет довольно элегантный способ реализации паттерна Посетитель, в отличие от Java,
где такой поддержки не имеется. Ранее делегатами в реальной практике пользовался нечасто, и особенно
в данном ключе никогда их не рассматривал. Буду теперь пробовать применять в своей работе для подходящих
задач.