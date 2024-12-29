package huffmanBin;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class delete {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Запрос пути к файлу у пользователя
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        // Проверка существования файла
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.out.println("Файл не существует.");
            scanner.close();
            return;
        }

        // Запрос количества символов для удаления
        System.out.print("Введите количество символов для удаления: ");
        int numCharsToDelete;
        try {
            numCharsToDelete = Integer.parseInt(scanner.nextLine());
            if (numCharsToDelete < 0) {
                System.out.println("Количество символов должно быть неотрицательным.");
                scanner.close();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод числа.");
            scanner.close();
            return;
        }

        // Чтение содержимого файла
        String content;
        try {
            content = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            scanner.close();
            return;
        }

        // Проверка, чтобы количество символов для удаления не превышало длину файла
        if (numCharsToDelete > content.length()) {
            System.out.println("Количество символов для удаления превышает длину файла.");
            scanner.close();
            return;
        }

        // Удаление символов из начала содержимого
        String modifiedContent = content.substring(numCharsToDelete);

        // Запись измененного содержимого обратно в файл
        try {
            Files.write(path, modifiedContent.getBytes());
            System.out.println("Удалено " + numCharsToDelete + " символов из файла.");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }

        scanner.close();
    }
}