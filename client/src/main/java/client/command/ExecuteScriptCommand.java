package client.command;



import client.Client;
import command.Command;
import connection.CollectionManager;
import connection.Response;

import java.io.IOException;

public class ExecuteScriptCommand implements Command {
    private Client client;
    private String fileName;

    public ExecuteScriptCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "execute_script : выполнить скрипт из файла";
    }

    @Override
    public Response execute(CollectionManager collectionManager) throws IOException {
        client.getScriptManager().executeScript(fileName, client);
        return null;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
