package command;


import connection.CollectionManager;
import connection.Response;
import model.FuelType;

public class CountGreaterThanCapacityCommand implements ServerCommand {
    private static final long serialVersionUID = 110L;
    private float capacity;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "count_greater_than_capacity";
    }

    @Override
    public String getDescription() {
        return "count_greater_than_capacity capacity : вывести количество элементов, значение поля capacity которых больше заданного";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        int count = collectionManager.countGreaterThanCapacity(capacity, ownerLogin);
        return new Response(new String[]{"Количество элементов с capacity > " + capacity + ": " + count});
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
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