package Task3_code;

public class ElectroWeapon extends Weapon {

    private static final int minDamage = 150;
    private static final int maxDamage = 380;


    public ElectroWeapon() {
        super(minDamage, maxDamage);
    }

    @Override
    public int getMinDamage() {
        return minDamage;
    }

    @Override
    public int getMaxDamage() {
        return maxDamage;
    }
}
