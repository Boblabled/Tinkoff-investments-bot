package telegrambot.commands;

import telegrambot.apimanager.ApiManager;
import telegrambot.commands.exceptions.*;
import telegrambot.dbmanager.DbManager;
import telegrambot.user.ProcessMode;
import telegrambot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class CommandManager {

    private final DbManager dbManager;
    public final Logger logger = LoggerFactory.getLogger(CommandManager.class);

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
            case COMMAND_CANCEL_ORDER -> {
                //TODO
                return "";
            }
            case TOKEN_UPDATE -> {
                //TODO
                return " ";
            }
        }
        return "Таки не понимаю я вас";
    }



    public String startExecution(String message, Long chatId) {
        CommandStart commandStart = new CommandStart(dbManager, chatId, message);
        return commandStart.execute();
    }

    public String defaultExecution(String message, User user) {
        ApiManager apiManager;
        try {
            apiManager = new ApiManager(user.getToken(), user.getBrokerAccountId());
        } catch (InterruptedException | ExecutionException e) {
            try {
                return ExceptionManager.check(e);
            } catch (InvalidBrokerAccountIdException | ExceededRequestLimitException | DeadTokenException | LostApiConnectionException ex) {
                logger.warn(ex.logMessage());
                if (ex instanceof DeadTokenException) {
                    dbManager.updateUserField(user.chatId(), "processMode", String.valueOf(ProcessMode.TOKEN_UPDATE));
                }
                return ex.getMessage();
            }
        }

        switch (message) {
            case "/help" -> {
                return commandsDescription();
            }
            case "/balance" -> {
                var commandBalance = new CommandBalance(apiManager);
                return commandBalance.execute();
            }
            case "/portfolio" -> {
                var commandPortfolio = new CommandPortfolio(apiManager);
                return commandPortfolio.execute();
            }
            case "/orders" -> {
                var commandOrders = new CommandOrders(apiManager);
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
