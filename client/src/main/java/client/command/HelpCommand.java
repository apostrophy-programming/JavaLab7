package client.command;


import client.Client;
import command.Command;
import connection.CollectionManager;
import connection.Response;

import java.io.IOException;

public class HelpCommand implements Command {
    private Client client;

    public HelpCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "help : вывести справку по доступным командам";
    }

    @Override
    public Response execute(CollectionManager collectionManager) throws IOException {
        System.out.println("Доступные команды:");
        for (Command command: client.getCommands().values()) {
            System.out.println(command.getDescription());
        }
        return null;
    }

}
