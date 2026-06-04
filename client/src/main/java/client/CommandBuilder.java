package client;

import command.Command;

@FunctionalInterface
public interface CommandBuilder {
    Command build(String[] args);
}