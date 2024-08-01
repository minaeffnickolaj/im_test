package org.infomaximum.statistics;

import org.infomaximum.entities.Record;
import org.infomaximum.reader.StreamReader;
import org.infomaximum.statistics.DuplicatesStats;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Stats {
    //минимальный-максимальный веса объектов в файле
    long minWeight, maxWeight;
    String groupWeights;
    String duplicateStats;

    public Stats(){
        this.maxWeight = 0;
        this.minWeight = 0;
    }

    //реализуем методы нахождения min-max и суммы веса по группе
    private class WeightsStats {

        //веса по группе
        private HashMap<String, BigInteger> weightsByGroup;

        public WeightsStats() {
            this.weightsByGroup = new HashMap<>();
        }

        public void setMinMax(long weight){
            // без дополнительных аллокаций памяти
            if (minWeight == 0 && maxWeight == 0) {
               minWeight = weight;
               maxWeight = weight;
            } else {
                if (weight < minWeight) {
                    minWeight = weight;
                }
                if (weight > maxWeight) {
                    maxWeight = weight;
                }
            }
        }

        public void addToGroupWeight(String group, long weight) {
            weightsByGroup.computeIfAbsent(group, k -> BigInteger.ZERO);
            weightsByGroup.merge(group, BigInteger.valueOf(weight), BigInteger::add);
        }

        public void countWeightsByGroup(){
            StringBuilder builder = new StringBuilder();
            builder.append("Сумма веса по каждой группе:\n");
            weightsByGroup.forEach((key, value) -> {
                    builder.append("Группа:\t" + key + "\t Cуммарный вес:\t" + value + '\n');
                }
            );
            groupWeights = builder.toString();
        }
    }

    // не знаем какую реализацию использовать, поэтому подойдет любая реализующая StreamReader
    public <T> void readFile(StreamReader<T> reader) throws IOException {
        WeightsStats weightsStats = new WeightsStats();
        DuplicatesStats duplicatesStats = new DuplicatesStats();
        while (reader.hasNext()) {
            Record record = (Record) reader.readNext();
            weightsStats.setMinMax(record.getWeight()); //ищем максимальный - минимальный вес по файлу
            weightsStats.addToGroupWeight(record.getGroup().intern(), record.getWeight());
            duplicatesStats.add(record);
        }
        weightsStats.countWeightsByGroup();
        weightsStats = null; //занулили ссылку на массив
        duplicateStats = duplicatesStats.getStats();
        System.gc();
    }

    public void printStats() {
        System.out.println(duplicateStats);
        System.out.println(groupWeights);
        System.out.printf("Минимальный вес: |%20d|%n", minWeight);
        System.out.printf("Максимальный вес: |%20d|%n", maxWeight);
    }
}
