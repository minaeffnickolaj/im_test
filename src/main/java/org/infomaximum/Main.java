package org.infomaximum;

import com.opencsv.exceptions.CsvException;
import org.infomaximum.reader.CsvStreamReader;
import org.infomaximum.reader.JsonStreamReader;
import org.infomaximum.statistics.Stats;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        while (true){
            System.out.println("Введите путь к файлу или \"exit\" для выхода: ");
            String command = userInput.nextLine();
            if (command.equals("exit")){
                break;
            }
            if (command.endsWith(".csv")) {
                Stats stats = new Stats();
                try ( CsvStreamReader reader = new CsvStreamReader(command)){
                    stats.readFile(reader);
                    stats.printStats();
                } catch (CsvException | IOException e) {

                }
            } else if (command.endsWith(".json")) {
                Stats stats = new Stats();
                try (JsonStreamReader reader = new JsonStreamReader(command)){
                    stats.readFile(reader);
                    stats.printStats();
                } catch (IOException e) {

                }
            }
        }
    }
}