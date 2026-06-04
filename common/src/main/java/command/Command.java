package command;


import connection.CollectionManager;
import connection.Response;

import java.io.IOException;
import java.io.Serializable;

public interface Command extends Serializable {

    String getName();
    String getDescription();
    Response execute(CollectionManager collectionManager) throws IOException;
}
