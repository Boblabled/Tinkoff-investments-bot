package TelegramBot.Commands;

import ApiManager.ApiManager;
import TelegramBot.DbManager.DbManager;
import TelegramBot.User.ProcessMode;
import TelegramBot.User.User;

import java.util.concurrent.ExecutionException;

public class CommandManager {

    private final DbManager dbManager;

    public CommandManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public String getMessage(String message, Long chatId) {
        if (message == null) {
            return "Таки не понимаю я вас";
        }

        User user = dbManager.getUser(chatId);
        ProcessMode mode = ProcessMode.START;
        if (user != null) {
            mode = user.getProcessMode();
        }

        switch (mode) {
            case START -> {
                return startExecution(message, chatId);
            }
            case DEFAULT -> {
                return defaultExecution(message, user);
            }
            case COMMANDCANCELORDER -> {
                return "";
            }
        }
        return "Таки не понимаю я вас";
    }

    public String startExecution(String message, Long chatId) {
        CommandStart commandStart = new CommandStart(dbManager, chatId, message);
        return commandStart.execute();
    }

    public String defaultExecution(String message, User user) {

        //TODO проверка токена
        ApiManager apiManager = null;
        try {
            apiManager = new ApiManager(user.getToken(), user.getBrokerAccountId());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        message.append("\n").append(new CommandBalance(null).description());
        message.append("\n").append(new CommandPortfolio(null).description());
        message.append("\n").append(new CommandOrders(null).description());
        message.append("\n").append(new CommandCancelOrder(null).description());

        return message.toString();
    }
}
