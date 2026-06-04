package client.command;


import client.Client;
import command.Command;
import connection.CollectionManager;
import connection.Response;

import java.io.IOException;

public class ExitCommand implements Command {
    private Client client;

    public ExitCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "exit : завершить работу";
    }

    @Override
    public Response execute(CollectionManager collectionManager) throws IOException {
        client.exit();
        return null;
    }

}
