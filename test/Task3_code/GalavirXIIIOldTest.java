package Task3_code;

import Task3_code.old.GalavirXIIIOld;

import static org.junit.Assert.*;

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