package org.infomaximum;

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
        }
    }
}