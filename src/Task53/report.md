## Задание 53

В Андроид-разработке и языке котлин есть поддержка корутин по умолчанию, поэтому использование cps как бы подразумевается
изначально. Однако cps+дефункционализация используются не так часто, поэтому постараюсь привести примеры, где это
можно применить.

### **Пример 1**

```kt
//1. Определяем данные для дефункционализации
sealed interface PaymentOperation {
    data class ValidateCard(val cardNumber: String, val expiryDate: String) : PaymentOperation
    data class ProcessPayment(val amount: Double, val currency: String) : PaymentOperation
    data class UpdateOrderStatus(val orderId: String, val success: Boolean) : PaymentOperation
    object ShowResult : PaymentOperation
}

//2. Реализуем CPS-функции для каждой операции
class PaymentProcessor(private val scope: CoroutineScope) {
    private suspend fun executeOperation(
        operation: PaymentOperation,
        continuation: (Result<Any>) -> Unit
    ) {
        when (operation) {
            is PaymentOperation.ValidateCard -> {
                validateCard(operation.cardNumber, operation.expiryDate, continuation)
            }
            is PaymentOperation.ProcessPayment -> {
                processPayment(operation.amount, operation.currency, continuation)
            }
            is PaymentOperation.UpdateOrderStatus -> {
                updateOrderStatus(operation.orderId, operation.success, continuation)
            }
            PaymentOperation.ShowResult -> {
                showResult(continuation)
            }
        }
    }

    private suspend fun validateCard(
        cardNumber: String,
        expiryDate: String,
        continuation: (Result<Any>) -> Unit
    ) = withContext(Dispatchers.Default) {
        delay(500) // Имитация проверки
        if (cardNumber.length == 16 && expiryDate.matches(Regex("\\d{2}/\\d{2}"))) {
            continuation(Result.success("valid_card_token"))
        } else {
            continuation(Result.failure(IllegalArgumentException("Invalid card data")))
        }
    }

    private suspend fun processPayment(
        amount: Double,
        currency: String,
        continuation: (Result<Any>) -> Unit
    ) = withContext(Dispatchers.IO) {
        delay(1000) // Имитация сетевого запроса
        if (Random.nextBoolean()) {
            continuation(Result.success(PaymentReceipt(UUID.randomUUID().toString(), amount)))
        } else {
            continuation(Result.failure(IOException("Payment gateway error")))
        }
    }

    private suspend fun updateOrderStatus(
        orderId: String,
        success: Boolean,
        continuation: (Result<Any>) -> Unit
    ) = withContext(Dispatchers.IO) {
        delay(300) // Имитация обновления БД
        continuation(Result.success(Unit))
    }

    private suspend fun showResult(continuation: (Result<Any>) -> Unit) {
        withContext(Dispatchers.Main) {
            // Обновление UI
            continuation(Result.success(Unit))
        }
    }

    fun startPaymentFlow(orderId: String, amount: Double, cardData: CardData) {
        val operations = listOf(
            PaymentOperation.ValidateCard(cardData.number, cardData.expiry),
            PaymentOperation.ProcessPayment(amount, "USD"),
            PaymentOperation.UpdateOrderStatus(orderId, true),
            PaymentOperation.ShowResult
        )

        // Запуск цепочки вызова операций
        scope.launch {
            executePaymentChain(operations, 0, null)
        }
    }

    private suspend fun executePaymentChain(
        operations: List<PaymentOperation>,
        index: Int,
        previousResult: Any?
    ) {
        if (index >= operations.size) return

        val currentOperation = when (val op = operations[index]) {
            is PaymentOperation.ProcessPayment -> {
                if (previousResult is String) op.copy(amount = op.amount)
                else op
            }
            else -> operations[index]
        }

        executeOperation(currentOperation) { result ->
            when {
                result.isSuccess -> {
                    when (currentOperation) {
                        is PaymentOperation.ShowResult -> {
                            // Обновление UI через ViewModel
                            _paymentState.value = PaymentState.Success
                        }
                        else -> {
                            scope.launch {
                                executePaymentChain(operations, index + 1, result.getOrNull())
                            }
                        }
                    }
                }
                else -> {
                    _paymentState.value = PaymentState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                    // Откат транзакции при ошибке
                    rollbackOperations(operations.subList(0, index))
                }
            }
        }
    }

    private suspend fun rollbackOperations(executedOperations: List<PaymentOperation>) {
        executedOperations.reversed().forEach { op ->
            when (op) {
                is PaymentOperation.UpdateOrderStatus -> {
                    updateOrderStatus(op.orderId, false) { /* ignore result */ }
                }
                // Другие операции отката
                else -> {}
            }
        }
    }
}

data class CardData(val number: String, val expiry: String)
data class PaymentReceipt(val id: String, val amount: Double)

class PaymentViewModel : ViewModel() {
    private val processor = PaymentProcessor(viewModelScope)

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    fun processPayment(orderId: String, amount: Double, cardData: CardData) {
        _paymentState.value = PaymentState.Loading
        processor.startPaymentFlow(orderId, amount, cardData)
    }

    sealed interface PaymentState {
        object Idle : PaymentState
        object Loading : PaymentState
        object Success : PaymentState
        data class Error(val message: String) : PaymentState
    }
}

// В Activity
viewModel.paymentState
    .onEach { state ->
        when (state) {
            is PaymentViewModel.PaymentState.Loading -> showProgress()
            is PaymentViewModel.PaymentState.Success -> showSuccess()
            is PaymentViewModel.PaymentState.Error -> showError(state.message)
            else -> {}
        }
    }
    .launchIn(lifecycleScope)
```


### **Пример 2**

```kt

//1. Определяем данные для дефункционализации

sealed class AuthOperation {
    // Проверка учетных данных
    data class CheckCredentials(val email: String, val password: String) : AuthOperation()
    
    // Загрузка профиля по токену
    data class FetchProfile(val authToken: String) : AuthOperation()
    
    // Обновление UI
    object UpdateUI : AuthOperation()
}

//2. Реализуем CPS-функции для каждой операции

typealias AuthContinuation = (Result<Any>) -> Unit

fun executeAuthOperation(
    operation: AuthOperation,
    continuation: AuthContinuation
) {
    when (operation) {
        is AuthOperation.CheckCredentials -> checkCredentials(operation.email, operation.password, continuation)
        is AuthOperation.FetchProfile -> fetchProfile(operation.authToken, continuation)
        is AuthOperation.UpdateUI -> updateUI(continuation)
    }
}

// Асинхронная проверка учетных данных
private fun checkCredentials(
    email: String,
    password: String,
    continuation: AuthContinuation
) {
    Thread {
        if (email.isNotEmpty() && password.length >= 6) {
            val fakeToken = "token_${email.hashCode()}"
            continuation(Result.success(fakeToken))
        } else {
            continuation(Result.failure(IllegalArgumentException("Invalid credentials")))
        }
    }.start()
}

// Асинхронная проверка профиля
private fun fetchProfile(
    authToken: String,
    continuation: AuthContinuation
) {
    Thread {
        if (authToken.startsWith("token_")) {
            val fakeProfile = mapOf(
                "name" to "John Doe",
                "email" to "john@example.com"
            )
            continuation(Result.success(fakeProfile))
        } else {
            continuation(Result.failure(SecurityException("Invalid token")))
        }
    }.start()
}

// Обновление UI
private fun updateUI(continuation: AuthContinuation) {
    Thread.sleep(500) // Имитация задержки UI
    println("UI updated: Welcome John!")
    continuation(Result.success(Unit))
}

//3. Связываем операции в цепочку с обработкой ошибок
fun startAuthFlow(email: String, password: String) {
    val operations = listOf(
        AuthOperation.CheckCredentials(email, password),
        AuthOperation.FetchProfile(""), // Токен будет передан из предыдущего шага
        AuthOperation.UpdateUI
    )

    executeAuthChain(operations, 0, null) { result ->
        when {
            result.isSuccess -> println("Auth flow completed")
            else -> println("Auth failed: ${result.exceptionOrNull()?.message}")
        }
    }
}

private fun executeAuthChain(
    operations: List<AuthOperation>,
    index: Int,
    previousResult: Any?,
    finalContinuation: AuthContinuation
) {
    if (index >= operations.size) {
        finalContinuation(Result.success(Unit))
        return
    }

    val operation = when (val op = operations[index]) {
        is AuthOperation.FetchProfile -> {
            // Используем токен из предыдущего шага
            if (previousResult is String) op.copy(authToken = previousResult)
            else op
        }
        else -> operations[index]
    }

    executeAuthOperation(operation) { result ->
        when {
            result.isSuccess -> {
                // Передаем результат следующей операции
                executeAuthChain(operations, index + 1, result.getOrNull(), finalContinuation)
            }
            else -> finalContinuation(result)
        }
    }
}

// Использование во ViewModel
class AuthViewModel : ViewModel() {
    fun startAuthFlow(email: String, password: String) {
        viewModelScope.launch {
            executeAuthChain(
                operations = listOf(
                    AuthOperation.CheckCredentials(email, password),
                    AuthOperation.FetchProfile(""),
                    AuthOperation.UpdateUI
                ),
                context = viewModelScope.coroutineContext
            )
        }
    }

    private suspend fun executeAuthChain(
        operations: List<AuthOperation>,
        index: Int = 0,
        previousResult: Any? = null,
        context: CoroutineContext
    ) = withContext(context) {
        if (index >= operations.size) return@withContext

        val operation = when (val op = operations[index]) {
            is AuthOperation.FetchProfile -> op.copy(
                authToken = previousResult as? String ?: ""
            )
            else -> operations[index]
        }

        when (val result = executeAuthOperationAsync(operation).await()) {
            is Result.Success -> {
                if (operation is AuthOperation.UpdateUI) {
                    _uiState.value = AuthUIState.Success(result.data)
                }
                executeAuthChain(operations, index + 1, result.data, context)
            }
            is Result.Error -> {
                _uiState.value = AuthUIState.Error(result.exception)
            }
        }
    }
}

```

### Выводы

На работе редко встречал данный подход к реализации прикладных задач. Всего в одном проекте была реализована машина состояний
с похожим поведением, но разобраться на тот момент в том, как она была устроена, было очень трудно, так как не понимал
прежде всего самой концепции данного стиля, зачем это нужно. Однако теперь, когда я сам на тестовых примерах попробовал
реализовать такой подход, стало понятнее. Код в каком-то смысле становится проще, чем если бы он был императивным, проще
становится реализовать цепочки длительных операций, а также упрощается тестирование.