package Task3_code.old;

public class GalavirXIIIOld {

    public static int superWeapon = Integer.MAX_VALUE;
    public double health = Double.MAX_VALUE;

    public void defend(double b) {
        health -= superWeapon / b;
    }

    public int attack() {
        return superWeapon;
    }
}