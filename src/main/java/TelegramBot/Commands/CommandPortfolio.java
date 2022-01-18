package TelegramBot.Commands;

import ApiManager.ApiManager;

import java.util.concurrent.ExecutionException;

public class CommandPortfolio extends Command{

    private final ApiManager apiManager;

    public CommandPortfolio(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    @Override
    public String description() {
        return "/portfolio - все активы в потрфеле";
    }

    @Override
    public String execute() {
        StringBuilder message = new StringBuilder();
        try {
            //TODO крсивый вывод
            apiManager.getPortfolioPositions().forEach(portfolioPosition -> {
                message.append(portfolioPosition.toString());
            });
            return message.toString();
        } catch (ExecutionException | InterruptedException e) {
            return "Превышен лимит запросов";
        }
    }
}
