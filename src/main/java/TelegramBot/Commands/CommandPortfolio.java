package TelegramBot.Commands;

import ApiManager.ApiManager;
import ru.tinkoff.invest.openapi.model.rest.Currency;
import ru.tinkoff.invest.openapi.model.rest.PortfolioPosition;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
            message.append("Активы в портфеле:\n\n");
            apiManager.getPortfolioPositions().forEach(portfolioPosition -> {
                message.append(format(portfolioPosition));
            });
            apiManager.getPortfolioCurrencies().forEach(currencyPosition -> {
                if (currencyPosition.getCurrency().equals(Currency.RUB)) {
                    message.append("RUB | Рубль").append("\n");
                    message.append(currencyPosition.getBalance().setScale(2, RoundingMode.UP));
                    message.append(" ").append(currencyPosition.getCurrency());
                }
            });
            return message.toString();
        } catch (ExecutionException | InterruptedException e) {
            return "Превышен лимит запросов";
        }
    }

    private String format(PortfolioPosition portfolioPosition) {
        BigDecimal currentPrice = portfolioPosition.getAveragePositionPrice().getValue()
                .multiply(portfolioPosition.getBalance())
                .add(portfolioPosition.getExpectedYield().getValue());
        StringBuilder message = new StringBuilder();
        message.append(portfolioPosition.getTicker());
        message.append(" | ");
        message.append(portfolioPosition.getName());
        message.append("\n");
        message.append("Текущая цена: ").append(currentPrice.setScale(2, RoundingMode.UP));
        message.append(" ").append(portfolioPosition.getAveragePositionPrice().getCurrency());
        message.append("\n");
        if (portfolioPosition.getExpectedYield().getValue().compareTo(new BigDecimal(0)) < 0) {
            message.append("Убыток: ");
        } else {
            message.append("Прибыль: ");
        }
        message.append(portfolioPosition.getExpectedYield().getValue());
        message.append(" ").append(portfolioPosition.getExpectedYield().getCurrency());
        message.append(" (");
        if (currentPrice.compareTo(portfolioPosition.getAveragePositionPrice().getValue()) < 0) {
            message.append(currentPrice.divide(portfolioPosition.getAveragePositionPrice().getValue(), MathContext.DECIMAL32)
                    .subtract(new BigDecimal(1)).multiply(new BigDecimal(100)).setScale(2, RoundingMode.UP));
        } else {
            //TODO где-то ошибка
            message.append("+").append(portfolioPosition.getAveragePositionPrice().getValue()
                    .multiply(portfolioPosition.getBalance()).divide(currentPrice, MathContext.DECIMAL32)
                    .subtract(new BigDecimal(1)).multiply(new BigDecimal(100))
                    .abs().setScale(2,RoundingMode.UP));
        }
        message.append("%)\n\n");
        return message.toString();
    }
}
