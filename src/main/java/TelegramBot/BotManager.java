package TelegramBot;

import ApiManager.ApiManager;
import TelegramBot.Commands.CommandManager;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotManager {
    private final TelegramBot bot;
    private final CommandManager commandManager;
    private final Logger logger = LoggerFactory.getLogger(BotManager.class);

    public BotManager(String token, ApiManager apiManager) {
        this.bot = new TelegramBot(token);
        this.commandManager = new CommandManager(apiManager);
    }

    public void run() {
        logger.info("Бот успешно запустился");
        logger.debug("TEST");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        Message message = update.message();
        BaseRequest request = null;
        if (message != null) {
            long chatId = message.chat().id();
            request = new SendMessage(chatId, commandManager.getMessange(message.text()));
            logger.debug("Запрос успешо выполнен");
        }
        bot.execute(request);
    }
}
