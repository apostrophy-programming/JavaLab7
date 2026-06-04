package command;

import connection.CollectionManager;
import connection.Response;

public class RegisterCommand implements ServerCommand {
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() { return "register"; }
    @Override
    public String getDescription() { return "register : зарегистрировать нового пользователя"; }

    @Override
    public Response execute(CollectionManager collectionManager) {
        boolean success = collectionManager.register(ownerLogin, passwordHash);
        if (success) return new Response(new String[]{"OK"});
        else return new Response(new String[]{"Пользователь уже существует"});
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