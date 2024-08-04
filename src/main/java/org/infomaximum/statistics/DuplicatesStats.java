package org.infomaximum.statistics;

import org.infomaximum.entities.Record;
import org.infomaximum.utils.AsciiString;

import java.util.*;

public class DuplicatesStats {
    private static final int CHUNK_SIZE = 250000; // обрабатываем объекты чанками
    private List<Record> currentChunk = new ArrayList<>(); // обрабатываемый чанк
    private Map<AsciiString, Map <AsciiString, Integer>> totals;

   public DuplicatesStats(){
       this.totals = new HashMap<AsciiString, Map<AsciiString, Integer>>();
   }

    public void add(Record record) {
        currentChunk.add(record);
        if (currentChunk.size() >= CHUNK_SIZE) { // набрали чанк для обработки
            processChunk();
            currentChunk.clear(); // очистили чанк для следующей порции данных
        }
    }

    private void processChunk() {
        Map<AsciiString, Map<AsciiString, Integer>> chunkStats = new HashMap<>(); // создаем статистику для чанка

        // Подсчет количества повторений в чанке
        for (Record record : currentChunk) {
            AsciiString group = new AsciiString(record.getGroup());
            AsciiString type = new AsciiString(record.getType());
            chunkStats.computeIfAbsent(group, k -> new HashMap<>())
                    .merge(type, 1, Integer::sum);
        }

        // отбрасываем все что не повторяется
        Map<AsciiString, Map<AsciiString, Integer>> filteredChunkStats = new HashMap<>();
        for (Map.Entry<AsciiString, Map<AsciiString, Integer>> entry : chunkStats.entrySet()) {
            AsciiString group = entry.getKey();
            Map<AsciiString, Integer> types = entry.getValue();
            Map<AsciiString, Integer> filteredTypes = new HashMap<>();
            for (Map.Entry<AsciiString, Integer> typeEntry : types.entrySet()) {
                if (typeEntry.getValue() > 1) {
                    filteredTypes.put(typeEntry.getKey(), typeEntry.getValue());
                }
            }
            if (!filteredTypes.isEmpty()) {
                filteredChunkStats.put(group, filteredTypes);
            }
        }
        // Мердж чанка в глобальную статистику
        mergeChunkStats(filteredChunkStats);
    }


    private void mergeChunkStats(Map<AsciiString, Map<AsciiString, Integer>> chunkStats) {
        for (Map.Entry<AsciiString, Map<AsciiString, Integer>> entry : chunkStats.entrySet()) {
            AsciiString group = entry.getKey();
            Map<AsciiString, Integer> types = entry.getValue();

            // Получаем или создаем новую Map для текущей группы
            Map<AsciiString, Integer> existingTypes = totals.computeIfAbsent(group, k -> new HashMap<>());

            // Обновляем или добавляем типы и их количества
            for (Map.Entry<AsciiString, Integer> typeEntry : types.entrySet()) {
                AsciiString type = typeEntry.getKey();
                Integer count = typeEntry.getValue();

                existingTypes.merge(type, count, Integer::sum);
            }
        }
    }

    public String getStats() {
        StringBuilder builder = new StringBuilder();
        builder.append("Дубликаты группы-тип: \n");

        for (Map.Entry<AsciiString, Map<AsciiString, Integer>> entry : totals.entrySet()) {
            AsciiString group = entry.getKey();
            builder.append("Группа: ").append(group.toString()).append('\n');
            Map<AsciiString, Integer> duplicates = entry.getValue();

            for (Map.Entry<AsciiString, Integer> duplicateEntry : duplicates.entrySet()) {
                AsciiString type = duplicateEntry.getKey();
                Integer count = duplicateEntry.getValue();
                if (count > 1) {
                    builder.append("\tТип: ").append(type.toString()).append("\tКол-во повторений: ").append(count).append('\n');
                }
            }
        }
        return builder.toString();
    }
}
