package client;

import client.command.ExecuteScriptCommand;
import client.command.ExitCommand;
import client.command.HelpCommand;
import command.Command;
import command.*;
import model.FuelType;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private Client client;
    private final Map<String, CommandBuilder> builders = new HashMap<>();

    public CommandFactory(Client client) {
        this.client=client;
        registerCommands();
    }

    public Command createCommand(String commandName, String[] args) {
        CommandBuilder builder = builders.get(commandName);
        if (builder == null) return null;
        Command command = builder.build(args);
        if (command instanceof ServerCommand &&
                !(command instanceof RegisterCommand) &&
                !(command instanceof LoginCommand)) {
            ((ServerCommand) command).setOwnerLogin(client.getCurrentLogin());
            ((ServerCommand) command).setPasswordHash(client.getCurrentPasswordHash());
        }
        return command;
    }

    private void registerCommands() {
        register("help", args -> new HelpCommand(client));

        register("info", args -> new InfoCommand());

        register("show", args -> new ShowCommand());

        register("add", args -> {
            AddCommand addCommand = new AddCommand();
            addCommand.setVehicle(client.getInputManager().readVehicle(false, null));
            return addCommand;
        });

        register("update", args -> {
            requireArgs(args, 1);
            long id = requireLong(args[0], "id");
            UpdateCommand updateCommand = new UpdateCommand();
            updateCommand.setId(id);
            updateCommand.setVehicle(client.getInputManager().readVehicle(false, null));
            return updateCommand;
        });

        register("remove_by_id", args -> {
            requireArgs(args, 1);
            long id = requireLong(args[0], "id");
            RemoveByIdCommand removeByIdCommand = new RemoveByIdCommand();
            removeByIdCommand.setId(id);
            return removeByIdCommand;
        });

        register("clear", args -> new ClearCommand());

        register("execute_script", args -> {
            requireArgs(args, 1);
            ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand(client);
            executeScriptCommand.setFileName(args[0]);
            return executeScriptCommand;
        });

        register("exit", args -> new ExitCommand(client));

        register("remove_first", args -> new RemoveFirstCommand());

        register("add_if_min", args -> {
            AddIfMinCommand addIfMinCommand = new AddIfMinCommand();
            addIfMinCommand.setVehicle(client.getInputManager().readVehicle(false, null));
            return addIfMinCommand;
        });

        register("remove_greater", args -> {
            RemoveGreaterCommand removeGreaterCommand = new RemoveGreaterCommand();
            removeGreaterCommand.setVehicle(client.getInputManager().readVehicle(false, null));
            return removeGreaterCommand;
        });

        register("count_by_fuel_type", args -> {
            requireArgs(args, 1);
            FuelType fuelType = requireEnum(args[0], FuelType.class);
            CountByFuelTypeCommand countByFuelTypeCommand = new CountByFuelTypeCommand();
            countByFuelTypeCommand.setFuelType(fuelType);
            return countByFuelTypeCommand;
        });

        register("count_greater_than_capacity", args -> {
            requireArgs(args, 1);
            float capacity = requireFloat(args[0], "capacity");
            CountGreaterThanCapacityCommand countGreaterThanCapacityCommand = new CountGreaterThanCapacityCommand();
            countGreaterThanCapacityCommand.setCapacity(capacity);
            return countGreaterThanCapacityCommand;
        });

        register("filter_contains_name", args -> {
            requireArgs(args, 1);
            String substring = args[0];
            FilterContainsNameCommand filterContainsNameCommand = new FilterContainsNameCommand();
            filterContainsNameCommand.setSubstring(substring);
            return filterContainsNameCommand;
        });


        register("login", args -> new LoginCommand());

        register("register", args -> new RegisterCommand());
    }

    private void register(String name, CommandBuilder builder) {
        builders.put(name, builder);
    }



    private void requireArgs(String[] args, int count) {
        if (args.length != count) {
            throw new IllegalArgumentException("Требуется " + count + " аргумент(ов)");
        }
    }

    private long requireLong(String value, String name) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " должен быть целым числом");
        }
    }

    private float requireFloat(String value, String name) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " должен быть числом с плавающей точкой");
        }
    }

    private <T extends Enum<T>> T requireEnum(String value, Class<T> enumClass) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимое значение для " + enumClass.getSimpleName() +
                    ". Доступны: " + String.join(", ", getEnumNames(enumClass)));
        }
    }

    private <T extends Enum<T>> String[] getEnumNames(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        String[] names = new String[constants.length];
        for (int i = 0; i < constants.length; i++) {
            names[i] = constants[i].name();
        }
        return names;
    }
}
