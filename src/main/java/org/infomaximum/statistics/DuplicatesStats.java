package org.infomaximum.statistics;

import org.infomaximum.entities.Record;
import org.infomaximum.utils.AsciiString;

import java.util.*;

public class DuplicatesStats {
   private HashMap<AsciiString, HashMap<AsciiString, Integer>> duplicateStats;

   public DuplicatesStats(){
       this.duplicateStats = new HashMap<AsciiString, HashMap<AsciiString, Integer>>();
   }

    public void add(Record record) {
        try {
            AsciiString group = new AsciiString(record.getGroup());
            AsciiString type = new AsciiString(record.getType());
            if (!duplicateStats.containsKey(group)) {
                HashMap<AsciiString, Integer> duplicate = new HashMap<>();
                duplicate.put(type, 1);
                duplicateStats.put(group,duplicate);
            } else {
                HashMap<AsciiString, Integer> duplicate = duplicateStats.get(group);
                if (!duplicate.containsKey(type)){
                    duplicate.put(type, 1);
                } else {
                    duplicate.put(type, duplicate.get(type) + 1);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getStats() {
        StringBuilder builder = new StringBuilder();
        builder.append("Дубликаты группы-тип: \n");
        for (Map.Entry<AsciiString, HashMap<AsciiString, Integer>> entry : duplicateStats.entrySet()) {
            AsciiString group = entry.getKey();
            builder.append("Группа: " + group.toString().intern() + '\n');
            HashMap<AsciiString, Integer> duplicates = entry.getValue();
            for (Map.Entry<AsciiString, Integer> duplicateEntry : duplicates.entrySet()) {
                AsciiString type = duplicateEntry.getKey();
                Integer count = duplicateEntry.getValue();
                if (count > 1) {
                    builder.append("\tТип: " + type.toString().intern() + "\tКол-во повторений: " + count + '\n');
                }
            }
        }
        return builder.toString();
    }
}
