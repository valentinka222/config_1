import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Эмулятор командной строки UNIX-подобной ОС.
 * Реализует минимальный REPL с поддержкой
 * команд-заглушек ls, cd и exit, параметров командной
 * строки (путь к VFS, путь к стартовому скрипту)
 * и выполнения стартового скрипта.
 */
public class Main {
    private static VfsFolder vfsRoot;
    private static List<VfsFolder> pathStack = new ArrayList<>();
    private static final int CLEAR_LINES_COUNT = 50;

    /**
     * Точка входа в приложение. Разбирает аргументы командной
     * строки, выводит их, при наличии стартового скрипта выполняет
     * его, после чего переходит в режим REPL.
     *
     * @param args аргументы командной строки: --vfs-path, --script
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<String> paths = checkParams(args);
        String vfsPath = paths.get(0);
        String scriptPath = paths.get(1);

        printParams(vfsPath, scriptPath);

        if (vfsPath != null) {
            loadVfs(vfsPath);
        }

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
     * Проходится по аргументам командной строки и сохраняет путь
     * к VFS и путь к стартовому скрипту
     * @param args аргументы командной строки: --vfs-path, --script
     */
    public static ArrayList<String> checkParams(String[] args) {
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
        ArrayList<String> paths = new ArrayList<>();
        paths.add(vfsPath);
        paths.add(scriptPath);
        return paths;
    }

    /**
     * Загружает VFS по указанному пути и сохраняет в поле vfsRoot. Если
     * загрузка не удалась, возвращает сообщение об ошибке
     * @param vfsPath путь к XML файлу
     */
    public static void loadVfs(String vfsPath) {
        try {
            vfsRoot = VfsLoader.load(vfsPath);
            pathStack.clear();
            pathStack.add(vfsRoot);
            System.out.println("VFS успешно загружена:");
            printTree(vfsRoot, 0);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки VFS");
        }
    }

    /**
     * Выводит в консоль структуру дерева с отступом, отражающим уровень
     * вложенности
     * @param node узел дерева, с которого начинаем вывод
     * @param depth текущий уровень вложенности
     */
    public static void printTree(VfsNode node, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + node.getName());

        if (node instanceof VfsFolder folder) {
            for (VfsNode child : folder.getChildren()) {
                printTree(child, depth+1);
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
     * @return список аргументов, полученых после разбора строки
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
     * Ищет среди детей папки узел с именем.
     * @param folder папка, в которой ищем
     * @param name искомое имя
     * @return найденый узел, либо null
     */
    private static VfsNode findChild(VfsFolder folder, String name) {
        for (VfsNode child : folder.getChildren()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Меняет текущую папку. Поддерживает переход в подпапку по имени
     * и подъем на уровень через "..".
     * @param args аргументы команды: имя папки или ".."
     */
    public static void handleCd(ArrayList<String> args) {
        if (vfsRoot == null) {
            System.out.println("VFS не загружена");
            return;
        }
        if (args.isEmpty()) {
            pathStack.clear();
            pathStack.add(vfsRoot);
            return;
        }
        String name = args.getFirst();
        if (name.equals("..")) {
            if (pathStack.size() > 1) {
                pathStack.removeLast();
            }
            return;
        }
        VfsFolder currentFolder = pathStack.getLast();
        VfsNode found = findChild(currentFolder, name);
        if (found == null) {
            System.out.println("Папка " + name + " не найдена");
            return;
        }
        if (found instanceof VfsFolder folder) {
            pathStack.add(folder);
        } else {
            System.out.println(name + " не является папкой");
        }
    }

    /**
     * Выводит содержимое текущей папки. Папки помечаются слэшем в конце.
     * @param args аргументы команды (пока не используются)
     */
    public static void handleLs(ArrayList<String> args) {
        if (vfsRoot == null) {
            System.out.println("VFS не загружена");
            return;
        }
        VfsFolder current = pathStack.getLast();
        for (VfsNode child : current.getChildren()) {
            if (child instanceof VfsFolder) {
                System.out.println(child.getName() + "/");
            } else {
                System.out.println(child.getName());
            }
        }
    }

    /**
     * Выводит текущий путь внутри VFS от корня.
     */
    public static void handlePwd() {
        StringBuilder path = new StringBuilder();
        for (VfsFolder folder : pathStack) {
            path.append("/").append(folder.getName());
        }
        System.out.println(path);
    }

    /**
     * Очищает экран терминала
     */
    public static void handleClear() {
        for (int i = 0; i < CLEAR_LINES_COUNT; i++) {
            System.out.println();
        }
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
                handleLs(args);
                break;
            case "cd":
                handleCd(args);
                break;
            case "clear":
                handleClear();
                break;
            case "pwd":
                handlePwd();
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