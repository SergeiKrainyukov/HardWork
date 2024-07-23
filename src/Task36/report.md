## Задание 36

### Пример 1: Иерархия классов для сотрудников компании
Исходная иерархия:

```kotlin
open class Employee {
    open fun work() {}
}

open class Manager : Employee() {
    open fun manage() {}
}

class SalesManager : Manager() {
    fun generateSalesReport() {}
}

class ITManager : Manager() {
    fun superviseTechTeam() {}
}

```

Оптимизированная иерархия:

```kotlin
open class Employee {
    open fun work() {}
}

class SalesManager : Employee() {
    fun generateSalesReport() {}
}

class ITManager : Employee() {
    fun superviseTechTeam() {}
}

```

Здесь класс Manager лишь усложнял понимание кода, так как поведение всех его наследников принципиально отличалось друг от
друга, и полиморфизм только мешал.

Применение интерфейсов:

```kotlin
interface Workable {
    fun work()
}

interface Manageable {
    fun manage()
}

interface SalesReportable {
    fun generateSalesReport()
}

interface TechTeamSupervisable {
    fun superviseTechTeam()
}

class SalesManager : Workable, Manageable, SalesReportable {
    override fun work() {
        //...
    }

    override fun manage() {
        //...
    }

    override fun generateSalesReport() {
        //...
    }
}

class ITManager : Workable, Manageable, TechTeamSupervisable {
    override fun work() {
        //...
    }

    override fun manage() {
        //...
    }

    override fun superviseTechTeam() {
        //...
    }
}

```

Подход с интерфейсами выглядит предпочтительнее, так как не усложняет иерархию и добавляет новое поведение лишь при необходимости.

### Пример 2: Иерархия классов для устройств
Исходная иерархия:

```kotlin
open class Device {
    open fun powerOn() {}
    open fun powerOff() {}
}

open class Computer : Device() {
    open fun boot() {}
}

class Laptop : Computer() {
    fun closeLid() {}
}

class Desktop : Computer() {
    fun connectMonitor() {}
}

```

Оптимизированная иерархия:

```kotlin
open class Device {
    open fun powerOn() {}
    open fun powerOff() {}
}

class Laptop : Device() {
    fun closeLid() {}
}

class Desktop : Device() {
    fun connectMonitor() {}
}

```

Здесь от класса Computer также можно безболезненно избавиться, так как необходимые методы включения и
выключения девайса уже и так есть в родительском классе.

Применение интерфейсов:

```kotlin
interface Powerable {
    fun powerOn()
    fun powerOff()
}

interface Bootable {
    fun boot()
}

interface LidClosable {
    fun closeLid()
}

interface MonitorConnectable {
    fun connectMonitor()
}

class Laptop : Powerable, Bootable, LidClosable {
    override fun powerOn() {
        //...
    }

    override fun powerOff() {
        //...
    }

    override fun boot() {
        //...
    }

    override fun closeLid() {
        //...
    }
}

class Desktop : Powerable, Bootable, MonitorConnectable {
    override fun powerOn() {
        //...
    }

    override fun powerOff() {
        //...
    }

    override fun boot() {
        //...
    }

    override fun connectMonitor() {
        //...
    }
}

```

Здесь применение интерфейсов также выглядит наиболее оптимальным, позволяя добавить разным сущностям
необходимое поведение.

Ясную архитектуру прохожу прямо сейчас, пока не вижу как можно применить описанные там подходы к данному случаю.

## Выводы

Вообще во всех проектах, которые мне встречались, использовалась либо одноуровневая иерархия, либо интерфейсы, поэтому
примеры привел очень простые, отражающие то, как я понял материал. Однако, благодаря данному занятию я получил новый способ 
думания о программе в контексте того, что множеством различных объектов можно манипулировать так же, 
как и одним конкретным объектом. Пример с котиками очень хорошо запомнился. Это позволяет нам немного иначе подходить к проектированию
иерархий классов и исключать лишние элементы цепочки, лишь усложняющие понимание кода.