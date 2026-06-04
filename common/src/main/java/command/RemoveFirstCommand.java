package command;


import connection.CollectionManager;
import connection.Response;

public class RemoveFirstCommand implements ServerCommand {
    private static final long serialVersionUID = 106L;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "remove_first";
    }

    @Override
    public String getDescription() {
        return "remove_first : удалить первый элемент из коллекции";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        if (collectionManager!=null) {
            if (collectionManager.size(ownerLogin) > 0) {
                collectionManager.removeFirst(ownerLogin);
                return new Response(new String[]{"Первый элемент удалён."});
            }
            else {
                return new Response(new String[]{"Коллекция текущего клиента была пуста."});
            }
        }
        return null;
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