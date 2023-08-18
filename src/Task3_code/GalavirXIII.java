package Task3_code;

import java.util.List;
import java.util.Random;

public class GalavirXIII extends SpaceShip {

    private int defenseStatus;

    private int armor;
    private int shield;
    private final List<Weapon> weapons;

    public GalavirXIII(int armor, int shield, List<Weapon> weapons) {
        super(armor, shield, weapons);
        this.armor = armor;
        this.shield = shield;
        this.weapons = weapons;
        defenseStatus = DEFENSE_SUCCESS;
    }

    @Override
    public void defense(int damage) {
        if (armor == 0) {
            defenseStatus = DEFENSE_FAILURE;
            return;
        }
        if (shield >= damage) {
            shield -= damage;
            defenseStatus = DEFENSE_SUCCESS;
            return;
        }
        if (shield > 0) {
            armor -= damage - shield;
            shield = 0;
            defenseStatus = DEFENSE_SUCCESS;
            return;
        }
        if (armor >= damage) {
            armor -= damage;
            defenseStatus = DEFENSE_SUCCESS;
            return;
        }
        armor = 0;
        defenseStatus = DEFENSE_FAILURE;
    }

    @Override
    public int attack() {
        Random random = new Random();
        Weapon weapon = weapons.get(random.nextInt(weapons.size()));
        return random.nextInt(weapon.getMaxDamage() - weapon.getMinDamage()) + weapon.getMinDamage();
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public int getShield() {
        return shield;
    }

    @Override
    public int getDefenseStatus() {
        return defenseStatus;
    }

    @Override
    public List<Weapon> getWeapons() {
        return weapons;
    }
}
