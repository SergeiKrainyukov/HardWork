## Задание 31

### Пример 1

Для того, чтобы при обновлении элементов списка на ui перерисовывались только те, которые действительно
изменились, был придуман класс DiffUtil.ItemCallback, который нужно каждый раз переопределять собственным
значением. Однако, практически всегда используется абсолютно одинаковая реализация методов этого класса,
что приводит фактически к дублированию кода. Поэтому в исправленной версии был создан класс ListItem и 
базовая реализация DiffUtil.ItemCallback с его использованием, что позволило сделать код более полиморфным
и избавиться от бесполезного переопределения методов для каждой новой модели.

### Было
```kotlin
class TaskItemDiffCallback : DiffUtil.ItemCallback<StageListItem>() {
    override fun areItemsTheSame(
        oldItem: StageListItem,
        newItem: StageListItem,
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: StageListItem,
        newItem: StageListItem,
    ) = oldItem == newItem
}

class EventItemDiffCallback : DiffUtil.ItemCallback<EventListItem>() {
    override fun areItemsTheSame(
        oldItem: EventListItem,
        newItem: EventListItem,
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: EventListItem,
        newItem: EventListItem,
    ) = oldItem == newItem
}

class AdaptPlanItemDiffCallback : DiffUtil.ItemCallback<AdaptPlanListItem>() {
    override fun areItemsTheSame(
        oldItem: AdaptPlanListItem,
        newItem: AdaptPlanListItem,
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: AdaptPlanListItem,
        newItem: AdaptPlanListItem,
    ) = oldItem == newItem
}
```

### Стало

```kotlin
class DiffCallback<T> : DiffUtil.ItemCallback<ListItem<T>>() {
    override fun areItemsTheSame(
        oldItem: ListItem<T>,
        newItem: ListItem<T>,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ListItem<T>,
        newItem: ListItem<T>,
    ): Boolean {
        return oldItem.contentEquals(newItem)
    }
}

interface ListItem<T> {
    val id: Int
    val content: T

    fun contentEquals(item: ListItem<T>): Boolean
}

data class AdaptPlanListItem(
    override val id: Int,
    //другие поля...
) : ListItem<AdaptPlanListItem> {
    
    override fun contentEquals(item: ListItem<AdaptPlanListItem>) = this == item

    override val content: AdaptPlanListItem
        get() = this
    
}
```

### Пример 2

В приложении было много экранов, которые просто делают запрос на сервер и отображают список каких-то данных.
Каждый раз создавался отдельный useCase для каждого сценария, и каждый раз во вью модели вызывался
один и тот же код получения данных. В исправленном варианте была создана базовая модель, содержащая всю
данную логику получения данных и некоторый базовый use case обобщенного типа. Таким образом, конкретные
вью модели теперь содержат только уникальную логику.

### Было
```kotlin
class GetAdaptPlansUseCase
@Inject
constructor(
    private val plansRepository: PlansRepository,
) {
    suspend operator fun invoke() = plansRepository.getAdaptPlans()
}

class AdaptPlansFragmentViewModel
@Inject
constructor(
    private val useCase: UseCase<AdaptPlan>,
) : ViewModel() {
    //...
}
```

### Стало
```kotlin
class GetAdaptPlansUseCase
@Inject
constructor(
    private val plansRepository: PlansRepository,
) : UseCase<AdaptPlan> {
    override suspend operator fun invoke() = plansRepository.getAdaptPlans()
}

interface UseCase<T> {
    suspend fun invoke(): List<T>
}

class AdaptPlansFragmentViewModel
@Inject
constructor(
    useCase: UseCase<AdaptPlan>,
) : BaseViewModel<AdaptPlan>(useCase) {
    val adaptListState = state.map { it.map { AdaptPlanListItem.fromModel(it) } }
}

open class BaseViewModel<T>(val useCase: UseCase<T>) : ViewModel() {
    private var _state = MutableSharedFlow<List<T>>()
    val state: SharedFlow<List<T>>
        get() = _state.asSharedFlow()

    fun init() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }) {
            val entities = useCase.invoke()
            _state.emit(entities)
        }
    }
}

```

### Пример 3

В приложении часто встречаются списки, в которых есть логика клика на какой-то элемент списка и переход
на другой экран с описанием этого элемента. Чтобы каждый раз не вешать слушатель на элемент списка,
был создан специальный класс ClickableViewHolder, который уже содержит нужную логику, ему только
надо передать слушатель.

### Было
```kotlin
class StagesListAdapter : ListAdapter<StageListItem, StageViewHolder>(TaskItemDiffCallback()) {
    var onClickStage: ((StageListItem) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stage, parent, false)
        return StageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StageViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
        holder.itemView.setOnClickListener {
            onClickStage?.invoke(currentList[position])
        }
    }
}
```

### Стало
```kotlin
class StagesListAdapter(
    private val onClickStage: (StageListItem) -> Unit
) : ListAdapter<StageListItem, StageViewHolder>(TaskItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stage, parent, false)
        return StageViewHolder(view, onClickStage)
    }

    override fun onBindViewHolder(
        holder: StageViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }
}


open class ClickableViewHolder<T>(
    val view: View,
    val onClick: (T) -> Unit,
) : RecyclerView.ViewHolder(view) {
    open fun bind(item: T) {
        view.setOnClickListener { onClick.invoke(item) }
    }
}

class StageViewHolder(view: View, onclick: (StageListItem) -> Unit) :
    ClickableViewHolder<StageListItem>(view, onclick) {
    override fun bind(stage: StageListItem) {
        super.bind(stage)
        //...
    }
}
```

### Пример 4

В приложении множество раз в разных местах надо было создавать объект Gson() и вызывать у него
специальные методы для преобразования в JSON-строку и обратно. В новой версии создал полиморфную
функцию, которая упрощает работу с этим и инкапсулирует все необходимое.

### Было
```kotlin
val document = Gson().fromJson(requestWrapper.data, DocumentDto::class.java)
```

### Стало
```kotlin
inline fun <reified T> fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

inline fun <reified T> toJson(t: T): String {
    return Gson().toJson(t, T::class.java)
}

val document = fromJson<DocumentDto>(requestWrapper.data)
```

### Пример 5

В приложении было множество экранов, которые подписываются на события вью модели абсолютно одинаковым
образом, однако на каждом экране надо было писать один и тот же код подписки для каждого сценария.
В новой версии создал полиморфную функцию высшего порядка, которая инкапсулирует логику подписки и
предоставляет удобный интерфейс для пользователя. Логика подписки сократилась до одной строчки.

### Было
```kotlin
private fun bindViewModel() {
    lifecycleScope.launch {
        viewModel.stageDataState.flowWithLifecycle(
            lifecycle,
            Lifecycle.State.RESUMED,
        ).collectLatest {
            it?.let(::setViews)
        }
    }
}
```

### Стало
```kotlin
fun <T> Fragment.collectFlow(
    flow: Flow<T>,
    action: (T) -> Unit,
) {
    lifecycleScope.launch {
        flow.flowWithLifecycle(
            lifecycle,
            Lifecycle.State.RESUMED,
        ).collectLatest {
            action(it)
        }
    }
}

private fun bindViewModel() {
    collectFlow(viewModel.stageDataState.filterNotNull(), ::setViews)
}
```

### Выводы
Начал гораздо чаще видеть места в коде, которые дублируют уже имеющуюся логику, которую можно просто
сделать полиморфной. Раньше очень боялся использовать механизмы дженериков, это казалось очень
трудным для понимания, а теперь, когда потренировался, стало значительно проще, и главное, ощутил
преимущество в виде чистого элегантного кода. В некоторых случаях действительно ощутил, что полиморфный
код сокращает пространство возможных реализаций, по сравнению с мономорфным, и это заставило более
ответственно подходить к проектированию архитектуры приложения.