package Task3_code;

import java.util.List;

public abstract class SpaceShip {

    public static final int DEFENSE_SUCCESS = 1; // защита прошла успешно
    public static final int DEFENSE_FAILURE = 2; // корабль был уничтожен

    //Конструктор
    //Постусловие: создан новый корабль с указанными значениями брони и щитов
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
