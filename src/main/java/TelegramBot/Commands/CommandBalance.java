package TelegramBot.Commands;

import ApiManager.ApiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;

public class CommandBalance extends Command {

    private final ApiManager apiManager;

    private final Logger logger = LoggerFactory.getLogger(CommandBalance.class);

    public CommandBalance(ApiManager apiManager) {
        super();
        this.apiManager = apiManager;
    }

    @Override
    public String description() {
        return "/balance - возвращает текущий баланс портфеля в рублях";
    }

    @Override
    public String execute() {
        try {
            return "<i>Ваш текущий балланс по портфелю: </i><code>" + apiManager.getBalance().setScale(2, RoundingMode.UP) + "</code> RUB";
        } catch (ExecutionException e) {
            if (e.getMessage().contains("Unknown account")) {
                logger.error("У пользователя нет счёта");
                return "У вас нету брокерского счёта";
            }
            logger.warn("Превышен лимит запросов");
            return "Превышен лимит запросов";
        } catch (InterruptedException e) {
            logger.error("Соединение с Tinkoff API прервано");
            return "Соединение прервано";
        }
    }
}
