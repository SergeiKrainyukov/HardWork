package Task3_code;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

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