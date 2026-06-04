package server;

import connection.CollectionManager;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class RealCollectionManager implements CollectionManager {
    private PriorityQueue<Vehicle> collection;
    private final LocalDate initializationDate;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public RealCollectionManager(String dbUrl, String dbUser, String dbPassword) {
        this.collection = new PriorityQueue<>();
        this.initializationDate = LocalDate.now();
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        initTables();
        loadCollection();
    }

    private void initTables() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "login VARCHAR(255) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(32) NOT NULL)";
        String sqlSeq = "CREATE SEQUENCE IF NOT EXISTS vehicles_id_seq START WITH 1";
        String sqlVehicles = "CREATE TABLE IF NOT EXISTS vehicles (" +
                "id BIGINT PRIMARY KEY DEFAULT nextval('vehicles_id_seq'), " +
                "name VARCHAR(255) NOT NULL, " +
                "x INTEGER NOT NULL, " +
                "y BIGINT NOT NULL, " +
                "creation_date DATE NOT NULL, " +
                "engine_power INTEGER, " +
                "capacity REAL NOT NULL, " +
                "type VARCHAR(20) NOT NULL, " +
                "fuel_type VARCHAR(20), " +
                "owner_id INTEGER REFERENCES users(id) NOT NULL)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement st = conn.createStatement()) {
            st.execute(sqlSeq);
            st.execute(sqlUsers);
            st.execute(sqlVehicles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean dbRegisterUser(String login, String passwordHash) {
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean dbAuthenticateUser(String login, String passwordHash) {
        String sql = "SELECT id FROM users WHERE login = ? AND password_hash = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void loadCollection() {
        PriorityQueue<Vehicle> vehicles = new PriorityQueue<>();
        String sql = "SELECT vehicles.*, users.login FROM vehicles " +
                "JOIN users ON vehicles.owner_id = users.id";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                Integer x = rs.getInt("x");
                Long y = rs.getLong("y");
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                Integer enginePower = rs.getObject("engine_power") != null ? rs.getInt("engine_power") : null;
                float capacity = rs.getFloat("capacity");
                VehicleType type = VehicleType.valueOf(rs.getString("type"));
                String fuelTypeStr = rs.getString("fuel_type");
                FuelType fuelType = fuelTypeStr != null ? FuelType.valueOf(fuelTypeStr) : null;
                String owner = rs.getString("login");
                Coordinates coordinates = new Coordinates(x, y);
                Vehicle v = new Vehicle(id, name, coordinates, creationDate, enginePower, capacity, type, fuelType, owner);
                vehicles.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        collection = vehicles;
        System.out.println("Коллекция загружена из базы данных");
    }


    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    public String getType() {
        return collection.getClass().getName();
    }

    public int size(String ownerLogin) {
        return collection.stream()
                .filter(v -> v.getOwner().equals(ownerLogin))
                .toList().size();
    }

    public void add(Vehicle vehicle, String ownerLogin) {
        vehicle.setCreationDate(LocalDate.now());
        vehicle.setOwner(ownerLogin);
        if (dbAddVehicle(vehicle, ownerLogin)) {
            lock.writeLock().lock();
            try {
                collection.add(vehicle);
            }
            finally {
                lock.writeLock().unlock();
            }
        }
    }


    public void update(Long id, Vehicle newVehicle, String ownerLogin) {
        newVehicle.setId(id);
        newVehicle.setCreationDate(LocalDate.now());
        newVehicle.setOwner(ownerLogin);
        if (dbUpdateVehicle(newVehicle, ownerLogin)) {
            lock.writeLock().lock();
            try {
                collection.removeIf(v -> v.getId().equals(id));
                collection.add(newVehicle);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    public boolean removeById(Long id, String ownerLogin) {
        if (dbRemoveVehicle(id, ownerLogin)) {
            lock.writeLock().lock();
            try {
                return collection.removeIf(v -> v.getId().equals(id));
            } finally {
                lock.writeLock().unlock();
            }
        }
        return false;
    }

    public void clear(String ownerLogin) {
        dbClearUserVehicles(ownerLogin);
        lock.writeLock().lock();
        try {
            collection.removeIf(v -> v.getOwner().equals(ownerLogin));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Vehicle getMin(String ownerLogin) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(v -> v.getOwner().equals(ownerLogin))
                    .min(Vehicle::compareTo)
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removeFirst(String ownerLogin) {
        lock.writeLock().lock();
        try {
            Vehicle toRemove = collection.stream()
                    .filter(v -> v.getOwner().equals(ownerLogin))
                    .min(Vehicle::compareTo)
                    .orElse(null);
            if (toRemove != null) {
                if (dbRemoveVehicle(toRemove.getId(), ownerLogin)) {
                    collection.remove(toRemove);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Vehicle> getGreaterThan(Vehicle vehicle, String ownerLogin) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(v -> v.getOwner().equals(ownerLogin) && v.compareTo(vehicle) > 0)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public int countByFuelType(FuelType fuelType, String ownerLogin) {
        lock.readLock().lock();
        try {
            return (int) collection.stream().filter(v -> v.getFuelType() == fuelType).count();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int countGreaterThanCapacity(float capacity, String ownerLogin) {
        lock.readLock().lock();
        try {
            return (int) collection.stream().filter(v -> v.getCapacity() > capacity).count();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Vehicle> filterContainsName(String substring, String ownerLogin) {
        lock.readLock().lock();
        try {
            return collection.stream().filter(v -> v.getName().contains(substring)).collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Vehicle getById(Long id) {
        lock.readLock().lock();
        try {
            return collection.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Vehicle> getSortedList(String ownerLogin) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getName))
                    .collect(Collectors.toList());
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean authenticate(String login, String passwordHash) {
        return dbAuthenticateUser(login, passwordHash);
    }

    @Override
    public boolean register(String login, String passwordHash) {
        return dbRegisterUser(login, passwordHash);
    }

    public boolean dbAddVehicle(Vehicle vehicle, String ownerLogin) {
        String sql = "INSERT INTO vehicles (name, x, y, creation_date, engine_power, capacity, type, fuel_type, owner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM users WHERE login = ?))";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehicle.getName());
            ps.setInt(2, vehicle.getCoordinates().getX());
            ps.setLong(3, vehicle.getCoordinates().getY());
            ps.setDate(4, Date.valueOf(vehicle.getCreationDate()));
            if (vehicle.getEnginePower() != null) ps.setInt(5, vehicle.getEnginePower()); else ps.setNull(5, Types.INTEGER);
            ps.setFloat(6, vehicle.getCapacity());
            ps.setString(7, vehicle.getType().toString());
            ps.setString(8, vehicle.getFuelType() != null ? vehicle.getFuelType().toString() : null);
            ps.setString(9, ownerLogin);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                vehicle.setId(keys.getLong(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dbUpdateVehicle(Vehicle vehicle, String ownerLogin) {
        String sql = "UPDATE vehicles SET name=?, x=?, y=?, creation_date=?, engine_power=?, capacity=?, type=?, fuel_type=? " +
                "WHERE id=? AND owner_id = (SELECT id FROM users WHERE login = ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getName());
            ps.setInt(2, vehicle.getCoordinates().getX());
            ps.setLong(3, vehicle.getCoordinates().getY());
            ps.setDate(4, Date.valueOf(vehicle.getCreationDate()));
            if (vehicle.getEnginePower() != null) ps.setInt(5, vehicle.getEnginePower()); else ps.setNull(5, Types.INTEGER);
            ps.setFloat(6, vehicle.getCapacity());
            ps.setString(7, vehicle.getType().toString());
            ps.setString(8, vehicle.getFuelType() != null ? vehicle.getFuelType().toString() : null);
            ps.setLong(9, vehicle.getId());
            ps.setString(10, ownerLogin);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dbRemoveVehicle(Long id, String ownerLogin) {
        String sql = "DELETE FROM vehicles WHERE id=? AND owner_id = (SELECT id FROM users WHERE login = ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, ownerLogin);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void dbClearUserVehicles(String ownerLogin) {
        String sql = "DELETE FROM vehicles WHERE owner_id = (SELECT id FROM users WHERE login = ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerLogin);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
