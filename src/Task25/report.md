## Задание 25

В андроид для работы с бд в абсолютном большинстве случаев используется библиотека Room, поэтому разберем ее. В самой ОС Андроид 
в качестве СУБД используется SQLite, соответственно мы можем писать как прямые запросы к бд, так и пользоваться различными
ORM-фреймворками по типу Room.

Краткая схема работы Room:
Разработчик определяет сущности (Entities) и объекты доступа к данным (DAO), используя аннотации, 
а Room генерирует реализацию DAO и выполняет соответствующие SQL-запросы. 
Room также выполняет маппинг результатов запросов в объекты сущностей и поддерживает связи между 
сущностями. Кроме того, Room автоматически управляет транзакциями и применяет оптимизации, такие как 
кэширование и ленивая загрузка, для повышения производительности.

Далее разберем примеры запросов прямо из рабочего проекта с использованием Room и без его использования,
обращаясь напрямую к бд.

[Пример 1](Пример1.md)

[Пример 2](Пример2.md)

[Пример 3](Пример3.md)

### Выводы

Исходя из примеров выше, мы вполне можем заключить, что выигрыш в производительности в обход ОРМ получается существенный - 
в 2-7 раз. Однако, специфика моей работы не предполагает настолько больших объемов данных и настолько частых запросов
к бд, чтобы эта разница была максимально заметна пользователю. Все запросы укладываются в несколько десятков
миллисекунд, что незаметно для пользователя даже при вводе текста с клавиатуры. При том, что ОРМ предполагает
очень удобный апи для работы с данными, все-таки остается предпочтительным использовать его.

Предполагаю, что отказ от использования ОРМ в пользу прямых запросов будет обоснован в случае
разработки игр, где производительность реально имеет значение и важна каждая миллисекунда.