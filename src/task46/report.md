## Задание 46

### Пример 1

### Было

```kotlin
class DocumentFragment : Fragment {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        
        
        documentViewModel.documentLiveData.observe(viewLifecycleOwner) { document ->
            // Условная проверка на тип документа осуществляется прямо на ui
            // Если документ является паспортом, то выводить по нему дополнительные данные, а если нет, то скрывать их
            if (document.type.id == PASSPORT_DOCUMENT_TYPE_ID) {
                documentViewModel.updateOcrInfo()
            } else {
                documentViewModel.clearOcrInfo()
            }
        }
        
        
    }
    
    
}

class DocumentViewModel: ViewModel() {

    // Два разных Observable, хранящих одну и ту же структуру данных
    var documentLiveData: LiveData<Document?> = MutableLiveData()
    val passportLiveData: MutableLiveData<Document?> = MutableLiveData()

    fun getDocumentById(documentId: String) {
        if (documentLiveData.value?.id != documentId) {
            documentLiveData = getDocumentByIdUseCase.invoke(documentId)
        }
    }
    
    fun updateOcrInfo() {
        viewModelScope.launch {
            documentId?.let {
                getOnlyDocumentByIdUseCase.invoke(it).onSuccess {
                    passportLiveData.value = it
                }
            }
        }
    }
    
    fun clearOcrInfo() {
        passportLiveData.value = null
    }
}
```

### Стало
```kotlin
class DocumentFragment : Fragment {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Просто обновляем данные без всяких проверок
        documentViewModel.getDocumentById(documentViewModel.documentId ?: "")

        documentViewModel.documentLiveData.observe(viewLifecycleOwner) { document ->
            //...
        }
    }
}

class DocumentViewModel : ViewModel() {

    // Оставлен единственный Observable и удалены лишние методы
    var documentLiveData: LiveData<Document?> = MutableLiveData()

    fun getDocumentById(documentId: String) {
        val document = documentLiveData.value
        if (document?.id != documentId || document.type.id == PASSPORT_DOCUMENT_TYPE_ID) {
            documentLiveData = getDocumentByIdUseCase.invoke(documentId)
        }
    }
}

class DocumentItemWithPhotosViewHolder(
    val binding: SimpleItemWithPhotosViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PhotosCustomListItem, onSubItemClickListener: ((DocumentPage) -> Unit)? = null) {
        val ocrFields = item.photos.getOrNull(0)?.processing?.ocr?.fields
        with(binding) {
            PhotoListViewAdapter(onSubItemClickListener).apply {
                lvFragmentDkboPhotos.adapter = this
                submitList(item.photos)
            }
            
            // Результаты распознавания будут выводиться если список с данными непустой 
            ocrResultsView.apply {
                isVisible = !ocrFields.isNullOrEmpty()
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    GpbTheme {
                        ocrFields?.let { OcrStatusView(it) }
                    }
                }
            }
        }
    }
```

### Выводы
Благодаря занятию понял, что часто мое избавление от if было просто перемещением его в другое место, но оно не изменяло саму
суть работы программы. Код просто становился чуть красивее в определенном месте, но проверка никуда не уходила, и смысл
работы оставался прежним. Очень хорошо понял и осмыслил примеры, приведенные в статье, особенно понравился пример с книгой.
Теперь буду применять эти мета-правила на регулярной основе.
