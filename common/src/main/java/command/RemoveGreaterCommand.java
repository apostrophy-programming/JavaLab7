package command;


import connection.CollectionManager;
import connection.Response;
import model.Vehicle;

import java.util.List;

public class RemoveGreaterCommand implements ServerCommand {
    private static final long serialVersionUID = 108L;
    private Vehicle vehicle;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        List<Vehicle> toRemove = collectionManager.getGreaterThan(vehicle, ownerLogin);
        if (toRemove.isEmpty()) {
            return new Response(new String[]{"Нет элементов, превышающих заданный."});
        }
        for (Vehicle vehicle : toRemove) {
            collectionManager.removeById(vehicle.getId(), ownerLogin);
        }
        return new Response(new String[]{"Удалено элементов: " + toRemove.size()});
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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