package command;
import connection.CollectionManager;
import connection.Response;

public class LoginCommand implements ServerCommand {
    private static final long serialVersionUID = 201L;
    private String ownerLogin;
    private String passwordHash;


    @Override
    public String getName() { return "login"; }
    @Override
    public String getDescription() { return "login : войти в систему"; }

    @Override
    public Response execute(CollectionManager collectionManager) {
        boolean ok = collectionManager.authenticate(ownerLogin, passwordHash);
        if (ok) return new Response(new String[]{"OK"});
        else return new Response(new String[]{"Неверный логин или пароль"});
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
