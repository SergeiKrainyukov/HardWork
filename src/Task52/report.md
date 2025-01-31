## Задание 52

### Пример 1
У нас есть RecyclerView, который отображает список элементов, 
и у каждого элемента есть несколько разных реакций на клики. 
Вместо того чтобы передавать лямбда-функции для обработки кликов, мы можем использовать дефункционализацию.

**Исходный код:**

```kotlin
// Модель данных
data class ProductItem(val id: Int, val name: String, val description: String)

// Адаптер RecyclerView
class ItemAdapter(
    private val items: List<ProductItem>,
    private val onDeleteClick: (ProductItem) -> Unit,
    private val onAddToFavoritesClick: (ProductItem) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //....
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onDeleteClick, onAddToFavoritesClick)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ProductItem, onDeleteClick: (ProductItem) -> Unit, onAddToFavoritesClick: (ProductItem) -> Unit) {
            val deleteButton = itemView.findViewById(R.id.button)
            val addToFavoritesButton = itemView.findViewById(R.id.button)
            deleteButton.setOnClickListener { onDeleteClick(item) }
            addToFavoritesButton.setOnClickListener { onAddToFavoritesClick(item) }
        }
    }
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
        val adapter = ItemAdapter(items, 
            onDeleteClick = { onDelete(it) }, 
            onAddToFavoritesClick = { onAddToFavorites(it)  
        })
        
        //...
    }
    
    private fun onDelete(item: ProductItem) {
        //...
    }
    private fun onAddToFavorites(item: ProductItem) {
        //...
    }
}
```

Если у нас много разных типов действий (например, удаление элемента, редактирование, переход на другой экран), 
передача лямбда-функций может усложнить код. Кроме того, такие функции сложнее тестировать и поддерживать.
Поэтому мы можем заменить функции на данные, например, с помощью sealed class, и создать интерпретатор для обработки действий.

**Исправленный код:**
```kotlin

// Определяем типы действий
sealed class ProductItemAction {
    data class AddToFavorites(val itemId: Int) : ItemAction()
    data class DeleteItem(val itemId: Int) : ItemAction()
}

class ItemAdapter(
    private val items: List<ProductItem>,
    private val onAction: (ProductItemAction) -> Unit // Передаем действие, а не функцию
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onAction)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById(R.id.button)

        fun bind(item: ProductItem, onAction: (ProductItemAction) -> Unit) {
            deleteButton.setOnClickListener { onAction(ProductItemAction.DeleteItem(item.id)) }
            addToFavoritesButton.setOnClickListener { onAction(ProductItemAction.AddToFavorites(item.id)) }
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val adapter = ItemAdapter(items, this::handleItemAction)
        //...
    }

    fun handleItemAction(action: ProductItemAction) {
        when (action) {
            is ProductItemAction.AddToFavorites -> {
                onDelete(action.itemId)
            }
            is ProductItemAction.DeleteItem -> {
                onAddToFavorites(action.itemId)
            }
        }
    }

    private fun onDelete(item: ProductItem) {
        //...
    }
    private fun onAddToFavorites(item: ProductItem) {
        //...
    }
}

```

**Преимущества такого подхода**

* Упрощение кода: Все действия представлены в виде данных, что делает код более структурированным.
* Легкость расширения: Добавление нового действия требует только добавления нового типа в sealed class.
* Тестируемость: Интерпретатор (handleItemAction) можно легко протестировать.
* Универсальность: Один и тот же подход можно использовать для обработки различных типов событий (клики, свайпы, долгие нажатия и т.д.).

### Пример 2


### Выводы
