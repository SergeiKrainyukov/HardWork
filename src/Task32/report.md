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
fun calculateInputs(groups: List<Group>, maxQuestionsCount: Int) = groups.flatMap { group ->
    when {
        !group.isSplittable || group.sections.size <= maxQuestionsCount + 2 -> listOf(group)
        else -> group.sections.chunked(maxQuestionsCount)
            .fold(emptyList<List<Section>>()) { accumulator, list ->
                when {
                    list.size <= 2 -> accumulator.dropLast(1) + listOf(accumulator.last() + list)
                    else -> accumulator + listOf(list)
                }
            }.map { group.copy(sections = it) }
    }
}

```

### Стало
```kotlin
 fun calculateInputs(groups: List<Group>, maxQuestionsCount: Int): List<Group> {
    // Строим выходной список, обрабатывая каждую группу
    return buildOutput(groups, maxQuestionsCount)
}

// Функция, которая обрабатывает отдельную группу и возвращает список групп
fun processGroup(group: Group, maxQuestionsCount: Int): List<Group> {
    return when {
        // Если группа неразделима или количество секций меньше или равно допустимому максимуму + 2
        !group.isSplittable || group.sections.size <= maxQuestionsCount + 2 -> listOf(group)
        else -> {
            // Разбиваем секции группы на подсписки, каждый из которых имеет размер не более maxQuestionsCount
            val chunkedSections = group.sections.chunked(maxQuestionsCount)

            // Результирующий список списков секций
            val foldedSections = chunkedSections.fold(emptyList<List<Section>>()) { accumulator, list ->
                when {
                    list.size <= 2 -> accumulator.dropLast(1) + listOf(accumulator.last() + list)
                    else -> accumulator + listOf(list)
                }
            }

            // Создаем новые группы с обновленными секциями
            foldedSections.map { group.copy(sections = it) }
        }
    }
}

// Функция, строящая выходной список групп
fun buildOutput(groups: List<Group>, maxQuestionsCount: Int): List<Group> {
    // Когда выходной список пуст
    if (groups.isEmpty()) return emptyList()

    // Берем первую группу и обрабатываем ее
    val firstGroup = groups.first()
    val processedGroups = processGroup(firstGroup, maxQuestionsCount)

    // Рекурсивно строим оставшуюся часть выходного списка
    return processedGroups + buildOutput(groups.drop(1), maxQuestionsCount)
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
Одно из самых сложных занятий в данном формате. Не смог найти большое количество собственных
примеров, где такой подход был бы уместен, поэтому взял примеры из материала и реализовал
их на языке котлин, попутно закрепив теорию. В каждом из приведенных примеров я пытался 
анализировать, как должен выглядеть результат функции на основе входных данных, с помощью вопросов.
В простых примерах код становился проще для понимания, но в более сложных уже не совсем понятно, как лучше.
Пример 2 взят из рабочего проекта, переписанный код в данном случае получился сильно объемнее, и не сильно
проще, возможно просто ко-рекурсивный подход тут не очень уместен, либо я не до конца понял его смысл.

В любом случае, ранее не знал, что существуют разные подходы к написанию рекурсивных программ, и в будущем
при столкновении со сложными рекурсивными типами данных буду стараться применять данную методологию.