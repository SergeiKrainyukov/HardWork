package Task3_code;

public class LaserWeapon extends Weapon {

    private static final int minDamage = 200;
    private static final int maxDamage = 300;

    public LaserWeapon() {
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
