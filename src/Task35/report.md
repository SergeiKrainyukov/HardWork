## Задание 35

Приведем примеры абстракций из практики Android-разработки. Эти примеры будут включать основные аспекты разработки
Android-приложений, такие как работа с базами данных, управление жизненным циклом, взаимодействие с сетевыми сервисами и
обработка пользовательского интерфейса.

Пример 1: Работа с Room (библиотека для работы с базами данных)
Абстракция: Объект доступа к данным (DAO)

```kotlin

// Entity class representing a table in the database
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val age: Int
)


// Абстракция: Таблица базы данных с полями id, name и age.


// Data Access Object (DAO) interface
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?
}


// Абстракция: Интерфейс доступа к данным.
// Этот код представляет абстракцию взаимодействия с таблицей базы данных, предоставляя методы для вставки и извлечения данных.


// Database class
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}


// Абстракция: База данных.
// Этот код создает абстракцию базы данных, включая определение DAO и управление версиями базы данных.

```

Пример 2: Работа с ViewModel и LiveData
Абстракция: Управление состоянием UI с помощью ViewModel и LiveData

```kotlin
// ViewModel class
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao: UserDao = AppDatabase.getDatabase(application).userDao()
    val user: LiveData<User> = liveData {
        emit(userDao.getUserById(1) ?: User(0, "Unknown", 0))
    }


// Абстракция: Управление состоянием UI.
// Этот код абстрагирует бизнес-логику и хранение данных от пользовательского интерфейса, обеспечивая реактивное обновление UI.

}

// Activity class
class UserActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.user.observe(this, Observer { user ->
            // Update UI with user data
            findViewById<TextView>(R.id.userName).text = user.name
            findViewById<TextView>(R.id.userAge).text = user.age.toString()
        })


// Абстракция: Наблюдение за изменениями данных.
// Этот код абстрагирует обновление пользовательского интерфейса при изменении данных, используя LiveData.

    }
}

```

Пример 3: Взаимодействие с сетевыми сервисами с использованием Retrofit
Абстракция: Работа с сетевыми запросами

```kotlin
// Data class representing the response from the network
data class UserResponse(val id: Int, val name: String, val age: Int)


// Абстракция: Модель данных сетевого ответа.
// Этот код абстрагирует сетевой ответ в виде объекта данных, который можно использовать в приложении.


// Retrofit service interface
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserResponse
}


// Абстракция: Сетевой сервис.
// Этот код абстрагирует взаимодействие с сетевыми API, предоставляя методы для выполнения HTTP-запросов.


// Repository class
class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(id: Int): UserResponse {
        return apiService.getUser(id)
    }


// Абстракция: Репозиторий данных.
// Этот код абстрагирует источник данных, объединяя локальные и удаленные данные.
}

```

Пример 4: Управление жизненным циклом Activity
Абстракция: Управление жизненным циклом и состоянием Activity

```kotlin

// BaseActivity class to handle common functionality
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView()


// Абстракция: Общий функционал Activity.
// Этот код абстрагирует общие операции, такие как настройка макета и инициализация представлений, которые могут использоваться в наследниках.

}

// Specific Activity class
class MainActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        // Initialize views specific to this Activity
    }
}

```

Пример 5: Обработка событий пользовательского интерфейса с использованием RecyclerView и ViewHolder
Абстракция: Управление элементами списка

```kotlin
// Data class representing an item in the list
data class ListItem(val id: Int, val title: String)


// Абстракция: Элемент списка.
// Этот код абстрагирует данные элемента списка, которые будут отображаться в RecyclerView.


// Adapter class for RecyclerView
class ListAdapter(private val items: List<ListItem>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // ViewHolder class
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
    }

    override fun getItemCount(): Int = items.size


// Абстракция: Адаптер для RecyclerView.
// Этот код абстрагирует отображение данных списка в пользовательском интерфейсе, используя ViewHolder для управления представлениями элементов.

}

```

Эти примеры демонстрируют различные абстракции, которые помогают управлять сложностью кода в Android-приложениях, делая
его более понятным и поддерживаемым.

Напишите, как вы понимаете определение Дейкстры применительно к своей практике, как сможете его применять.
ChatGPT
Эдсгер Дейкстра был известен своим вкладом в развитие теории программирования, особенно его акцентом на важности
структурированного и систематического подхода к написанию кода. Его идея о "новом семантическом уровне мышления", на
котором можно быть "абсолютно точным", подразумевает, что разработчики должны стремиться к тому, чтобы их код был не
просто работающим, но и понятным на более высоком уровне абстракции.

Применение идеи Дейкстры в практике программирования

1. Четкие абстракции и интерфейсы
   Определение: Разработчик должен создавать четкие и хорошо определенные интерфейсы и абстракции, которые скрывают
   сложность реализации и предоставляют простые и понятные способы взаимодействия.
   Применение: В практике это означает, что при проектировании системы следует уделять внимание интерфейсам классов и
   методам, обеспечивая, чтобы они отражали ясную и интуитивно понятную модель взаимодействия. Например, при разработке
   библиотеки для работы с базами данных важно четко определить интерфейсы для доступа к данным (DAO), скрывая детали
   реализации от пользователя библиотеки.
2. Модульность и разделение ответственности
   Определение: Система должна быть разделена на независимые модули, каждый из которых отвечает за определенную часть
   функциональности.
   Применение: В Android-разработке это может означать четкое разделение кода на слои: UI, бизнес-логика, работа с
   данными и сетью. Каждый слой должен иметь свои четко определенные обязанности и минимально зависеть от других слоев.
   Например, ViewModel должен управлять состоянием UI и взаимодействовать с репозиториями данных, но не заниматься
   непосредственным взаимодействием с пользовательским интерфейсом.
3. Читаемость и поддерживаемость кода
   Определение: Код должен быть написан таким образом, чтобы он был понятен не только автору, но и другим разработчикам,
   которые могут работать с ним в будущем.
   Применение: Это достигается путем использования понятных имен переменных и методов, написания комментариев и
   документации, а также соблюдения единых стилей кодирования. В Android-разработке это может включать в себя
   документирование API интерфейсов и обеспечение наличия комментариев, объясняющих сложные или неочевидные части кода.
4. Тестируемость
   Определение: Код должен быть написан так, чтобы его можно было легко тестировать, что обеспечивает уверенность в его
   корректности и помогает в будущем при рефакторинге.
   Применение: В практике это означает написание модульных тестов и использование зависимостей, которые могут быть легко
   заменены (например, с использованием инъекции зависимостей). В Android-разработке это может включать написание тестов
   для ViewModel и репозиториев, используя библиотеки вроде JUnit и Mockito для упрощения тестирования.
   Примеры применения в практике


1. Четкие абстракции и интерфейсы

```kotlin
   interface UserRepository {
    suspend fun getUser(id: Int): User
    suspend fun saveUser(user: User)
}

// Реализация UserRepository для работы с локальной базой данных
class LocalUserRepository(private val userDao: UserDao) : UserRepository {
    override suspend fun getUser(id: Int): User {
        return userDao.getUserById(id)
    }

    override suspend fun saveUser(user: User) {
        userDao.insert(user)
    }
}

// Реализация UserRepository для работы с сетевым API
class RemoteUserRepository(private val apiService: ApiService) : UserRepository {
    override suspend fun getUser(id: Int): User {
        val userResponse = apiService.getUser(id)
        return User(userResponse.id, userResponse.name, userResponse.age)
    }

    override suspend fun saveUser(user: User) {
        // Not implemented for remote repository
    }
}

```

2. Модульность и разделение ответственности

```kotlin
   // ViewModel для управления состоянием UI
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val user: LiveData<User> = liveData {
        val user = userRepository.getUser(1)
        emit(user)
    }
}

// Activity для отображения данных пользователя
class UserActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.user.observe(this, Observer { user ->
            findViewById<TextView>(R.id.userName).text = user.name
            findViewById<TextView>(R.id.userAge).text = user.age.toString()
        })
    }

}

```

3. Читаемость и поддерживаемость кода

```kotlin
// Комментарии и документация для методов

/**

 * Репозиторий для работы с пользователями.
 */
interface UserRepository {
    /**
     * Получить пользователя по ID.
     * @param id ID пользователя
     * @return Пользователь с указанным ID
     */
    suspend fun getUser(id: Int): User

    /**
     * Сохранить пользователя.
     * @param user Пользователь для сохранения
     */
    suspend fun saveUser(user: User)
}
```

4. Тестируемость

```kotlin

// Пример теста для ViewModel
class UserViewModelTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        userViewModel = UserViewModel(userRepository)
    }

    @Test
    fun testGetUser() = runBlocking {
        val user = User(1, "John Doe", 30)
        `when`(userRepository.getUser(1)).thenReturn(user)

        val liveData = userViewModel.user
        val observer = mock(Observer::class.java) as Observer<User>
        liveData.observeForever(observer)

        verify(observer).onChanged(user)
    }
}
```

Таким образом, применение принципов Дейкстры в практике Android-разработки помогает создавать более четкий, понятный
и поддерживаемый код, способствующий эффективной командной работе и долгосрочной поддержке проекта .


## Выводы
