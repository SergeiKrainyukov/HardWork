## Задание 44

### 1. Кэширование результатов запросов к бд

   При выполнении сложных и ресурсоемких запросов к базе данных,
   таких как агрегация данных или сложные соединения, 
   можно создать кэшированные представления результатов.

В одном из рабочих проектов на Андроид приложение было ориентировано на работу в оффлайне, из-за
чего в нем была сложна система таблиц в бд и несколько View, с которыми приложение постоянно тесно
взаимодействовало. Если приложение будет выполнять сложные запросы каждый раз, когда данные потребуются,
то это будет неэффективно как с точки зрения производительности приложения, так и с точки зрения
потребления памяти и ресурсов смартфона. Однако, это самый простой подход с точки зрения кода, так
как нужно всего лишь прописать запросы без какой-либо дополнительной поддержки. В итоге было
принято решение сделать дополнительный кэш в оперативной памяти во время работы приложения, и его
поддерживать, чтобы каждый раз при получении однотипных данных не обращаться заново к бд. На его поддержку
требовались дополнительные ресурсы, но это было гораздо более эффективно. Еще одним преимуществом
является то, что пользователям апи теперь не нужно задумываться о том, в каком потоке получать
необходимые данные, так как они могут быть получены моментально из кэша.

Пример в коде:

```java
public class AddressRepository extends AbstractRepository<Address> {
    
    private SparseArray<List<Address>> deliveryPointAddresses; // кэш для хранения адресов

    public AddressRepository(DataManager dataManager) {
        super(dataManager);
    }
    
    // Методы, предоставляемые для пользования извне, которые берут информацию из кэша

    public List<Address> getAddressesByDeliveryPoint(int deliveryPointId) {
        return deliveryPointAddresses.get(deliveryPointId, new ArrayList<>());
    }

    public Address getDeliveryPointOrderAddress(int deliveryPointId) {
        Address result = getEmptyContent();
        for (Address address : getAddressesByDliveryPoint(deliveryPointId)) {
            if (address.getTypeOfAddressId() == General.ADDRESS_ORDER_DP_ID) {
                result = address;
                break;
            }
        }
        return result;
    }

    public Address getDeliveryPointAddressByTypeId(int deliveryPointId, int typeOfAddressid) {
        Address result = getEmptyContent();
        for (Address address : getAddressesByDliveryPoint(deliveryPointId)) {
            if (address.getTypeOfAddressId() == typeOfAddressid) {
                result = address;
                break;
            }
        }
        return result;
    }

    // Методы для работы с базой данных
    
    @Override
    public Pair<String, String> getInsertUpdateCommand() {
        return new Pair<>(Persistence.SQL_INSERT_ADDRESS, Persistence.SQL_UPDATE_ADDRESS);
    }

    @Override
    protected SparseArray<Address> executeCursor(Cursor cursor) {
        SparseArray<Address> result = new SparseArray<>();
        try {
            if (cursor.moveToFirst()) {
                int id = cursor.getColumnIndex(Persistence.FIELD_ID);
                int addressCustomer = cursor.getColumnIndex(Persistence.FIELD_ADDRESS);
                int longitude = cursor.getColumnIndex(Persistence.FIELD_LONGITUDE);
                int latitude = cursor.getColumnIndex(Persistence.FIELD_LATITUDE);
                int deliveryPointId = cursor.getColumnIndex(Persistence.FIELD_DELIVERY_POINT_ID);
                int addressTypeId = cursor.getColumnIndex(Persistence.FIELD_ADDRESS_TYPE_ID);
                int isActive = cursor.getColumnIndex(Persistence.FIELD_IS_ACTIVE);
                do {
                    Address address = new Address();
                    address.setId(cursor.getInt(id));
                    address.setAddress(cursor.getString(addressCustomer));
                    address.setLatitude(cursor.getFloat(latitude));
                    address.setDeliveryPointId(cursor.getInt(deliveryPointId));
                    address.setLongitude(cursor.getFloat(longitude));
                    address.setTypeOfAddressId(cursor.getInt(addressTypeId));
                    address.setActive(cursor.getString(isActive).equals("true"));
                    result.put(address.getId(), address);
                    setPreparedMap(address.getDeliveryPointId(), address); // Сохранение данных в кэш
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            Log.e(General.APP_LOG, this.getClass().getSimpleName() + ": Exception " + ex.getLocalizedMessage());
            result = new SparseArray<>();
        } finally {
            cursor.close();
        }
        return result;
    }
    
    // Методы для поддержки корректной работы кэша

    @Override
    protected void init() {
        deliveryPointAddresses = new SparseArray<>();
    }

    private void setPreparedMap(int deliveryPointId, Address address) {
        List<Address> result = deliveryPointAddresses.get(deliveryPointId, new ArrayList<>());
        result.add(address);
        deliveryPointAddresses.put(deliveryPointId, result);
    }
}
```


### 2. Работа с состоянием экрана

В одном из приложений ранее использовался паттерн MVVM (Model-View-ViewModel) с целью максимально отделить презентационный
слой от слоя данных. Было несколько Flow, каждое из которых содержит какое-то свое состояние
кусочка экрана, и на ui было несколько подписок на эти flow, иногда в разных местах. Проблема такого
подхода в том, что, во-первых, тяжело отследить состояние экрана в конкретный момент времени, а
во-вторых, для взаимодействия view и viewModel требуется следить за состоянием всех flow, чтобы
ничего не поломалось. Поэтому приняли решение переписать логику с использованием паттерна MVI, который
решает указанные проблемы и предоставляет более удобный интерфейс для пользования, предоставляя
всего одно состояние экрана целиком и набор действий для его изменения, причем этот набор ограниченный
и известен уже на этапе компиляции.

```kotlin
// Компонент, используемый как связующее звено между экраном и данными
interface DeliveryTransferScreenComponent {

    val model: StateFlow<DeliveryTransferScreenStore.State> // Состояние экрана
    
    // Действия пользователя

    fun changeSearchQuery(query: String)

    fun selectAddressFromList(value: String, unrestrictedValue: String)

    fun selectDate(date: LocalDate)

    fun selectInterval(interval: TimeInterval)

    fun changeComment(comment: String)

    fun onClickTransferButton()

}

// Класс с ограниченным набором действий пользователя и состоянием экрана
interface DeliveryTransferScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class ChangeSearchQuery(val query: String) : Intent
        class SelectAddressFromList(val value: String, val unrestrictedValue: String) : Intent
        class SelectDate(val date: LocalDate) : Intent
        class SelectInterval(val interval: TimeInterval) : Intent
        class ChangeComment(val comment: String) : Intent
        data object OnClickTransferDelivery : Intent
    }

    data class State(
        val initialDeliveryAddress: String,
        val addressState: AddressState,
        val calendarState: CalendarState,
        val intervalState: IntervalState,
        val commentState: CommentState,
        val transferDeliveryButtonState: TransferDeliveryButtonState
    ) {
        data class AddressState(
            val currentAddress: String,
            val addresses: List<CityAddress>,
            val addressSelected: Boolean
        )

        data class CalendarState(
            val availableDates: AddressDeliveryDates,
            val selectedDate: LocalDate,
            val deliveryImpossible: Boolean
        )

        data class IntervalState(
            val availableTimes: List<TimeInterval>,
            val selectedInterval: TimeInterval
        )

        data class CommentState(
            val isVisible: Boolean,
            val comment: String
        )

        data class TransferDeliveryButtonState(
            val buttonStatus: ButtonStatus
        )
    }

    sealed interface Label {
        data object TransferDeliveryCompleted : Label
        data class ShowError(val message: String) : Label
    }

}

class DefaultDeliveryTransferScreenComponent @AssistedInject constructor(
    private val deliveryTransferScreenStoreFactory: DeliveryTransferScreenStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext,
) :
    DeliveryTransferScreenComponent, ComponentContext by componentContext {

    private val store =
        instanceKeeper.getStore { deliveryTransferScreenStoreFactory.create(deliveryData) }
    
    override val model: StateFlow<DeliveryTransferScreenStore.State> = store.stateFlow // Все изменения состояния экрана будут прилетать сюда, на ui надо только подписаться

    override fun changeSearchQuery(query: String) {
        store.accept(DeliveryTransferScreenStore.Intent.ChangeSearchQuery(query)) // Посылаем намерение что-то сделать
    }

    // ...
```


### 3. Механизм вывода уведомлений для пользователя 

В рабочих проектах периодически требуется выводить различные уведомления для пользователя. Раньше
для этого в каждом конкретном случае использовался примерно идентичный код вызова Snackbar
с определенными настройками для отображения. Однако, это во-первых, приводило к дублированию кода
в различных местах, а во-вторых, увеличивало объем кода и сложность его восприятия. Поэтому
приняли решение сделать отдельный унифицированный интерфейс для отображения уведомлений, чтобы
к нему можно было обращаться из любого места в приложении, просто заинжектив в конструктор класса.
   
```kotlin
interface AlertManager {

    val alert: StateFlow<AlertData?>

    fun showSuccess(message: String)
    fun showError(message: String)
    fun showInfo(message: String)
}

class AlertManagerImpl @Inject constructor() : AlertManager {

    private var _alert: MutableStateFlow<AlertData?> = MutableStateFlow(null)
    override val alert: StateFlow<AlertData?> = _alert

    override fun showSuccess(message: String) {
        _alert.value = SuccessAlertData(message)
    }

    override fun showError(message: String) {
        _alert.value = ErrorAlertData(message)
    }

    override fun showInfo(message: String) {
        _alert.value = InfoAlertData(message)
    }
}

interface AlertData : SnackbarVisuals {
    val containerColor: Color
    val contentColor: Color
    val iconResId: Int
}

data class SuccessAlertData(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val containerColor: Color = ElementSuccess,
    override val contentColor: Color = TextWhite,
    override val iconResId: Int = R.drawable.ic_filled_success,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
) : AlertData

data class ErrorAlertData(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val containerColor: Color = ElementError,
    override val contentColor: Color = TextWhite,
    override val iconResId: Int = R.drawable.ic_filled_error,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
) : AlertData


data class InfoAlertData(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val containerColor: Color = ElementGrey,
    override val contentColor: Color = TextWhite,
    override val iconResId: Int = R.drawable.ic_filled_info,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
) : AlertData
```

### Выводы

Раньше в проектах использовал информационную избыточность, но чаще всего это было спонтанно и
не запланировано заранее. Где-то у кого-то увидел хорошее решение, попробовал у себя, получилось,
и все остались довольны. Не отдавал себе отчет в том, что я использую один из важных принципов
построения хорошей архитектуры и чистого кода в приложении. Да, часто возникает соблазн реализовать
что-то по-простому, единоразово, чтобы побыстрее закрыть рабочий тикет, или когда уже начальство
начинает давить, однако теперь более ясно для себя осознал выгоды такого подхода в будущем, поэтому
буду стараться делать сразу учитывая данный подход.