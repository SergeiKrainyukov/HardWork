## Задание 26

Примеры того, когда ошибочно полагался на дефолтную работу.

### Пример 1

Недавно в рабочем проекте столкнулся с ситуацией, когда использовал FragmentResult api для возврата
значения из одного фрагмента в другой совместно с использованием ViewPager2 для отображения
фрагментов во вкладках. Я вешал слушатель событий в методе жц onCreate(), и не знал, что ViewPager
только единожды создает фрагменты и кеширует их, а также я думал, что у каждого фрагмента будет свой
result listener.

```kotlin
 override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    parentFragmentManager.setFragmentResultListener(
        CodesListFragment.FRAGMENT_RESULT_KEY, this
    ) { _, bundle ->
        //...
    }
}
```

Оказалось, что setFragmentResult нужно вешать единожды только на активный фрагмент,
который отображается на экране, поэтому я перенес его в onResume(), чтобы он каждый раз вешался только
при переходе на данный фрагмент.

```kotlin
 override fun onResume() {
        super.onResume()
        //Создаем слушателя только в onResume(), так как ViewPager кеширует фрагменты и не пересоздает их
        parentFragmentManager.setFragmentResultListener(
            CodesListFragment.FRAGMENT_RESULT_KEY, this
        ) { _, bundle ->
            //...
        }
    }
```

Здесь была моя вина, поленился качественно изучить документацию и разобраться в его работе, делал
больше на автомате.

### Пример 2

Когда впервые сталкивался с фреймворком корутин в котлин, не мог понять, почему не отрабатывает
подписка на несколько flow в одном скоупе. Потом все-таки пришлось разобраться поглубже во
внутреннем механизме работы корутины и выяснить, что подписка на flow действует постоянно и не
завершается, поэтому после первой подписки весь дальнейший код не выполнится, и надо запускать новую
корутину.

Так не сработает:
```kotlin
viewModelScope.launch {
    flow1.collectLatest {
        //...
    }
    
    //данная подписка не будет работать, так как корутина будет ждать завершения предыдущей
    flow2.collectLatest {
        //...
    }
}
```

А так сработает:
```kotlin
viewModelScope.launch {
    flow1.collectLatest {
        //...
    }
}
    
    //данная подписка будет работать, так как запущена в отдельной корутине
viewModelScope.launch {
    flow2.collectLatest {
        //...
    }
}
```

В данном случае я также не удосужился достаточно хорошо разобраться в документации, хотя там такой
случай рассматривается, хоть и не в разделе для начинающих.

### Пример 3

В рабочем проекте есть модуль, который предоставляет интерфейс:
```kotlin
interface QuestionnaireInteractor {

    //Создаем запись в t_Requests. Тип запроса - добавление плательщика или точки доставки
    fun createRequestToAddAddress(typeOfAddressId: Long): UUID

    //Создаем запись в t_Requests. Тип запроса - редактирование плательщика или точки доставки
    fun createRequestToEditAddress(addressId: Long): UUID

    fun getGroups(requestUid: UUID): Flow<List<Group>>

    fun validateAnswer(requestUid: UUID, question: Question, value: String?): Boolean

    fun saveAnswer(requestUid: UUID, question: Question, value: String?)

    suspend fun getAnswerCode(id: Long): String

    suspend fun getDescriptionsByTypeOfCodeValueId(id: Long): List<String>

    fun closeRequest(requestUid: UUID)
}
```

Нас интересуют 2 метода - saveAnswer и validateAnswer. Бизнес-логика предполагает, что ответ на
вопрос анкеты должен сначала пройти валидацию на корректное значение, а затем уже быть
сохранен. Однако документации по проекту не ведется и в самом коде нет никаких комментариев
по поводу данной логики. Поэтому я, приступая к реализации UI, сразу пытался сохранить ответ
в бд, что иногда приводило к странным ошибкам. Только после уточнения у ведущего разработчика
я понял, что надо обязательно сначала проверить валидность ответа, и только потом уже
пытаться его сохранить. В данном случае это можно отнести к чужой ошибке, так как у меня не
было никаких сведений о том, в каком порядке я должен вызывать данные функции.