import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Эмулятор командной строки UNIX-подобной ОС.
 * Реализует минимальный REPL с поддержкой
 * команд-заглушек ls, cd и exit, параметров командной
 * строки (путь к VFS, путь к стартовому скрипту)
 * и выполнения стартового скрипта.
 */
public class Main {
    /**
     * Точка входа в приложение. Разбирает аргументы командной
     * строки, выводит их, при наличии стартового скрипта выполняет
     * его, после чего переходит в режим REPL.
     *
     * @param args аргументы командной строки: --vfs-path, --script
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String vfsPath = null;
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--vfs-path":
                    if ((i+1) < args.length) {
                        vfsPath = args[++i];
                    }
                    break;
                case "--script":
                    if ((i+1) < args.length) {
                        scriptPath = args[++i];
                    }
                    break;
                default:
                    System.out.println("Неизвестная команда");
            }
        }

        printParams(vfsPath, scriptPath);

        if (scriptPath != null) {
            runScript(scriptPath);
        }

        while (true) {
            greetUser();
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }

            ArrayList<String> parts = parseLine(line);
            String command = parts.getFirst();
            parts.removeFirst();

            if (executeCommand(command, parts)) {
                return;
            }

        }
    }
    /**
     * Выводит в консоль все параметры запуска эмулятора.
     *
     * @param vfsPath путь к VFS
     * @param scriptPath путь к стартовому скрипту
     */
    public static void printParams(String vfsPath, String scriptPath) {
        System.out.println("Параметры запуска:");
        if (vfsPath == null) {
            System.out.println("Путь к VFS не задан");
        } else {
            System.out.println("Путь к VFS: " + vfsPath);
        }

        if (scriptPath == null) {
            System.out.println("Путь к стартовому скрипту не задан");
        } else {
            System.out.println("Путь к стартовому скрипту: " + scriptPath);
        }
    }

    /**
     * Выполняет команды их стартового скрипта. Ошибочные строки
     * (неизвестные команды) не прерывают выполнение скрипта.
     *
     * @param scriptPath путь к файлу стартового скрипта
     */
    public static void runScript(String scriptPath) {
        File file = new File(scriptPath);
        if (!file.exists()) {
            System.out.println("Стартовый скрипт не найден");
            return;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                greetUser();
                System.out.println(line);

                ArrayList<String> parts = parseLine(line);
                String command = parts.getFirst();
                parts.removeFirst();

                if (executeCommand(command, parts)) {
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения стартового скрипта");
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

    /**
     * Выполняет одну команду с переданными аргументами
     *
     * @param command имя команды
     * @param args аргументы команды
     * @return true, если нужно завершить работу (exit)
     */
    public static boolean executeCommand(String command, ArrayList<String> args) {
        switch (command) {
            case "ls":
                if (args.isEmpty()) {
                    System.out.println("ls");
                    break;
                }
                System.out.println("Команда ls вызвана с аргументами " + args);
                break;
            case "cd":
                if (args.isEmpty()) {
                    System.out.println("cd");
                    break;
                }
                System.out.println("Команда cd вызвана с аргументами " + args);
                break;
            case "exit":
                System.out.println("Выполняется выход из программы...");
                return true;
            default:
                System.out.println("Имя '" + command + "' не распознано как имя командлета, функции, файла сценария"
                        + " или выполняемой программы.");
        }
        return false;
    }
}