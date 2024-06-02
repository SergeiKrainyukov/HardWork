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