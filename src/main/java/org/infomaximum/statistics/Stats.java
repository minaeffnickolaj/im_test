package org.infomaximum.statistics;

import org.infomaximum.entities.Record;
import org.infomaximum.reader.StreamReader;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Stats {
    //минимальный-максимальный веса объектов в файле
    long minWeights, maxWeights;
    HashMap<String, GroupStats> groupStats;

    public Stats(){
        this.maxWeights = 0;
        this.minWeights = 0;
        this.groupStats = new HashMap<>();
    }

    private class GroupStats {
        BigInteger groupWeight;
        HashMap<String, DuplicateCount> duplicatesList;

        public GroupStats() {
            this.groupWeight = BigInteger.ZERO;
            this.duplicatesList = new HashMap<>();
        }

        private class DuplicateCount {
            String type;
            int count;

            public DuplicateCount(){
                this.count = 1; // минимальное значение чтобы причислить тип к дубликатам
            }

            public void setType(String type) {
                this.type = type;
            }

        }
    }

    public void setMaxWeights(long maxWeights) {
        this.maxWeights = maxWeights;
    }

    public void setMinWeights(long minWeights) {
        this.minWeights = minWeights;
    }

    // не знаем какую реализацию использовать, поэтому подойдет любая реализующая StreamReader
    public <T> void readFile(StreamReader<T> reader) throws IOException {
        long[] weights = new long[10000000]; // предельный размер массива weights
        int readedCount = 0; // кол-во прочитанных по факту записей
        HashMap<String, HashMap<String, Integer>> tempDuplicates = new HashMap<>();

        while (reader.hasNext()) {
            Record record = (Record) reader.readNext();
            weights[readedCount] = record.getWeight();
            readedCount++; // итерируемся к следующей записи
            String group = record.getGroup();
            String type = record.getType();
            long weight = record.getWeight();

            // Получаем статистику для группы или создаем новую, если ее нет
            GroupStats stats = groupStats.computeIfAbsent(group, k -> new GroupStats());

            // Обновляем вес группы
            stats.groupWeight = stats.groupWeight.add(BigInteger.valueOf(weight));

            // Обновляем временную структуру для дубликатов
            tempDuplicates.computeIfAbsent(group, k -> new HashMap<>())
                    .merge(type, 1, Integer::sum);
        }

        Arrays.sort(weights, 0, readedCount); // сортируем только диапазон активных записей
        setMinWeights(weights[0]);
        setMaxWeights(weights[readedCount - 1]);

        // Обновляем статистику дубликатов
        for (Map.Entry<String, HashMap<String, Integer>> groupEntry : tempDuplicates.entrySet()) {
            String group = groupEntry.getKey();
            HashMap<String, Integer> types = groupEntry.getValue();

            GroupStats stats = groupStats.get(group);

            for (Map.Entry<String, Integer> typeEntry : types.entrySet()) {
                String type = typeEntry.getKey();
                int count = typeEntry.getValue();

                if (count > 1) { // учитываем только те сочетания, где количество больше 1
                    GroupStats.DuplicateCount duplicateCount = new GroupStats().new DuplicateCount();
                    duplicateCount.setType(type);
                    duplicateCount.count = count;
                    stats.duplicatesList.put(type, duplicateCount);
                }
            }
        }
    }

    public void printStats() {
        // Выводим дубликаты group type с количеством повторений
        System.out.println("Дубликаты group type с количеством повторений:");
        for (String group : groupStats.keySet()) {
            GroupStats stats = groupStats.get(group);
            for (Map.Entry<String, GroupStats.DuplicateCount> entry : stats.duplicatesList.entrySet()) {
                String type = entry.getKey();
                GroupStats.DuplicateCount duplicateCount = entry.getValue();
                System.out.printf("Группа: %s, Тип: %s, Количество повторений: %d%n", group, type, duplicateCount.count);
            }
        }

        // Выводим groupWeight по каждой группе
        System.out.println("\nВес группы по каждой группе:");
        for (String group : groupStats.keySet()) {
            GroupStats stats = groupStats.get(group);
            System.out.printf("Группа: %s, Общий вес: %s%n", group, stats.groupWeight.toString());
        }

        // Выводим minWeights и maxWeights
        System.out.println("\nМинимальный и максимальный веса:");
        System.out.printf("Минимальный вес: %d%n", minWeights);
        System.out.printf("Максимальный вес: %d%n", maxWeights);
    }
}
