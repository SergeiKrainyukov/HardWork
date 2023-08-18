package Task3_code.old;

public class NimrodOld {
    public static int laser = 100_000;
    public static int rocket = 10_000;
    public static int angelicaKiss;
    public int health = 1_000_000;

    public void defend(int b) {
        health = health - applyShield(b);
    }

    public int attack() {
        return angelicaKiss;
    }

    public byte applyShield(int z) {
        byte result = (byte) z;
        return (byte) (result >= 0 ? result : result * (-1));
    }

}