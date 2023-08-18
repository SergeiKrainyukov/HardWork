package Task3_code;

public abstract class Weapon {
    //Конструктор
    //Постусловие: создано новое оружие, наносящее урон в указанных границах
    public Weapon(int minDamage, int maxDamage) {

    }

    //запросы:

    public abstract int getMinDamage(); // возвращает минимальный урон, наносимый оружием

    public abstract int getMaxDamage(); // возвращает максимальный урон, наносимый оружием
}
