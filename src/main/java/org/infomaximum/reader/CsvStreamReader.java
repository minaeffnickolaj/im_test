package org.infomaximum.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.infomaximum.entities.Record;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

// AutoCloseable для автоматического закрытия/освобождения ресурсов по завершению работы
public class CsvStreamReader implements StreamReader<Record>, AutoCloseable {
    private final CSVReader reader;
    private Iterator<String[]> iterator;

    public CsvStreamReader(String filePath) throws CsvException {
        try {
            this.reader = new CSVReader(new FileReader(filePath));
            this.iterator = reader.iterator();
            if (iterator.hasNext()) {
                iterator.next(); //пропустим заголовки
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CsvException("Не удалось открыть файл!");
        }
    }

    @Override
    public Record readNext() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException("Файл вычитан!");
        }
        String[] row = iterator.next();
        return rowToData(row);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    //переопределение close() из AutoCloseable
    @Override
    public void close() throws IOException {
        reader.close();
    }

    private Record rowToData(String[] row) {
        if (row == null || row.length < 3) { //проверяем на целостность
            throw new IllegalArgumentException("Битая строка, недостаточно данных");
        }
        try{
            String group = row[0];
            String type = row[1];
            //long number = Long.parseLong(row[2]);
            long weight = Long.parseLong(row[3]);
            return new Record(group, type, weight);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка парсинга строки");
        }
    }
}
