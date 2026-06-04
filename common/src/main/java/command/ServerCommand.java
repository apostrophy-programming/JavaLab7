package command;



public interface ServerCommand extends Command {
    void setOwnerLogin(String ownerLogin);
    void setPasswordHash(String passwordHash);
    String getOwnerLogin();
    String getPasswordHash();
}
