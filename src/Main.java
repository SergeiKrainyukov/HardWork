class Main {
//    public static void main(String[] args) {
//        var list = List.of(1, 2, 3);
//        var map = list.stream()
//                .map(integer -> List.of(integer, integer))
//                .collect(Collectors.toMap(integers -> integers.get(0), integers -> integers.get(1)));
//
//        map.forEach((key, value) -> System.out.println("key: " + key + " value " + value));

//    }


    public static void main(String[] args) {
        int[] arr1 = {256, 256, 512, 512, 5, 1024, 1024, 1024, 5};
        int[] arr2 = {1, 2, 3, 2, 1};
        int[] arr3 = {1, 2, 3, 2, 1, 2, 4, 2, 1};

        System.out.println(artificial_muscle_fibers(arr1)); // Output: 0
        System.out.println(artificial_muscle_fibers(arr2)); // Output: 2
        System.out.println(artificial_muscle_fibers(arr3)); // Output: 2
    }

    public static int artificial_muscle_fibers(int[] arr) {
        byte[] bitArray = new byte[8000]; // Массив для отслеживания чисел (32000 чисел * 2 бита)

        int duplicateCount = 0;

        for (int num : arr) {
            int index = num - 1;
            int byteIndex = (index * 2) / 8;
            int bitOffset = (index * 2) % 8;

            // Получаем текущее значение двух битов для данного числа
            int currentBits = (bitArray[byteIndex] >> bitOffset) & 3; // Используем десятичное число 3 вместо двоичного 0b11

            if (currentBits == 1) { // Используем десятичное число 1 вместо двоичного 0b01
                // Если число уже встречалось один раз, увеличиваем счетчик дубликатов
                duplicateCount++;
                // Обновляем значение битов до состояния "встречалось более одного раза"
                bitArray[byteIndex] |= (2 << bitOffset); // Используем десятичное число 2 вместо двоичного 0b10
            } else if (currentBits == 0) { // Используем десятичное число 0 вместо двоичного 0b00
                // Если число не встречалось ранее, обновляем значение битов до состояния "встречалось один раз"
                bitArray[byteIndex] |= (1 << bitOffset); // Используем десятичное число 1 вместо двоичного 0b01
            }
            // Если число уже встречалось более одного раза, ничего не делаем
        }

        return duplicateCount;
    }
}

class TTT {

    String s;

    public TTT(String s) {
        this.s = s;
    }
}