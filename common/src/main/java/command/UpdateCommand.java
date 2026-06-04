package command;


import connection.CollectionManager;
import connection.Response;
import model.Vehicle;

public class UpdateCommand implements ServerCommand {
    private static final long serialVersionUID = 103L;
    private Vehicle vehicle;
    private Long id;
    private String ownerLogin;
    private String passwordHash;

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
    }

    @Override
    public Response execute(CollectionManager collectionManager) {
        if (vehicle == null) {
            return new Response(new String[]{"Ошибка: данные транспортного средства не заданы."});
        }
        Vehicle existing = collectionManager.getById(id);
        if (existing == null) {
            return new Response(new String[]{"Нет элемента Vehicle с заданным id"});
        }
        if (!existing.getOwner().equals(ownerLogin)) {
            return new Response(new String[]{"Нельзя обновить элемент с заданным id, т.к. этот элемент Вам не принадлежит"});
        }
        collectionManager.update(id, vehicle, ownerLogin);
        return new Response(new String[]{"Обновлён элемент: " + vehicle.getName()});
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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