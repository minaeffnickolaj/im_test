package org.infomaximum.statistics;

import org.infomaximum.entities.Record;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class DuplicatesStats {
   private HashMap<String,Counter> duplicateStats;

   public DuplicatesStats(){
       this.duplicateStats = new HashMap<>();
   }

    private class Counter{
        /*
            Диапазон символов вписывается в ASCII, будем
            пользоваться примитивами чтобы вмещать символ
            в один байт, вместо двух в UTF-16
         */
        byte[] group;
        byte[] type;
        int counter;

        public Counter(){
            /*
            В любом случае, раз мы инстанцировались
            значит есть как минимум 1 вхождение
             */
            this.counter = 1;
            /* по результатам 10 прогонов тест-генератора не встретилась
             подстрока длиннее 14 символов
             */
            this.type = new byte[14];
            this.group = new byte[14];
            //итого размера объекта 4 + 14 + 14 = 32 байта + 4/8 байт на указатель
            //в зависимости от разрядности JVM
        }

        //проверка строки на возможность преобразования и перевод в ASCII
        public byte[] stringToASCII(String string){
            for (char c : string.toCharArray()) {
                if (c > 127) { // 0..127 - 1 байт
                    throw new IllegalArgumentException("Символ вне ASCII диапазона");
                }
            }
            return string.getBytes(StandardCharsets.US_ASCII);
        }

        //перевод в обычное представление строк в Java
        public String toUTF16String(byte[] arr){
            return new String(arr, StandardCharsets.US_ASCII);
        }

        public boolean compare(byte[] arr, String string) {
            byte[] stringBytes = string.getBytes(StandardCharsets.US_ASCII);
            return Arrays.equals(arr, stringBytes);
        }

        //инкрементирование счетчика
        public void inc(){
            counter++;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Counter counter = (Counter) obj;
            //TODO: наверняка можно оптимальнее
            return (toUTF16String(group).intern() + toUTF16String(type).intern()) ==
                    (counter.toUTF16String(group).intern() + counter.toUTF16String(type).intern());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(toUTF16String(group).intern() + toUTF16String(type).intern());
        }
    }

    public void add(Record record) {
        Counter counter = new Counter();
        try {
            counter.group = counter.stringToASCII(record.getGroup().intern());
            counter.type = counter.stringToASCII(record.getType().intern());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (!duplicateStats.containsKey(record.getGroup())) {
            //добавились в сет если не было
            duplicateStats.put(record.getGroup(), counter);
        } else {
            duplicateStats.get(record.getGroup()).counter++;
        }
    }

    public String getStats() {
        StringBuilder builder = new StringBuilder();
        builder.append("Дубликаты группы-тип: \n");
        duplicateStats.forEach( (k, v) -> {
                    if (v.counter > 1){
                        builder.append("Группа: " + v.toUTF16String(v.group).intern() + "\tТип: " +
                                v.toUTF16String(v.type).intern() + "\tКол-во повторений: " +
                                v.counter + '\n');
                    }
                }
        );
        return builder.toString();
    }
}
