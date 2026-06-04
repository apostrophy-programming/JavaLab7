package command;


import connection.CollectionManager;
import connection.Response;

public class InfoCommand implements ServerCommand {
    private static final long serialVersionUID = 112L;
    private String ownerLogin;
    private String passwordHash;


    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "info : вывести в стандартный поток вывода информацию о коллекции";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        return new Response(new String[]{
                "Тип коллекции: " + collectionManager.getType(),
                "Дата инициализации: " + collectionManager.getInitializationDate(),
                "Количество элементов текущего клиента: " + collectionManager.size(ownerLogin)
        });
    }

    @Override
    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String getOwnerLogin() {
        return ownerLogin;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }
}