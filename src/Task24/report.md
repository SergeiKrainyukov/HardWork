## Задание 24
## Отчёт

### 1. Зависимость фреймворка

В проекте используется фреймворк Firebase Realtime Database - база данных реального времени от Google.
С ней работает мобильный клиент. Если что-то случится на стороне базы данных, то проект не сломается,
но перестанет работать функционал отправки новых заказов и получение актуального ассортимента с сервера.
Соответственно, работоспособность всего проекта целиком не зависит от данного фреймворка, но конкретно
данный функционал зависит. Сейчас в проекте нет локального кеширования данных, так как используются
чисто онлайн-функции. Для того чтобы снизить зависимость вполне логично будет организовать локальное
кэширование данных на устройстве если на нем пропадет интернет, либо если с базой данных что-то случится.
Важно отметить, что клиент не должен зависеть от реализации источника данных, поэтому ему достаточно знать лишь об
интерфейсе получения данных. В данном случае уместно будет сказать, что мы перевели зависимость фреймворка в плоскость зависимости
перебрасывания, и наш клиент теперь зависит от нескольких источников данных, и переключается между ними по необходимости,
однако локальное сохранение данных в андроид реализуется так, что обычный пользователь не может добраться до файла с бд,
и никакие другие внешние факторы не влияют на эти данные, поэтому пока приложение установлено на смартфоне, 
можно гарантировать его стабильную работу.

### 2. Зависимость расшаренного формата

В рабочих приложениях следую подходу Clean Architecture и для каждого слоя создаю свою модель данных, чтобы при любых
изменениях в каком-либо из слоев нужно было изменить только маппер и не менять логику целых модулей. Domain слой
выступает некоторой прослойкой между слоем данных и слоем ui, что делает их независимыми друг от друга, но
зависимыми от общего формата данных.

### 3. Зависимость зависимости

В проекте использовались сложные sql-запросы к базе данных, работа которых была неочевидна многим сотрудникам, которые
ранее глубоко не погружались в sql. Методы, которые совершали эти запросы, использовались так же в разных местах
программы. Соответственно, изменение любого оператора могло привести к некорректной работе некоторых частей программы, так
как модуль ui, хоть и неявно, но зависит от того, что вернет модуль данных. Договорились с коллегами, что при реализации
нового функционала или фиксе багов не будем менять уже написанный код запросов, а будем либо создавать обертку и добавлять
какие-то параметры, либо писать отдельный запрос. Формально говоря, наложили запрет на изменение определенных частей
программы, сузив пространство допустимых изменений на уровне суперспецификации.


### 4. Зависимость краша

Ранее я обновлял версии зависимостей в проекте до последней версии без всякой необходимости просто по собственному желанию,
как бы "на автомате" не отслеживая при этом изменения, которые были внесены в новую версию библиотеки/фреймворка. Это
могло привести к неожиданным сбоям программы в определенных случаях, хотя в моем коде при этом ничего
не изменялось, то есть имела места неявная зависимость от библиотеки/фреймворка. Теперь я подхожу к обновлению версий более
осознанно и обновляю только в случае необходимости, отслеживая все изменения.

### 5. Зависимость перебрасывания

Недавно в проекте было принято решение отказаться от ручной синхронизации данных с сервером в пользу автоматической без
участия пользователя. Приложение с определенной периодичностью стучится на сервер для получения новых данных, если сервер не отвечает
более 2 раз за определенное время, то единоразово отправляется отчет с ошибкой во внешний сервис от гугла, приложение
перестает стучаться на сервер в течение некоторого времени, и затем возобновляет попытки. Данные в приложении берутся из
локального кеша. В таком случае, если что-то случится с сервисом гугла, то разработчики не смогут оперативно узнать об ошибке
и исправить ее. Хорошим решением было бы сделать резервный внутренний сервер, который мог бы принимать и отчеты об ошибках, и
проводить синхронизацию с клиентом. В таком случае, получилась бы следующая система: приложение стучится на основной сервер,
если он недоступен, то отправляет отчет об ошибке в гугл сервис, и стучится на резервный. Если гугл сервис недоступен, а резервный
работает, то отчет об ошибке шлется на резервный. Если резервный не работает, а гугл работает, то отчеты о недоступности
обоих серверов посылаются туда. Таким образом, актуальность данных в приложении уже зависит от двух источников: {основной сервер, резервный сервер},
и оперативность получения отчетов об ошибках также зависит от двух источников: {гугл сервис, резервный сервер}.

### 6. Зависимость инверсии

После того, как в одном из проектов возникла ошибка на этапе выполнения, связанная с DI, я в своей практике стал 
использовать фреймворки со статической компиляцией и тайп-чекингом, отдавая предпочтение надежности
перед скоростью компиляции. Однако, в небольших проектах на несколько тысяч строк, где интерфейсы имеют практически всегда одну реализацию,
эта зависимость не столь критична, и нужно всегда принимать обоснованное решение об использовании DI в целом, насколько это
необходимо.

### 7. Зависимость зацикливания

В Андроид-разработке циклические зависимости определяются сразу на этапе сборки проекта, поэтому при их наличии
проект просто не соберется. Если такая зависимость все же появляется, то разрешаем его созданием модуля-посредника, который
сам по себе ни от кого не зависит, но от которого могут зависеть другие модули приложения.

### 8. Зависимость высшего порядка

В Андроиде есть механизм создания Custom View, который позволяет создавать свои собственные UI-элементы путем наследования
от базового класса View и переопределять у них методы жизненного цикла, изменяя при этом их поведение как нам угодно. Таким образом,
когда мы добавляем новую View на наш экран динамически, то корректность ее работы во время выполнения зависит от корректности
тех функций, которые мы переопределили у базового класса.

### 9. Зависимость большинства

Возьмем, к примеру, приложение для обработки и сохранения каких-то показателей здоровья человека, например, сердечного ритма.
Для того, чтобы данные были как можно более достоверны, на человека прикрепляется несколько датчиков сразу, и с них поступает
информация на обработку. Таким образом, работа системы не зависит от работы каждого конкретного датчика, и если один датчик выйдет
из строя, то система продолжит работать. При этом чем больше датчиков работают одновременно, тем более точные результаты
будут получены в итоге.

### Рефлексия и выводы
По итогу занятий я стал более уверен в том, что удаляю зависимость правильно, потому что теперь я, знаю к какому
типу она относится, и, как следствие, знаю как разрешаются зависимости данного типа. Раньше мне действительно казалось,
что все влияет на все, но теперь это не так. Также научился выделять зависимость конкретного свойства в системе.

Ранее не знал, что некоторые проблемы (зависимость зависимости и зависимость краша) могут быть решены на уровне
суперспецификации, простым запретом на какие-либо изменения.

Крайне неочевидный момент в занятии - нетранзитивность зависимостей. Позволяет избежать поспешного вывода о том, что
если А зависит от В и В зависит от С, то А зависит от С. Судя по тексту занятия - это не всегда так, хотя данное
положение я все-таки не до конца понял.