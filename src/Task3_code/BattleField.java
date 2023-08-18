package Task3_code;

import Task3_code.old.GalavirXIIIOld;
import Task3_code.old.NimrodOld;

public class BattleField {
    public static String nimrodDefence = "Вражеская атака отбита";
    public static String nimrodAttack = "Наносим контр удар";
    public static String nimrodDestroy = "Вражеский удар не выдержан";
    public static String galavirDefence = "Наша атака успешно отражена врагом";
    public static String galavirAttack = "Враг наносит удар";
    public static String galavirDestroy = "Вражеский корабль повержен";

    public static void main(String[] args) {
        GalavirXIIIOld galavirXIIIOld = new GalavirXIIIOld();
        NimrodOld nimrodOld = new NimrodOld();
        do {
            System.out.println(galavirAttack);
            nimrodOld.defend(galavirXIIIOld.attack());
            if (!isNimrodAlive(nimrodOld)) break;
            System.out.println(nimrodAttack);
            galavirXIIIOld.defend(nimrodOld.attack());
        }
        while (isGalavirAlive(galavirXIIIOld));
    }

    public static boolean isNimrodAlive(NimrodOld nimrodOld) {
        if (nimrodOld.health > 0) {
            System.out.println(nimrodDefence);
            return true;
        } else {
            System.out.println(nimrodDestroy);
            return false;
        }
    }

    public static boolean isGalavirAlive(GalavirXIIIOld galavir) {
        if (galavir.health > 0) {
            System.out.println(galavirDefence);
            return true;
        } else {
            System.out.println(galavirDestroy);
            return false;
        }
    }
}
