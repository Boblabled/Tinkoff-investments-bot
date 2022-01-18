package TelegramBot;

import ApiManager.ApiManager;
import TelegramBot.Commands.CommandManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

public class BotManager {
    private final TelegramBot bot;
    private final ApiManager apiManager;

    public BotManager(String token, ApiManager apiManager) {
        this.bot = new TelegramBot(token);
        this.apiManager = apiManager;
        start();
    }

    public void start() {
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
            CommandManager commandManager = new CommandManager(apiManager);
            request = new SendMessage(chatId, commandManager.getMessange(message.text()));
        }
        bot.execute(request);
    }
}
