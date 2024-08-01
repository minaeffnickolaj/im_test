package org.infomaximum.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AsciiString {
    private byte[] bytes;

    public AsciiString(String string){
        if (string.length() > 14) {
            int capacity = string.length() / 2; //т.к. режем объем по байтам на 2
            enhance(capacity);
            stringToASCII(string);
        } else {
            bytes = new byte[14];
            stringToASCII(string);
        }
    }

    public void stringToASCII(String string){
        for (char c : string.toCharArray()) {
            if (c > 127) { // 0..127 - 1 байт
                throw new IllegalArgumentException("Символ вне ASCII диапазона");
            }
        }
         bytes = string.getBytes(StandardCharsets.US_ASCII);
    }

    //предусмотрим на случай если не влезаем в 14 символов
    private void enhance(int newSize) {
        bytes = new byte[newSize];
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString(){
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AsciiString string = (AsciiString) obj; //каст к сокращенной строке
        return Arrays.equals(bytes, string.bytes);
    }
}

