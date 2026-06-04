package client;

import client.command.*;
import command.*;
import connection.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Client {
    private String host;
    private int port;
    private Socket socket;
    private final int connectTimeout = 5000;
    private final int reconnectDelay = 3000;
    private final int maxReconnectAttempts = 5;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private final Map<String, Command> commands;
    private final InputManager inputManager;
    private final ScriptManager scriptManager;
    private final Scanner scanner;
    private final CommandFactory commandFactory;
    private String currentScriptFile;
    private String currentLogin="";
    private String currentPasswordHash="";

    public Client (String host, int port) {
        this.host = host;
        this.port = port;
        scanner = new Scanner(System.in);
        this.inputManager = new InputManager(scanner);
        this.commands = new LinkedHashMap<>();
        commandFactory = new CommandFactory(this);
        scriptManager = new ScriptManager();
        initCommands();

    }

    private synchronized void initCommands() {
        commands.put("help", new HelpCommand(this));
        commands.put("exit", new ExitCommand(this));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("login", new LoginCommand());
        commands.put("register", new RegisterCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("add", new AddCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("clear", new ClearCommand());
        commands.put("remove_first", new RemoveFirstCommand());
        commands.put("add_if_min", new AddIfMinCommand());
        commands.put("remove_greater", new RemoveGreaterCommand());
        commands.put("count_by_fuel_type", new CountByFuelTypeCommand());
        commands.put("count_greater_than_capacity", new CountGreaterThanCapacityCommand());
        commands.put("filter_contains_name", new FilterContainsNameCommand());
    }

    public void start() {
        try {
            connect();
        } catch (IOException e) {
            try {
                reconnect();
            } catch (IOException ex) {
                exit();
                System.out.println("Попробуйте позже");
                throw new RuntimeException(ex);
            }
        }
        System.out.println("Введите help для справки:");
        System.out.print("> ");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            executeCommand(line);
            System.out.print("> ");
        }
        exit();
    }

    public void executeCommand(String line) {
        String[] parts = line.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String argsLine = parts.length > 1 ? parts[1] : "";
        String[] args = argsLine.isEmpty() ? new String[0] : argsLine.split("\\s+");
        Command command = commands.get(commandName);
        if (command==null) {
            System.out.println("Неизвестная команда. Введите 'help' для справки.");
            return;
        }

        Command newCommand;
        if (command instanceof LoginCommand) {
            login();
            return;
        }
        if (command instanceof RegisterCommand) {
            register();
            return;
        }
        if (command instanceof ServerCommand && !checkAuthentication()) {
            System.out.println("Для выполнения этой команды необходимо авторизоваться - login или register");
            return;
        }
        try {
            newCommand = commandFactory.createCommand(command.getName(), args);
        }
        catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (newCommand instanceof ServerCommand) {
            try {
                if (newCommand !=null) {
                    sendCommand(newCommand);
                    connection.Response response = receiveResponse();
                    for (String responseLine: response.getText()) {
                        System.out.println(responseLine);
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка в аргументах команды: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка при выполнении команды: " + e.getMessage());
            }
        }
        else {
            try {
                newCommand.execute(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void exit() {
        scanner.close();
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия сокета: " + e.getMessage());
        }
        System.out.println("Завершение программы...");
        System.exit(0);
    }

    public void sendCommand (Command command) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
        dataOutputStream.flush();
    }

    public Response receiveResponse() throws IOException, ClassNotFoundException {
        int length = dataInputStream.readInt();
        byte[] data = new byte[length];
        dataInputStream.readFully(data);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Response) objectInputStream.readObject();
        }
    }

    private void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), connectTimeout);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    private void reconnect() throws IOException {
        int attempt = 0;
        while (attempt < maxReconnectAttempts) {
            try {
                connect();
                System.out.println("Соединение установлено.");
                return;
            } catch (IOException e) {
                attempt++;
                System.out.println("Не удалось переподключиться (попытка " + attempt + " из " + maxReconnectAttempts + "): " + e.getMessage());
                if (attempt == maxReconnectAttempts) {
                    throw new IOException("Не удалось восстановить соединение после " + maxReconnectAttempts + " попыток", e);
                }
                try {
                    Thread.sleep(reconnectDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Операция прервана", ie);
                }
            }
        }
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public void setCurrentScriptFile(String currentScriptFile) {
        this.currentScriptFile = currentScriptFile;
    }

    public String getCurrentScriptFile() {
        return currentScriptFile;
    }


    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkAuthentication() {
        if (currentLogin.trim().equals("") || currentPasswordHash.trim().equals("")) {
            return false;
        }
        try {
            LoginCommand loginCommand = new LoginCommand();
            loginCommand.setOwnerLogin(currentLogin);
            loginCommand.setPasswordHash(currentPasswordHash);
            sendCommand(loginCommand);
            Response response = receiveResponse();
            if (response.getText()[0].equals("OK")) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        String passwordHash = md5(password);
        LoginCommand loginCommand = new LoginCommand();
        loginCommand.setOwnerLogin(login);
        loginCommand.setPasswordHash(passwordHash);
        try {
            sendCommand(loginCommand);
            Response response = receiveResponse();
            if (response.getText()[0].equals("OK")) {
                currentLogin = login;
                currentPasswordHash = passwordHash;
                System.out.println("Добро пожаловать, " + currentLogin);
                return true;
            } else {
                System.out.println("Ошибка: " + response.getText()[0]);
                System.out.print("Зарегистрироваться? (y/n): ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("y")) {
                    register(login, passwordHash);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка соединения: " + e.getMessage());
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    public boolean register (String login, String passwordHash) {
        RegisterCommand registerCommand = new RegisterCommand();
        registerCommand.setOwnerLogin(login);
        registerCommand.setPasswordHash(passwordHash);
        try {
            sendCommand(registerCommand);
            Response response = receiveResponse();
            if (response.getText()[0].equals("OK")) {
                currentLogin = login;
                currentPasswordHash = passwordHash;
                System.out.println("Регистрация успешна!");
                return true;
            } else {
                System.out.println("Ошибка регистрации: " + response.getText()[0]);
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean register () {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        String passwordHash = md5(password);
        RegisterCommand registerCommand = new RegisterCommand();
        registerCommand.setOwnerLogin(login);
        registerCommand.setPasswordHash(passwordHash);
        try {
            sendCommand(registerCommand);
            Response response = receiveResponse();
            if (response.getText()[0].equals("OK")) {
                currentLogin = login;
                currentPasswordHash = passwordHash;
                System.out.println("Регистрация успешна!");
                return true;
            } else {
                System.out.println("Ошибка регистрации: " + response.getText()[0]);
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentLogin() {
        return currentLogin;
    }

    public String getCurrentPasswordHash() {
        return currentPasswordHash;
    }
}

