package TelegramBot.Commands;

import ApiManager.ApiManager;

public class CommandManager {

    private final ApiManager apiManager;

    public CommandManager(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    public String getMessage(String message) {
        if (message == null) {
            return "Таки не понимаю я вас";
        }
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
            case "/orders" -> {
                CommandOrders commandOrders = new CommandOrders(apiManager);
                return commandOrders.execute();
            }
            default -> {
                return "Таки не понимаю я вас";
            }
        }
    }

    private String commandsDescription() {
        StringBuilder message = new StringBuilder();
        message.append("<b>Шалом!</b> Я самый кошерный бот для управления портфелем Тинькофф инвестиций. ");
        message.append("Если вы доверите мне свой капитал, таки я стану вышим персональным менеджером.\n\n");
        message.append("<i><b>Вот список команд для управления мной:</b></i>\n\n");
        message.append("<b>Управление портфелем </b>");
        message.append("\n").append(new CommandBalance(apiManager).description());
        message.append("\n").append(new CommandPortfolio(apiManager).description());
        message.append("\n").append(new CommandOrders(apiManager).description());
        message.append("\n").append(new CommandCancelOrder(apiManager).description());

        return message.toString();
    }
}
