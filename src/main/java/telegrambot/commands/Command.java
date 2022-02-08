package telegrambot.commands;

public interface Command {
    public String description();
    public String execute();
}
