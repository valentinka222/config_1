import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Эмулятор командной строки UNIX-подобной ОС.
 * Реализует минимальный REPL с поддержкой
 * команд-заглушек ls, cd и exit (завершение работы)
 */

public class Main {
    /**
     * Точка входа в приложение. Выводит приглашение к вводу,
     * считывает команду пользователя, разбирает ее на
     * аргументы и выполняет соответствующую заглушку.
     *
     * @param args аргументы командной строки (пока не используется)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            greetUser();
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }

            ArrayList<String> parts = parseLine(line);
            String command = parts.getFirst();
            parts.removeFirst();

            switch (command) {
                case "ls":
                    if (parts.isEmpty()) {
                        System.out.println("ls");
                        break;
                    }
                    System.out.println("Команда ls вызвана с аргументами " + parts);
                    break;
                case "cd":
                    if (parts.isEmpty()) {
                        System.out.println("cd");
                        break;
                    }
                    System.out.println("Команда cd вызвана с аргументами " + parts);
                    break;
                case "exit":
                    System.out.println("Выполняется выход из программы...");
                    return;
                default:
                    System.out.println("Имя '" +command+ "' не распознано как имя командлета, функции, файла сценария"
                            + " или выполняемой программы.");
            }

        }
    }
    /**
     * Формирует и выводит приглашение к вводу, используя реальное
     * имя пользователя и хост текущей ОС.
     * Если имя хоста определить не удалось, используется
     * значение по умолчанию "hostname".
     */
    public static void greetUser() {
        String username = System.getProperty("user.name");
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception e) {
            hostname = "hostname";
        }

        String result = username + "@" + hostname + ":~$ ";
        System.out.print(result);
    }

    /**
     * Разбирает строку ввода на список аргументов с учетом текстовых
     * блоков в двойных кавычках: пробелы внутри кавычек не считаются
     * разделителями.
     *
     * @param line строка, введенная пользователем
     * @ return список аргументов, полученых после разбора строки
     */

    public static ArrayList<String> parseLine(String line) {
        ArrayList<String> args = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                insideQuotes = !insideQuotes;
            } else if (line.charAt(i) == ' ' && !insideQuotes) {
                if (!stringBuilder.isEmpty()) {
                    args.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
            } else {
                stringBuilder.append(line.charAt(i));
            }
        }
        if (!stringBuilder.isEmpty()) {
            args.add(stringBuilder.toString());
        }

        return args;
    }
}