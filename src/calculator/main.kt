package calculator

fun main() {
    println(calculate("BYTE", "BIT", 1.0))
    println(calculate("KILOBYTE", "BIT", 1.0))
    println(calculate("BIT", "BYTE", 2048.0))

//    println(calculateV2("BYTE", "BIT", 1.0))
//    println(calculateV2("KILOBYTE", "BIT", 1.0))
//    println(calculateV2("BIT", "BYTE", 2048.0))
}

class Calculator {
    fun calculate(typeFrom: String, typeTo: String, valueFrom: Double): Double {
        val entryTypeFrom = TYPE.valueOf(typeFrom)
        val entryTypeTo = TYPE.valueOf(typeTo)

        val realTypeFrom = RealType(
            value = valueFrom,
            type = entryTypeFrom
        )

        var realTypeTo = realTypeFrom

        return if (entryTypeFrom.ordinal < entryTypeTo.ordinal) {
            repeat(entryTypeTo.ordinal - entryTypeFrom.ordinal) {
                realTypeTo = realTypeTo.next()
            }
            realTypeTo.value
        } else {
            repeat(entryTypeFrom.ordinal - entryTypeTo.ordinal) {
                realTypeTo = realTypeTo.previous()
            }
            realTypeTo.value
        }
    }
}

enum class TYPE(val unit: Int) {
    BIT(8),
    BYTE(1024),
    KILOBYTE(1024),
    MEGABYTE(1024)
}

class RealType(
    val value: Double,
    val type: TYPE
) {
    fun next(): RealType {
        if (type.ordinal == TYPE.values().lastIndex) return this
        val nextEntry = TYPE.values()[type.ordinal + 1]
        return RealType(
            value = value / nextEntry.unit,
            type = nextEntry
        )
    }

    fun previous(): RealType {
        if (type.ordinal == 0) return this
        val previousEntry = TYPE.values()[type.ordinal - 1]
        return RealType(
            value = value * previousEntry.unit,
            type = previousEntry
        )
    }
}

//fun calculateV2(typeFrom: String, typeTo: String, valueFrom: Double) =
//    calculateRec(
//        realType = RealType(
//            value = valueFrom,
//            type = TYPE.valueOf(typeFrom)
//        ),
//        typeTo = TYPE.valueOf(typeTo)
//    ).value
//
//fun calculateRec(realType: RealType, typeTo: TYPE): RealType = when {
//    (realType.type == typeTo) -> realType
//    (realType.type.ordinal < typeTo.ordinal) -> calculateRec(realType.next(), typeTo)
//    (realType.type.ordinal > typeTo.ordinal) -> calculateRec(realType.previous(), typeTo)
//    else -> realType
//}