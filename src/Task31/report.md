## Задание 31

### Пример 1

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
        view.findViewById<TextView>(R.id.task_name_tv).text = stage.name
        view.findViewById<TextView>(R.id.description_tv).text =
            Html.fromHtml(stage.description, Html.FROM_HTML_MODE_COMPACT)
        view.findViewById<TextView>(R.id.created_date_tv).text = stage.date
        view.findViewById<TextView>(R.id.status_tv).text = stage.status
    }
}
```

### Пример 4
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
