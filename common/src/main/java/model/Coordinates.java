package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Класс координат транспортного средства.
 * Хранит координаты x и y.
 * @author Max
 */

public class Coordinates implements Serializable {

    @Serial
    private static final long serialVersionUID = 2000000L;
    private Integer x; // Поле не может быть null
    private Long y; // Поле не может быть null, значение должно быть больше -289

    /**
     * Конструктор по умолчанию.
     */
    public Coordinates() {}

    /**
     * Конструктор с валидацией полей.
     * @param x координата x, не null
     * @param y координата y, не null и > -289
     * @throws IllegalArgumentException если проверки не пройдены
     */
    public Coordinates(Integer x, Long y) {
        setX(x);
        setY(y);
    }

    /**
     * Возвращает координату X.
     * @return координата X
     */
    public Integer getX() {
        return x;
    }

    /**
     * Устанавливает координату x.
     *
     * @param x новое значение, не null
     * @throws IllegalArgumentException если x == null
     */
    public void setX(Integer x) {
        if (x == null) throw new IllegalArgumentException("x не может быть null");
        this.x = x;
    }

    /**
     * Возвращает координату Y.
     * @return координата Y
     */
    public Long getY() {
        return y;
    }

    /**
     * Устанавливает координату y.
     *
     * @param y новое значение, не null и > -289
     * @throws IllegalArgumentException если проверки не пройдены
     */
    public void setY(Long y) {
        if (y == null) throw new IllegalArgumentException("y не может быть null");
        if (y <= -289) throw new IllegalArgumentException("y должно быть больше -289");
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}