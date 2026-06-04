package command;


import connection.CollectionManager;
import connection.Response;
import model.Vehicle;

public class AddIfMinCommand implements ServerCommand {
    private static final long serialVersionUID = 107L;
    private Vehicle vehicle;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescription() {
        return "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        if (collectionManager.size(ownerLogin)==0) {
            collectionManager.add(vehicle, "");
            return new Response(new String[]{"Коллекция текущего клиента была пуста. Добавлен элемент"});
        }
        else {
            Vehicle min = collectionManager.getMin(ownerLogin);
            if (vehicle.compareTo(min)<0) {
                collectionManager.add(vehicle, ownerLogin);
                return new Response(new String[]{"Элемент добавлен как минимальный."});
            }
            else return new Response(new String[]{"Элемент не добавлен, так как не является минимальным."});
        }
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