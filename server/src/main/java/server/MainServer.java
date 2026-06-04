package server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Использование: <port> <db_url> <db_user> <db_password>");
            System.err.println("Напоминание: 12345 jdbc:postgresql://pg:5432/studs user pass");

            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String dbUrl = args[1];
        String dbUser = args[2];
        String dbPassword = args[3];
        Server server = new Server(port, dbUrl, dbUser, dbPassword);
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
