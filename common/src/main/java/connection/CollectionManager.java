package connection;

import model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.PriorityQueue;

public interface CollectionManager {

    LocalDate getInitializationDate();
    String getType();
    int size(String ownerLogin);
    void add(Vehicle vehicle, String ownerLogin);
    void update(Long id, Vehicle newVehicle, String ownerLogin);
    boolean removeById(Long id, String ownerLogin);
    void clear(String ownerLogin);
    Vehicle getMin(String ownerLogin);
    void removeFirst(String ownerLogin);
    List<Vehicle> getGreaterThan(Vehicle vehicle, String ownerLogin);
    int countByFuelType(FuelType fuelType, String ownerLogin);
    int countGreaterThanCapacity(float capacity, String ownerLogin);
    List<Vehicle> filterContainsName(String substring, String ownerLogin);
    Vehicle getById(Long id);
    List<Vehicle> getSortedList(String ownerLogin);
    boolean authenticate(String login, String passwordHash);
    boolean register(String login, String passwordHash);

}