## Задание 30

### Пример 1

Есть алгоритм маппинга списка секций по контейнерам в зависимости от определенных условий. Сами условия и
алгоритм незамысловатые, по сути цикл с простым добавление в итоговый список объектов в зависимости от каких-то
условий, но если взглянуть на такой код без пояснений, то читать его может быть затруднительно, так как эти
куски не связаны друг с другом. С добавлением комментариев код стало сильно проще воспринимать.

```kotlin
class SectionsToContainersMapperImpl @Inject constructor(): SectionToContainersMapper {
    override fun mapSectionsToContainers(sections: List<Section>): List<ContainerWithQuestions> {
        val containersWithQuestionsList = mutableListOf<ContainerWithQuestions>()
        val questionsList = mutableListOf<QuestionVO>()

        for (section in sections) {
            val questions = section.questions

            //Если кнопка, то создаем специальный контейнер для кнопки
            if (questions.size == 1 && questions.first().answerCode == AnswerCode.ACTION) {
                containersWithQuestionsList.add(
                    ContainerWithQuestions(
                        containerType = ContainerType.ActionContainer,
                        questions = section.questions
                    )
                )
                continue
            }

            //Если секция=вопрос, то добавляем в список и идем дальше
            if (questions.size == 1) {
                questionsList.add(questions.first())
                continue
            }

            //Если вопросов в секции больше одного, то делаем раскрывающийся контейнер
            if (questions.size > 1)
                containersWithQuestionsList.add(
                    ContainerWithQuestions(
                        containerType = ContainerType.DropdownContainer(
                            title = questions.first().description,
                            isExpanded = false
                        ),
                        questions = questions
                    )
                )
        }

        //Если обычных вопросов получилось несколько, то создаем обычный контейнер с вопросами
        if (questionsList.isNotEmpty())
            containersWithQuestionsList.add(
                ContainerWithQuestions(
                    containerType = ContainerType.SimpleContainer,
                    questions = questionsList
                )
            )
        return containersWithQuestionsList
    }
}
```

### Пример 2

В данном случае метод initViews() инициализирует разные типы представлений, в каждом случае присваивая различную
логику. Так как эта логика очень простая и в большинстве случаев занимает одну строчку, то выносить каждый слушатель
в отдельный метод было бы оверинжинирнгом, поэтому лучще написать краткий комментарий для облегчения понимания кода.

```kotlin
private fun initViews() {
        
        //Создаем адаптер для RecyclerView
        dpA = DeliveryPointsAdapter(viewModel)

        //При клике на toolbar возвращаемся на предыдущий фрагмент
        findView<MaterialToolbar>(R.id.toolbar)?.setNavigationOnClickListener {
            navigation.goBack()
        }

        //Присваиваем адаптер для RecyclerView
        findView<RecyclerView>(R.id.dpList)?.apply {
            adapter = dpA
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        //При изменении значения в поисковом поле обновляем вью модель
        findView<TextInputEditText>(R.id.searchField)?.addTextChangedListener {
            viewModel.searchPatternIsChanged(it)
        }

        //При нажатии на кнопку создания торговой точки посылаем команду во вью модель на создание точки
        findView<TextView>(R.id.create_new_dp)?.setOnClickListener {
            viewModel.createNewDeliveryPoint()
        }
    }
```

### Пример 3

Из данного следовало бы убрать строчку viewModel._searchText = it.toString(), чтобы он отвечал строго за одну
функциональность, но это потянуло за собой множество других изменений в других частях программы, так как
система изначально была спроектирована не очень хорошо, поэтому было решено просто добавить комментарий для
более легкого понимания кода.

```kotlin
private fun updateAdapter(text: Editable?) {
        text?.let {

            //запоминаем во вью модели введенный текст
            viewModel._searchText = it.toString()

            //Обновляем список кодов ответа
            codesListAdapter.updateList(codesListAdapter.codeListItems.filter {
                it.title.contains(
                    text,
                    ignoreCase = true
                )
            })
        }
    }
```

### Пример 4

В данном примере без комментария было бы не совсем очевидно, зачем нужен вызов метода removeAllViews(), и могли
возникнуть вопросы, либо какой-то разработчик мог бы посчитать, что этот метод вовсе не нужен, и удалить его.

```kotlin
private fun createLayout(containersWithQuestions: List<ContainerWithQuestions>) {
        binding.progressBar.isVisible = false
        binding.contentLayout.apply {
            //Если остались закешированные view, то удаляем их
            removeAllViews()
            
            //Добавляем контейнеры с вопросами
            containersWithQuestions.forEach {
                when (it.containerType) {
                    is ContainerType.SimpleContainer -> addView(SimpleContainerView(requireContext()).apply {
                        addViews(getViewsForContainer(it.questions))
                    })

                    is ContainerType.DropdownContainer -> addView(
                        DropdownContainerView(
                            requireContext()
                        ).apply {
                            setParentLayoutView(it, viewModel::updateUI)
                            setChildLayoutView(getViewsForContainer(it.questions))
                        })

                    is ContainerType.ActionContainer -> addView(ActionContainer(requireContext()).apply {
                        prepare(it.questions.first(), parentFragmentManager)
                    })
                }
            }
        }
    }
```

### Пример 5

В данном случае метод onError() сочетает в себе довольно разнообразную логику при возникновении какой-либо ошибки.
Благодаря комментариям стало легче понимать, что происходит.

```java
public void onError(Throwable e) {
        //Отправить сообщение об ошибке в FirebaseCrashlytics
        FirebaseCrashlytics.getInstance().recordException(e);
                                
        //Вывести сообщение об ошибке в лог
        String message = (e.getMessage() != null) ? e.getMessage() : "";
        Log.e(FirstSynchronizationPresenter.class.getSimpleName(), message);
                                
        //При ошибке сети выйти на экран авторизации
        throwableHandlerForPotentialLogout.execute(e, fragment::openAuthorizationScreen);
                                
        //Заблокировать кнопки синхронизации и вывести сообщение об ошибке
        fragment.blocSyncButtons("Произошла ошибка при получении маршрутов");
}
```

### Выводы

Начал гораздо чаще видеть код-лапшу в проектах и те моменты, которые стоило бы переписать. Чаще всего в коде
коллег стал замечать, что и классы, и функции, могут совмещать в себе большое количество логики, относящейся
к совершенно различным по смыслу частям. Каждый раз возникал вопрос: а можно/нужно ли это переписать, чтобы
код был более хорошо скомпонован по смыслу и соблюдался принцип Single Responsibility? В каких-то случаях
это было бы вполне оправдано, часто это повлекло за собой изменения во множестве других мест программы, что
наглядно демонстрирует плохую архитектуру проекта. Теперь более тщательно продумываю свой код, чтобы таких
ситуаций было как можно меньше.