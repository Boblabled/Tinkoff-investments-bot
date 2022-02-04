package TelegramBot;

import TelegramBot.Commands.CommandManager;
import TelegramBot.DbManager.DbManager;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BotManager {
    private final TelegramBot bot;
    private final CommandManager commandManager;
    private final DbManager dbManager = new DbManager();

    private final Logger logger = LoggerFactory.getLogger(BotManager.class);

    public BotManager(String token) {
        this.bot = new TelegramBot(token);
        this.commandManager = new CommandManager(dbManager);
    }

    public void run() {
        dbManager.connect();
        //dbManager.createUsersTable();
        logger.info("Бот успешно запустился");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        Message receivedMessage = update.message();
        BaseRequest request = null;
        if (receivedMessage != null) {
            Long chatId = receivedMessage.chat().id();
            String message = commandManager.getMessage(receivedMessage.text(), chatId);
            request = new SendMessage(chatId, message).parseMode(ParseMode.HTML);
            logger.debug("Запрос успешно выполнен");
        }
        bot.execute(request);
    }

    public void stop() {
        dbManager.disconnect();
        bot.shutdown();
    }
}
