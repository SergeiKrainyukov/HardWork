## Задание 32

### Пример 1

### Было
```kotlin
fun <A, B> zip(list1: List<A>, list2: List<B>): List<Pair<A, B>> {
    return if (list1.isEmpty() || list2.isEmpty()) {
        emptyList()
    } else {
        listOf(Pair(list1.first(), list2.first())) + zip(list1.drop(1), list2.drop(1))
    }
}

```

### Стало

```kotlin
fun <A, B> zip(list1: List<A>, list2: List<B>): List<Pair<A, B>> {
    // Когда результат будет пуст?
    // Когда хотя бы один из входных списков пуст.
    if (list1.isEmpty() || list2.isEmpty()) return emptyList()

    // Если результат не пуст, то какова его голова?
    // Пара из голов входных списков.
    val head = Pair(list1.first(), list2.first())

    // Из каких данных рекурсивно строится хвост результата?
    // Вызов zipCoRecursive для хвостов входных списков.
    val tail = zip(list1.drop(1), list2.drop(1))

    // Комбинируем голову и хвост, формируя результат
    return listOf(head) + tail
}

```

### Пример 2


### Было
```kotlin
fun selectionSort(list: List<Int>): List<Int> {
    if (list.isEmpty()) return emptyList()
    val min = list.minOrNull() ?: return emptyList()
    return listOf(min) + selectionSort(list.filter { it != min })
}

```

### Стало
```kotlin
fun selectionSortCoRecursive(list: List<Int>): List<Int> {
    // Вопрос 1: Когда результат будет пуст?
    // Ответ: Когда входной список пуст.
    if (list.isEmpty()) return emptyList()

    // Вопрос 2: Если результат не пуст, то какова его голова?
    // Ответ: Минимальное значение из входного списка.
    val min = list.minOrNull() ?: return emptyList()

    // Вопрос 3: Из каких данных рекурсивно строится хвост результата?
    // Ответ: Входной список без минимального элемента.
    val tail = selectionSortCoRecursive(list.filter { it != min })

    // Комбинируем голову и хвост, формируя результат
    return listOf(min) + tail
}

```

### Пример 3

### Было
```kotlin
fun bundleString(input: String, length: Int): List<String> {
    if (input.isEmpty()) return emptyList()
    val bundle = if (input.length <= length) input else input.substring(0, length)
    return listOf(bundle) + bundleString(input.substring(bundle.length), length)
}
```

### Стало
```kotlin
fun bundleStringCoRecursive(input: String, length: Int): List<String> {
    // Вопрос 1: Когда результат будет пуст?
    // Ответ: Когда входная строка пуста.
    if (input.isEmpty()) return emptyList()

    // Вопрос 2: Если результат не пуст, то какова его голова?
    // Ответ: Первая часть входной строки длиной n символов, или вся строка целиком, если её длина меньше или равна n.
    val bundle = if (input.length <= length) input else input.substring(0, length)

    // Вопрос 3: Из каких данных рекурсивно строится хвост результата?
    // Ответ: Входная строка без её первой части из n символов.
    val tail = bundleStringCoRecursive(input.substring(bundle.length), length)

    // Комбинируем голову и хвост, формируя результат
    return listOf(bundle) + tail
}

```

### Выводы
Одно из самых сложный занятий в данном формате. Не смог найти большое количество собственных
примеров, где такой подход был бы уместен, поэтому взял примеры из материала и реализовал
их на языке котлин, попутно закрепив теорию. 