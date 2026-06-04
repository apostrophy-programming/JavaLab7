package command;



import connection.CollectionManager;
import connection.Response;
import model.Vehicle;

import java.util.List;

public class FilterContainsNameCommand implements ServerCommand {
    private static final long serialVersionUID = 111L;
    private String substring;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "filter_contains_name";
    }

    @Override
    public String getDescription() {
        return "filter_contains_name name : вывести элементы, значение поля name которых содержит заданную подстроку";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        List<Vehicle> filterList = collectionManager.filterContainsName(substring, ownerLogin);
        if (filterList.isEmpty()) {
            return new Response(new String[]{"Элементы, содержащие подстроку \"" + substring + "\", не найдены."});
        } else {
            return new Response(filterList.stream().map(Vehicle::toString).toArray(String[]::new));
        }
    }

    public void setSubstring(String substring) {
        this.substring = substring;
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