package org.infomaximum.reader;

import java.io.IOException;

public interface StreamReader<T> extends AutoCloseable {
    T readNext() throws IOException;
    boolean hasNext() throws IOException; //есть ли следующая запись
    void close() throws IOException; // освобождение/закрытие ресурса
}