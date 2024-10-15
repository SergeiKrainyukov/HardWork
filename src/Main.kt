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
//    println("Введите значение")
//    val user: String? = readLine()
//    val x: Double = user!!.toDouble()
//    println("Введите значение")
//    val user1: String? = readLine()
//    val y: Double = user1!!.toDouble()
//    println("Введите значение")
//    val user2: String? = readLine()
//    val z: Double = user2!!.toDouble()
//    if (x > y && x > z || x == y && x == z) {
//        println(x)
//    }
//    if (y > x && y > z || y == x && y == z) {
//        println(y)
//    }
//    if (z > x && z > y || z == y && z == y) {
//        println(z)
//    }
    println(-5%2)
}

class TTT (
    val s: String
) {

}