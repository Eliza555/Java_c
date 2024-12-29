package huffmanBin;
import java.io.IOException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        HuffmanCoding hc = new HuffmanCoding();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Алгоритм Хаффмана");
        System.out.println("1. Кодировать");
        System.out.println("2. Декодировать");
        System.out.print("Выберите операцию (1 или 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Поглощение оставшегося символа новой строки

        try {
            if (choice == 1) {
                System.out.print("Введите путь к исходному файлу: ");
                String inputPath = scanner.nextLine();
                System.out.print("Введите путь к выходному файлу: ");
                String outputPath = scanner.nextLine();
                System.out.print("Вывести коды символов на экран? (y/n): ");
                String display = scanner.nextLine();
                boolean displayCodes = display.equalsIgnoreCase("y");
                hc.encode(inputPath, outputPath, displayCodes);
                System.out.println("Кодирование завершено.");
            } else if (choice == 2) {
                System.out.print("Введите путь к исходному файлу: ");
                String inputPath = scanner.nextLine();
                System.out.print("Введите путь к выходному файлу: ");
                String outputPath = scanner.nextLine();
                System.out.print("Вывести коды символов на экран? (y/n): ");
                String display = scanner.nextLine();
                boolean displayCodes = display.equalsIgnoreCase("y");
                hc.decode(inputPath, outputPath, displayCodes);
                System.out.println("Декодирование завершено.");
            } else {
                System.out.println("Неверный выбор.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
        }

        scanner.close();
    }
}