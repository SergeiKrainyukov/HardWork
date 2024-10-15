### Задание 42

Приведу пример тестов к фиче экрана комментариев к доставке в одном из рабочих проектов

```kt
@ExperimentalCoroutinesApi
class DefaultCommentsComponentTest {

    private lateinit var commentsComponent: DefaultCommentsComponent
    private val deliveryTransferScreenStoreFactory: CommentsComponentStoreFactory = mockk()
    private val sendCommentBottomSheetFactory: DefaultSendCommentBottomSheetComponent.Factory = mockk()
    private val alertManager: AlertManager = mockk()
    private val componentContext: ComponentContext = mockk()
    private val deliveryId = "123"
    
    private val store: CommentsComponentStore = mockk()
    
    @Before
    fun setUp() {
        // Мокируем поведение store
        every { deliveryTransferScreenStoreFactory.create(deliveryId) } returns store
        
        // Инициализируем компонент
        commentsComponent = DefaultCommentsComponent(
            deliveryTransferScreenStoreFactory,
            sendCommentBottomSheetFactory,
            alertManager,
            componentContext,
            deliveryId
        )
    }

    @After
    fun tearDown() {
        clearMocks(deliveryTransferScreenStoreFactory, sendCommentBottomSheetFactory, alertManager, componentContext, store)
    }

    @Test
    fun `should show error when comment text is blank`() {
        // Arrange
        val blankCommentText = ""

        // Act
        commentsComponent.sendCommentClicked(blankCommentText)

        // Assert
        verify { alertManager.showError("Введите комментарий") }
        verify(exactly = 0) { sendCommentBottomSheetFactory.create(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `should activate bottom sheet when comment text is provided`() {
        // Arrange
        val commentText = "This is a comment"
        
        // Act
        commentsComponent.sendCommentClicked(commentText)

        // Assert
        verify { sendCommentBottomSheetFactory.create(any(), any(), any(), any(), commentText, deliveryId) }
    }

    @Test
    fun `should update text in store when updateText is called`() {
        // Arrange
        val newText = "New comment text"

        // Act
        commentsComponent.updateText(newText)

        // Assert
        verify { store.accept(Intent.UpdateText(newText)) }
    }

    @Test
    fun `should handle error label from store`() {
        // Arrange
        val errorMessage = "Some error occurred"
        every { store.labels } returns MutableStateFlow(Label.ShowError(errorMessage))

        // Act
        runBlocking {
            commentsComponent.model.collect() // Collecting to trigger the label handling in init block.
        }

        // Assert
        verify { alertManager.showError(errorMessage) }
    }

    @Test
    fun `should reload comments when bottom sheet is dismissed`() {
        // Arrange
        every { store.stateFlow } returns MutableStateFlow(CommentsComponentStore.State("", emptyList(), emptyList()))

        // Act
        commentsComponent.bottomSlot.child.dismiss()

        // Assert
        verify { store.accept(Intent.LoadComments) }
    }
}
```
Данные тесты проверяют следующие ключевые свойства фичи:

#### Тест на показ ошибки при пустом комментарии:

Проверяет, что при нажатии кнопки отправки с пустым текстом вызывается метод showError у AlertManager, и не создается экземпляр BottomSheet для ввода комментария.

#### Тест на активацию BottomSheet при наличии текста:

Проверяет, что при наличии текста комментария активируется BottomSheet с правильными параметрами.

#### Тест на обновление текста в хранилище:

Проверяет, что метод updateText вызывает соответствующий метод в хранилище с новым текстом.

#### Тест на обработку ошибки из хранилища:

Проверяет, что при получении ошибки из хранилища вызывается метод showError у AlertManager.

#### Тест на перезагрузку комментариев при закрытии BottomSheet:

Проверяет, что при закрытии нижнего листа (dismiss) вызывается метод для перезагрузки комментариев.


### Выводы

Задание позволило по-новому взглянуть на то, что и как мы должны тестировать. Ранее часто тесты писались
по всему подряд, что придет в голову, чем больше, тем лучше. Теперь буду стараться декомпозировать фичу
на ключевые свойства и в первую очередь тестировать их, а не писать тесты ко всему подряд.
