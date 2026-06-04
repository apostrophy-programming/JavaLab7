package client;

public class MainClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("client.MainClient аргументы: хост, порт");
            System.exit(1);
        }
        String host = args[0];
        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.err.println("Неверный формат порта, введено: " + args[1] + ". Введите целое число.");
        }
        System.out.printf("Хост: %s, порт: %d\n", host, port);
        Client client = new Client(host, port);
        client.start();
    }
}
