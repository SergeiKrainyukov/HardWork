# Пример 3

## Логический дизайн

Сервис управления корзиной должен:

- добавлять товар в корзину
- удалять товар из корзины
- удалять все товары из корзины
- редактировать товар в корзине
- получать список товаров в корзине

## Рефлексия

Главный недостаток старой версии кода в том, что добавление, удаление и редактирование товаров
разбросано в совершенно разных местах по проекту. Нет единого сервиса, отвечающего за данный
функционал, кроме собственно интерфейса к базе данных. Соответственно, без полного погружения в
проект, понять, какие требования предъявляются к корзине, невозможно.

Для соответствия логическому дизайну требовался совершенно иной код.
- создан отдельный сервис для работы с корзиной
- все методы сервиса теперь возвращают какой-результат, который обрабатывается отдельно в ui.
 
Из кода теперь ясно виден логический дизайн системы.

## Сколько времени заняла итерация

Итерация заняла 2 часа.

- [Старая версия](Старая_версия.md)
- [Новая версия](Новая_версия.md)