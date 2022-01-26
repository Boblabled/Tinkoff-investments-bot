package TelegramBot.Commands;

import ApiManager.ApiManager;

import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;

public class CommandBalance extends Command {

    private final ApiManager apiManager;

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
            return "Ваш текущий балланс по портфелю: " + apiManager.getBalance().setScale(2, RoundingMode.UP) + " RUB";
        } catch (ExecutionException | InterruptedException e) {
            return "Превышен лимит запросов";
        }
    }
}
