# Отчет по заданию 3

В качестве проекта для выполнения задания сделал небольшую мини-игру, где есть несколько космических
кораблей с различными характеристиками - броня, щиты, оружие. Когда писал код по TDD, то тесты
выглядели следующим образом:

```Java
public class GalavirXIIIOldTest {

   @org.junit.Test
   public void defendTest() {
      GalavirXIIIOld galavirXIIIOld = new GalavirXIIIOld();
      int damage = 1000;
      galavirXIIIOld.defend(damage);
      assertEquals(Double.MAX_VALUE - damage, galavirXIIIOld.health, 0);
   }

   @org.junit.Test
   public void attackTest() {
      GalavirXIIIOld galavirXIIIOld = new GalavirXIIIOld();
      int attackValue = galavirXIIIOld.attack();
      assertEquals(Integer.MAX_VALUE, attackValue);
   }
}
```

Не определив изначально формальную спецификацию того, какое поведение ожидается от моих сущностей
в программе, я следовал принципу "красный-зеленый-рефакторинг". Сначала писал тест, потом код для
успешного прохождения теста, и рефакторил при необходимости.

Изучив статьи по трем уровням мышления о программе, и подумав, как я могу использовать 
знания из первого курса по ООАП, я применил другой подход. Для начала я определил абстрактные
типы данных Космический корабль и Оружие, и четко описал их интерфейсы:

```java
public abstract class SpaceShip {

    public static final int DEFENSE_SUCCESS = 1; // защита прошла успешно
    public static final int DEFENSE_FAILURE = 2; // корабль был уничтожен

    //Конструктор
    //Постусловие: создан новый корабль с указанными значениями брони, щитов и оружия
    public SpaceShip(int armor, int shield, List<Weapon> weapons) {

    }

    //Команды

    //Предусловие: количество брони больше нуля
    //Постусловие:
    // если уровень щитов больше переданного значения, то уменьшен только уровень щитов на величину переданного значения
    // если уровень щитов меньше переданного значения, то уменьшен уровень щитов до нуля и уровень брони на величину, равную
    // разнице урона и оставшегося уровня щитов
    // если уровень щитов равен нулю, то уменьшен уровень брони на величину переданного значения
    public abstract void defense(int damage);

    //Запросы
    public abstract int attack(); //возвращает случайное количество урона от одного из орудий

    public abstract int getArmor(); //возвращает уровень оставшегося здоровья

    public abstract int getShield(); //возвращает уровень оставшейся брони

    public abstract int getDefenseStatus(); //корабль цел; корабль уничтожен

    public abstract List<Weapon> getWeapons(); //возвращает список орудий, установленных на корабль
}
```

```java
public abstract class Weapon {
    //Конструктор
    //Постусловие: создано новое оружие, наносящее урон в указанных границах
    public Weapon(int minDamage, int maxDamage) {

    }

    //запросы:

    public abstract int getMinDamage(); // возвращает минимальный урон, наносимый оружием

    public abstract int getMaxDamage(); // возвращает максимальный урон, наносимый оружием
}
```

Теперь, независимо от реализации, каждый космический корабль обязан соответствовать определенным
пред- и постусловиям, которые являются отражением дизайна разрабатываемой системы.

Соответственно, тесты теперь выглядят иначе:

```java
public class GalavirXIIITest {

    private static final int armor = 1000;
    private static final int shield = 500;
    private static final Weapon electroWeapon = new ElectroWeapon();
    private static final Weapon laserWeapon = new LaserWeapon();

    private GalavirXIII galavirXIII;

    @Before
    public void createShip() {
        List<Weapon> weapons = new ArrayList<>() {
        };
        weapons.add(electroWeapon);
        weapons.add(laserWeapon);
        galavirXIII = new GalavirXIII(armor, shield, weapons);
    }

    @Test
    public void defenseSuccess() {
        for (int i = 0; i < 2; i++) {
            galavirXIII.defense(electroWeapon.getMaxDamage());
        }
        assertEquals(SpaceShip.DEFENSE_SUCCESS, galavirXIII.getDefenseStatus());
    }

    @Test
    public void defenseFailure() {
        for (int i = 0; i < 10; i++) {
            galavirXIII.defense(electroWeapon.getMaxDamage());
        }
        assertEquals(SpaceShip.DEFENSE_FAILURE, galavirXIII.getDefenseStatus());
    }

    @Test
    public void attack() {
        for (int i = 0; i < 100; i++) {
            int attackValue = galavirXIII.attack();
            assertTrue(attackValue >= Math.min(electroWeapon.getMinDamage(), laserWeapon.getMinDamage()));
            assertTrue(attackValue <= Math.max(electroWeapon.getMaxDamage(), laserWeapon.getMaxDamage()));
        }
    }
}
```
Сразу видны несколько преимуществ такого подхода:
1. Тесты стало гораздо проще читать и понимать, так как они следуют определенному дизайну 
2. Код самих классов стал гораздо более структурированным, читабельным, легко расширяемым и
поддерживаемым
3. Тесты теперь проверяют не конкретные значения полей класса (2 уровень мышления), которых
может быть огромное множество, а соответствие дизайну системы (3 уровень мышления)

Общие впечатления по теме:
- открыл для себя новый способ проектирования и написания кода, вдохновился, буду развиваться
дальше :)
- код стало писать проще и приятнее, чувствую, как растет скилл проектирования



