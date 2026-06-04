package command;


import connection.CollectionManager;
import connection.Response;
import model.FuelType;

public class CountByFuelTypeCommand implements ServerCommand {
    private static final long serialVersionUID = 109L;
    private FuelType fuelType;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "count_by_fuel_type";
    }

    @Override
    public String getDescription() {
        return "count_by_fuel_type fuelType : вывести количество элементов, значение поля fuelType которых равно заданному";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        int count = collectionManager.countByFuelType(fuelType, ownerLogin);
        return new Response(new String[]{"Количество элементов с fuelType " + fuelType + ": " + count});
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
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