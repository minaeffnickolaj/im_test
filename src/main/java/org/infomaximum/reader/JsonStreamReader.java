package org.infomaximum.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.infomaximum.entities.Record;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

// AutoCloseable для автоматического закрытия/освобождения ресурсов по завершению работы
public class JsonStreamReader implements StreamReader<Record>, AutoCloseable {
    private final JsonParser parser;
    private final ObjectMapper mapper;
    private JsonNode currentNode;
    private boolean endOfFile;

    public JsonStreamReader(String filePath) throws IOException {
        try {
            this.mapper = new ObjectMapper();
            this.parser = mapper.getFactory().createParser(new File(filePath));
            this.endOfFile = false;
            // Начнем чтение первого элемента
            advanceToNext();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Не удалось открыть файл!");
        }
    }

    @Override
    public Record readNext() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException("Файл вычитан!");
        }
        JsonNode node = currentNode;
        advanceToNext();
        return nodeToRecord(node);
    }

    @Override
    public boolean hasNext() {
        return !endOfFile;
    }

    // Переопределение close() из AutoCloseable
    @Override
    public void close() throws IOException {
        parser.close();
    }

    private void advanceToNext() {
        try {
            // Пропускаем до следующего объекта
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.currentToken() == JsonToken.START_OBJECT) {
                    currentNode = mapper.readTree(parser);
                    return;
                }
            }
            endOfFile = true;
        } catch (IOException e) {
            e.printStackTrace();
            endOfFile = true;
        }
    }

    private Record nodeToRecord(JsonNode node) {
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("Невалидные данные!");
        }
        try {
            String group = node.get("group").asText();
            String type = node.get("type").asText();
            long weight = node.get("weight").asLong();
            return new Record(group, type, weight);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка парсинга JSON");
        }
    }
}
