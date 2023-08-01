import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.forEach(integer -> {
            if (integer == 1){
                return;
            }
        });
        System.out.println("yaaaa");
    }
}