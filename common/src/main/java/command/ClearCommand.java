package command;


import connection.CollectionManager;
import connection.Response;

public class ClearCommand implements ServerCommand {
    private static final long serialVersionUID = 105L;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clear : очистить коллекцию";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        if (collectionManager.size(ownerLogin) > 0) {
            collectionManager.clear(ownerLogin);
            return new Response(new String[]{"Коллекция текущего клиента очищена."});
        }
        else {
            return new Response(new String[]{"Коллекция текущего клиента была пуста."});
        }
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