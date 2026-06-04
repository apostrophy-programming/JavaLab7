package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Класс транспортного средства. Является основным элементом коллекции.
 * Реализует {@link Comparable} для сортировки по id.
 *
 * @author Max
 */
public class Vehicle implements Comparable<Vehicle>, Serializable {
    @Serial
    private static final long serialVersionUID = 1000000L;
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer enginePower; //Поле может быть null, Значение поля должно быть больше 0
    private float capacity; //Значение поля должно быть больше 0
    private VehicleType type; //Поле не может быть null
    private FuelType fuelType; //Поле может быть null
    private String owner;

    /**
     * Конструктор с параметрами.
     *
     * @param id           идентификатор
     * @param name         название
     * @param coordinates  координаты
     * @param creationDate дата создания
     * @param enginePower  мощность
     * @param capacity     вместимость
     * @param type         тип транспортного средства
     * @param fuelType     тип топлива
     * @param owner        владелец
     */
    public Vehicle(Long id, String name, Coordinates coordinates, LocalDate creationDate,
                   Integer enginePower, float capacity, VehicleType type, FuelType fuelType, String owner) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.capacity = capacity;
        this.type = type;
        this.fuelType = fuelType;
        this.owner = owner;
    }

    /**
     * @return идентификатор
     * */
    public Long getId() { return id; }

    /**
     * Устанавливает идентификатор.
     *
     * @param id идентификатор
     * */
    public void setId(Long id) { this.id = id; }

    /**
     * @return название
     * */
    public String getName() { return name; }
    /**
     * Устанавливает название.
     *
     * @param name название
     * */
    public void setName(String name) { this.name = name; }

    /**
     * @return координаты.
     * */
    public Coordinates getCoordinates() {
        return coordinates;
    }
    /**
     * Устанавливает координаты.
     *
     * @param coordinates новые координаты
     * */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
    /**
     * @return дата создания.
     * */
    public LocalDate getCreationDate() { return creationDate; }
    /**
     * Устанавливает дату создания.
     *
     * @param creationDate дата создания
     * */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    /**
     *  @return мощность двигателя.
     * */
    public Integer getEnginePower() {
        return enginePower;
    }
    /**
     * Устанавливает мощность двигателя.
     *
     * @param enginePower мощность двигателя
     * */
    public void setEnginePower(Integer enginePower) {
        this.enginePower = enginePower;
    }
    /**
     * @return вместимость.
     * */
    public float getCapacity() {
        return capacity;
    }
    /**
     * Устанавливает вместимость.
     *
     * @param capacity вместимость
     * */
    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }
    /**
     * @return тип транспорта.
     * */
    public VehicleType getType() {
        return type;
    }
    /**
     * Устанавливает тип транспорта.
     *
     * @param type тип транспорта
     * */
    public void setType(VehicleType type) {
        this.type = type;
    }

    /**
     * @return  тип топлива.
     * */
    public FuelType getFuelType() {
        return fuelType;
    }
    /**
     * Устанавливает тип топлива.
     *
     * @param fuelType тип топлива
     * */
    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    /**
     * Сравнивает два транспортных средства по названию.
     *
     * @param o объект для сравнения
     * @return отрицательное, ноль или положительное число
     */
    @Override
    public int compareTo(Vehicle o) {
        int compareEnginePower = Integer.compare(this.enginePower, o.enginePower);
        int compareCapacity = Float.compare(this.capacity, o.capacity);
        if (compareEnginePower !=0) {
            return compareEnginePower;
        }
        else if (compareCapacity !=0) {
            return Float.compare(this.capacity, o.capacity);
        }
        else {
            return id.compareTo(o.getId());
        }
    }

    /**
     * Возвращает строковое представление объекта.
     *
     * @return строка с полями
     */
    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", enginePower=" + enginePower +
                ", capacity=" + capacity +
                ", type=" + type +
                ", fuelType=" + fuelType +
                ", owner=" + owner +
                '}';
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
