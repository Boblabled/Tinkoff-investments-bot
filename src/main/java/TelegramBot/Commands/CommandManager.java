package TelegramBot.Commands;

import ApiManager.ApiManager;

public class CommandManager {

    private final ApiManager apiManager;

    public CommandManager(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    public String getMessange(String message) {
        switch (message) {
            case "/help" -> {
                return commandsDescription();
            }
            case "/balance" -> {
                CommandBalance commandBalance = new CommandBalance(apiManager);
                return commandBalance.execute();
            }
            case "/portfolio" -> {
                CommandPortfolio commandPortfolio = new CommandPortfolio(apiManager);
                return commandPortfolio.execute();
            }
            default -> {
                return "Таки не понимаю я вас";
            }
        }
    }

    private String commandsDescription() {
        StringBuilder message = new StringBuilder("Список доступных команд: ");

        message.append("\n").append(new CommandBalance(apiManager).description());
        message.append("\n").append(new CommandPortfolio(apiManager).description());

        return message.toString();
    }
}
