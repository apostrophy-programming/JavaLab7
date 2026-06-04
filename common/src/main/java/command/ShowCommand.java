package command;


import connection.CollectionManager;
import connection.Response;
import model.Vehicle;

import java.util.List;

public class ShowCommand implements ServerCommand {
    private static final long serialVersionUID = 101L;
    private String ownerLogin;
    private String passwordHash;
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "show : вывести все элементы коллекции в строковом представлении";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        List<Vehicle> sorted = collectionManager.getSortedList(ownerLogin);
        if (sorted.isEmpty()) {
            return new Response(new String[]{"Коллекция пуста."});
        } else {
            return new Response(sorted.stream().map(Vehicle::toString).toArray(String[]::new));
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