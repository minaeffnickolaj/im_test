package org.infomaximum.statistics;

import org.infomaximum.entities.Record;
import org.infomaximum.reader.StreamReader;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Stats {
    //минимальный-максимальный веса объектов в файле
    long minWeight, maxWeight;
    //оставим эту мапу, т.к. групп всего десять
    //HashMap<String, BigInteger> weightsByGroup; //мапа сумма весов по группе

    public Stats(){
        this.maxWeight = 0;
        this.minWeight = 0;
        //this.weightsByGroup = new HashMap<>();
    }

    //реализуем методы нахождения min-max и суммы веса по группе
    private class WeightsStats {

        private long[] weights;
        private int size;

        public WeightsStats() {
            this.weights = new long[1];
            size = 0;
        }

        public void addWeight(long weight) {
            if (size == weights.length) { // дошли до размера
                long[] newSizeArr = new long[weights.length * 2]; //создаем новый массив в 2 раза больше
                //Эвакуация массива в новый объем
                System.arraycopy(weights, 0, newSizeArr, 0, weights.length);
                weights = newSizeArr;
            }
            weights[size++] = weight;
        }

        private void sort(){
            Arrays.sort(weights, 0, size - 1);
        }

        public void setMinMax(){
            sort(); //отсортировали по возрастанию
            minWeight = weights[0];
            maxWeight = weights[size - 1];
        }
    }

    // не знаем какую реализацию использовать, поэтому подойдет любая реализующая StreamReader
    public <T> void readFile(StreamReader<T> reader) throws IOException {
        WeightsStats weightsStats = new WeightsStats();
        while (reader.hasNext()) {
            Record record = (Record) reader.readNext();
            weightsStats.addWeight(record.getWeight()); // добавили в массив весов
        }
        weightsStats.setMinMax(); //нашли максимальный - минимальный вес по файлу
        weightsStats = null; //занулили ссылку на массив
        System.gc(); //почистили хип
    }

    public void printStats() {
        System.out.printf("Минимальный вес: |%20d|%n", minWeight);
        System.out.printf("Максимальный вес: |%20d|%n", maxWeight);
    }
}
