# Задание 7

## Решение

- [Старая версия кода](Пример1.md)
- [Новая версия кода](Пример2.md)

Пример кода был взят из учебного проекта автопарк. В старой версии используется
неистинное наследование - метод calculateReport() переопределяется в дочернем классе и
имеет другую логику.

В новой версии кода сущностей стало заметно больше, однако понимать код стало проще и
быстрее, а также появилось гораздо больше возможностей для его расширяемости.
В своем примере я реализовал Посетитель, дополнительно параметризировав его типом
возвращаемого результата, с учетом того, что в будущем могут появиться отчеты с иными
типами возвращаемых результатов, но с той же общей для всех операцией.

## Резюме и рефлексия по теме

С паттернами был знаком уже давно, хотя сам применял их нечасто, и в основном для
реализации функциональности, предоставляемой какой-либо библиотекой или фреймворком (внедрение 
зависимостей или UI-фреймворки).

О паттерне Посетитель слышал раньше, но никогда его не разбирал и на практике не
применял. Удалось довольно быстро разобраться в принципе его реализации, но много
времени потратил, чтобы подобрать нормальный пример из реальной практики.

Узнал, как применять одну и ту же операцию к объектам различных классов, не загромождая
при этом код, а также как над объектами сложной структуры объектов выполнить некоторые, 
не связанные между собой операции без ущерба структуре и читаемости кода.