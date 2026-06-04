package command;


import connection.CollectionManager;
import connection.Response;
import model.Vehicle;

public class AddCommand implements ServerCommand {
    private static final long serialVersionUID = 102L;
    private Vehicle vehicle;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add {element} : добавить новый элемент в коллекцию";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        collectionManager.add(vehicle, ownerLogin);
        if (vehicle!=null) {
            return new Response(new String[]{"Добавлен элемент: " + vehicle.getName()});
        }
        return null;
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