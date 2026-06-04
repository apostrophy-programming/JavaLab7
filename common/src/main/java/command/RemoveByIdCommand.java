package command;


import connection.CollectionManager;
import connection.Response;

public class RemoveByIdCommand implements ServerCommand {
    private static final long serialVersionUID = 104L;
    private Long id;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "remove_by_id id : удалить элемент из коллекции по его id";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        collectionManager.removeById(id, ownerLogin);
        return new Response(new String[]{"Элемент с id " + id + " удалён."});
    }

    public void setId(Long id) {
        this.id = id;
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