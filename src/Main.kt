import java.util.Scanner

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
//    val printer = Printer()
//
//    printer.print() // Вывод: Printing...
//    printer.setColor("Red") // Вывод: Setting color to Red
    val scanner = Scanner(System.`in`)
    val a = scanner.nextInt()
    val b = scanner.nextInt()
    print(a+b)
}
