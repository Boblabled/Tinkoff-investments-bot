package TelegramBot.Commands;

import ApiManager.ApiManager;

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
            return "Ваш текущий балланс по портфелю: " + apiManager.getBalance() + " RUB";
        } catch (ExecutionException | InterruptedException e) {
            return "Превышен лимит запросов";
        }
    }
}
