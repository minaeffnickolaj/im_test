package org.infomaximum.statistics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Stats {
    //минимальный-максимальный веса объектов в файле
    long minWeights, maxWeights;
    HashMap<String, GroupStats> groupStats;

    private class GroupStats {
        BigInteger groupWeight;
        ArrayList<DuplicateCount> duplicatesList;

        private class DuplicateCount {
            String type;
            int count;

            public DuplicateCount(){
                this.count = 2; // минимальное значение чтобы причислить тип к дубликатам
            }

            public void setType(String type) {
                this.type = type;
            }

            //сеттера не будет, вместо этого инкремент на 1
            public void incrementDuplCount(){
                count++;
            }
        }
    }
}
