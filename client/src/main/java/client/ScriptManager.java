package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ScriptManager {
    private final Set<String> runningScripts = new HashSet<>();

    public void executeScript(String fileName, Client client) {
        File file;
        try {
            file = new File(fileName);
        } catch (NullPointerException e) {
            System.out.println("Файл скрипта не найден: " + fileName);
            return;
        }
        if (runningScripts.contains(file.getAbsolutePath())) {
            System.out.println("Обнаружена рекурсия скрипта! Выполнение остановлено.");
            return;
        }
        runningScripts.add(file.getAbsolutePath());
        String previousScript = client.getCurrentScriptFile();
        client.setCurrentScriptFile(file.getAbsolutePath());
        for (String name:runningScripts) {
            System.out.println(name);
        }


        Scanner oldScanner = client.getInputManager().getScanner();
        try (Scanner fileScanner = new Scanner(file)) {
            client.getInputManager().setScanner(fileScanner);
            client.getInputManager().setReadingInput(false);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                System.out.println("Выполняется: " + line);
                client.executeCommand(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка чтения файла скрипта: " + e.getMessage());
        } finally {
            client.getInputManager().setScanner(oldScanner);
            client.getInputManager().setReadingInput(true);
            runningScripts.remove(file.getAbsolutePath());
            client.setCurrentScriptFile(previousScript);
        }
    }

}