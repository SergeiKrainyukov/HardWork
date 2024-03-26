## Задание 26

### Примеры того, когда ошибочно полагался на дефолтную работу:

### Пример 1

Недавно в рабочем проекте столкнулся с ситуацией, когда использовал FragmentResult api для возврата
значения из одного фрагмента в другой совместно с использованием ViewPager2 для отображения
фрагментов во вкладках. Я вешал слушатель событий в методе жизненного цикла onCreate(), и не знал, что ViewPager
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

Здесь была моя вина, поленился качественно изучить документацию и разобраться в его работе, делал скорее на автомате.

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
случай рассматривается, но не в разделе для начинающих.

### Пример 3

В рабочем проекте есть модуль, который предоставляет интерфейс:
```kotlin
interface QuestionnaireInteractor {

    fun validateAnswer(requestUid: UUID, question: Question, value: String?): Boolean

    fun saveAnswer(requestUid: UUID, question: Question, value: String?)
    
    //Другие методы
}
```

Нас интересуют 2 метода - saveAnswer и validateAnswer. Бизнес-логика предполагает, что ответ на
вопрос анкеты должен сначала пройти валидацию на корректное значение, а затем уже быть
сохранен. Однако документации по проекту не ведется и в самом коде нет никаких комментариев
по поводу данной логики. Поэтому я, приступая к реализации UI, сразу пытался сохранить ответ
в бд, что иногда приводило к некорректному поведению. Только после уточнения у ведущего разработчика
я понял, что надо обязательно сначала проверить валидность ответа, и только потом уже
пытаться его сохранить. В данном случае это можно отнести к чужой ошибке, так как у меня не
было никаких сведений о том, в каком порядке я должен вызывать данные функции.

### Исправления:

### Пример 1

Есть метод, который получает данные по нескольким категориям продуктов и затем присваивает их соответствующим
ui-компонентам. Каждый продукт в категории имеет определенный статус, который задан строкой. В исходной версии
кода мы проверяем строки на соответствие заданным, и выполняем далее какую-то логику. Это плохой подход,
так как при добавлении какого-то нового статуса мы никогда об этом не узнаем, а даже если узнаем и добавим новый
if, то есть вероятность опечатки, из-за чего код станет работать некорректно во время выполнения. Поэтому в данной версии
кода осуществляется проверка try-catch "на всякий случай", если что-то пойдет не так. Поэтому имеет
смысл изменить тип статуса продукта на строго определенный и ограниченный по возможным вариантам, для чего отлично
подходит enum.

Было:

```kotlin

class Product(
    var status: String?,
    //другие поля
)

class Category(val name: String, val productList: List<Product>)

 private fun setUniqueOffersButtons(view: View) {
        val categories: List<Category> = CategoriesKeeper.getInstance().getCategories()
        val promotionProducts: MutableList<Product> = ArrayList<Product>()
        val salesProducts: MutableList<Product> = ArrayList<Product>()
        val newProducts: MutableList<Product> = ArrayList<Product>()
        for (category in categories) {
            for (product in category.getProductList()) {
                try {
                    when (product.status) {
                        "Акции и предложения" -> promotionProducts.add(product)
                        "Распродажи" -> salesProducts.add(product)
                        "Новинки" -> newProducts.add(product)
                    }
                } catch (e: Exception) {
                    System.out.println(("Product " + product.id) + " has called problem")
                }
            }
        }
        view.findViewById<View>(R.id.promotions)
            .setOnClickListener { view1: View? ->
                createUniqueOfferFragment(
                    promotionProducts,
                    "Акции и предложения",
                )
            }
        view.findViewById<View>(R.id.new_products)
            .setOnClickListener { view1: View? ->
                createUniqueOfferFragment(
                    newProducts,
                    "Новинки",
                )
            }
        view.findViewById<View>(R.id.sales_products)
            .setOnClickListener { view1: View? ->
                createUniqueOfferFragment(
                    salesProducts,
                    "Распродажи",
                )
            }
    }
```

Стало:

```kotlin
private fun setUniqueOffersButtons(
        view: View,
        viewTypes: List<ViewType>,
    ) {
        viewTypes.forEach { viewType ->
            view.findViewById<View>(viewType.viewId)
                .setOnClickListener {
                    createUniqueOfferFragment(
                        CategoriesKeeper.getCategories().flatMap { it.productList.filter { it.status == viewType.status } },
                        viewType.status.statusName,
                    )
                }
        }
    }

class Product(
    var status: Status,
    //другие поля
)

enum class Status(val statusName: String) {
    PROMOTIONS("Акции и предложения"),
    NOVELTIES("Новинки"),
    SALES("Распродажи"),
}

data class ViewType(
    val status: Status,
    @IdRes val viewId: Int,
)

class Category(val name: String, val productList: List<Product>)
```

Благодаря данному изменению, во-первых, код становится переиспользуемым, потому что мы точно знаем, какие статусы
могут быть у продукта, а во-вторых более лаконичным, так как теперь нужные категории продуктов можно очень удобно 
фильтровать по статусу не боясь ошибиться при сравнении конкретных строк. Никакие проверки if-else, switch-case и 
try-catch становятся не нужны.

### Пример 2

В Андроид в классе Fragment есть метод жизненного цикла onCreateView(), где создается родительский контейнер, в который будут
вложены другие виджеты и элементы UI. При программном создании элемента можно явно создать объект, который будет
использован в качестве корневого контейнера. В исходной версии кода так и просходит, однако затем в других методах
чтобы вложить что-то в родительский контейнер, нужно делать явный каст, который может быть небезопасным, и к тому
же мы каждый раз должны предполагать, что родительским является именно тот контейнер, который нам нужен.
В таком случае создать отдельное поле для хранения данного контейнера и обращаться непосредственно к нему в других
методах.

Было:

```kotlin
class QuestionsListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ScrollView(requireContext())
}

private fun bindViewModel() {
    
    //получение корневой view
    (requireView() as? ScrollView)?.apply {
        addView(layout)
    }
        
}
```

Стало:

```kotlin
class QuestionsListFragment : Fragment() {

    lateinit var rootContainer: ScrollView
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ScrollView(requireContext()).apply { rootContainer = this }
}

private fun bindViewModel() {

    //получение корневой view
    rootContainer.addView(layout)
    

}
```

Код стал лаконичнее, и были убраны лишние проверки, мы теперь точно знаем какой тип у родительского контейнера.

### Пример 3

Рассмотрим, как можно исправить класс из последнего примера предыдущего блока. В исходной версии есть несколько
минусов:

Во-первых, неочевидно, что нужно вызывать сначала validateAnswer(), а затем saveAnswer(). 

Во-вторых, о результате выполнения saveAnswer() мы можем только догадываться или надеяться, что он выполнится успешно.

Также оба метода имеют одинаковую сигнатуру, что косвенно намекает нам о том, что можно было бы их объединить.

Было:

```kotlin
interface QuestionnaireInteractor {

    fun validateAnswer(requestUid: UUID, question: Question, value: String?): Boolean

    fun saveAnswer(requestUid: UUID, question: Question, value: String?)
    
    //Другие методы
}
```

Стало:

```kotlin
interface QuestionnaireInteractor {

    fun saveAnswer(requestUid: UUID, question: Question, value: String?): Flow<Result>
    
    //Другие методы
}

sealed class Result {
    data object Success : Result()
    class Error(error: Throwable): Result()
}
```

В результирующей версии кода оба метода объединены в один, который возвращает строго определенный тип результата,
либо Success, либо Error с описанием ошибки. Таким образом, мы сократили количество возможных состояний до двух, и 
нам не нужно предполагать или догадываться о том, что же произошло.

### Выводы

