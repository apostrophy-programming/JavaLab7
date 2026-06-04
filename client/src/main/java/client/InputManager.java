package client;

import model.*;

import java.util.Scanner;

public class InputManager {
    private Scanner scanner;
    private boolean isReadingInput = true;

    public InputManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public Vehicle readVehicle(boolean isUpdate, Long existingId) {
        if (isReadingInput) {
            String name = readString("Введите name (не может быть пустым): ", false, true);

            System.out.println("Введите coordinates:");
            Integer x = readInt("  x (целое число, не null): ", false, null, null);
            Long y = readLong("  y (целое число > -289, не null): ", false, -289L, null);
            Coordinates coordinates = new Coordinates(x, y);

            Integer enginePower = readInt("Введите enginePower (целое > 0, может быть пустым): ", true, 1, null);
            float capacity = readFloat("Введите capacity (число > 0): ", false, 0.0f, null);

            System.out.println("Доступные VehicleType: CAR, DRONE, SUBMARINE, SHIP");
            VehicleType type = readEnum("Введите type (одно из значений): ", VehicleType.class, false);

            System.out.println("Доступные FuelType: GASOLINE, MANPOWER, PLASMA, ANTIMATTER (можно оставить пустым)");
            FuelType fuelType = readEnum("Введите fuelType: ", FuelType.class, true);

            return new Vehicle(null, name, coordinates, null, enginePower, capacity, type, fuelType, "");
        }
        else {
            String name = readString("", false, true);
            Integer x = readInt("", false, null, null);
            Long y = readLong("", false, -289L, null);
            Coordinates coordinates = new Coordinates(x, y);
            Integer enginePower = readInt("", true, 1, null);
            float capacity = readFloat("", false, 0.0f, null);
            VehicleType type = readEnum("", VehicleType.class, false);
            FuelType fuelType = readEnum("", FuelType.class, true);

            return new Vehicle(null, name, coordinates, null, enginePower, capacity, type, fuelType, "");
        }
    }

    public String readString(String prompt, boolean nullable, boolean nonEmpty) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                if (nullable) return null;
                System.out.println("Ошибка: строка не может быть пустой.");
                continue;
            }
            if (nonEmpty && line.isEmpty()) {
                System.out.println("Ошибка: строка не может быть пустой.");
                continue;
            }
            return line;
        }
    }

    public Integer readInt(String prompt, boolean nullable, Integer min, Integer max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty() && nullable) return null;
            try {
                int val = Integer.parseInt(line);
                if (min != null && val <= min) {
                    System.out.println("Ошибка: значение должно быть больше " + min);
                    continue;
                }
                if (max != null && val >= max) {
                    System.out.println("Ошибка: значение должно быть меньше " + max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    public Long readLong(String prompt, boolean nullable, Long min, Long max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty() && nullable) return null;
            try {
                long val = Long.parseLong(line);
                if (min != null && val <= min) {
                    System.out.println("Ошибка: значение должно быть больше " + min);
                    continue;
                }
                if (max != null && val >= max) {
                    System.out.println("Ошибка: значение должно быть меньше " + max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    public Float readFloat(String prompt, boolean nullable, Float min, Float max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty() && nullable) return null;
            try {
                float val = Float.parseFloat(line);
                if (min != null && val <= min) {
                    System.out.println("Ошибка: значение должно быть больше " + min);
                    continue;
                }
                if (max != null && val >= max) {
                    System.out.println("Ошибка: значение должно быть меньше " + max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }

    public <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, boolean nullable) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.isEmpty() && nullable) return null;
            try {
                return Enum.valueOf(enumClass, line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: допустимые значения: " + java.util.Arrays.toString(enumClass.getEnumConstants()));
            }
        }
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setReadingInput(boolean readingInput) {
        isReadingInput = readingInput;
    }
}