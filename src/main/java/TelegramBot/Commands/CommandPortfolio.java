package TelegramBot.Commands;

import ApiManager.ApiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.model.rest.Currency;
import ru.tinkoff.invest.openapi.model.rest.PortfolioPosition;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;

public class CommandPortfolio extends Command{

    private final ApiManager apiManager;

    private final Logger logger = LoggerFactory.getLogger(CommandPortfolio.class);

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
            apiManager.getPortfolioPositions().forEach(portfolioPosition -> message.append(format(portfolioPosition)));
            apiManager.getPortfolioCurrencies().forEach(currencyPosition -> {
                if (currencyPosition.getCurrency().equals(Currency.RUB)) {
                    message.append("<b>RUB | Рубль</b>").append("\n");
                    message.append("<i>Текущая позиция: </i>");
                    message.append("<code>");
                    message.append(currencyPosition.getBalance().setScale(2, RoundingMode.HALF_EVEN));
                    message.append("</code>");
                    message.append(" ").append(currencyPosition.getCurrency());
                }
            });
            return message.toString();
        } catch (ExecutionException e) {
            logger.warn("Превышен лимит запросов");
            return "Превышен лимит запросов";
        } catch (InterruptedException e) {
            logger.error("Соединение с Tinkoff API прервано");
            return "Соединение прервано";
        }
    }

    private String format(PortfolioPosition portfolioPosition) {
        BigDecimal currentPosition = portfolioPosition.getAveragePositionPrice().getValue()
                .multiply(portfolioPosition.getBalance())
                .add(portfolioPosition.getExpectedYield().getValue());
        StringBuilder message = new StringBuilder();
        message.append("<b>");
        message.append(portfolioPosition.getTicker());
        message.append(" | ");
        message.append(portfolioPosition.getName());
        message.append("</b>");
        message.append("\n");

        message.append("<i>Текущая позиция: </i>");
        message.append("<code>");
        message.append(currentPosition.setScale(2, RoundingMode.HALF_EVEN));
        message.append("</code>");
        message.append(" ").append(portfolioPosition.getAveragePositionPrice().getCurrency());
        message.append("\n");

        message.append("<i>Колличество: </i>");
        message.append("<code>");
        message.append(portfolioPosition.getBalance());
        message.append("</code>");
        message.append(" шт.");
        message.append("<code> * ");
        message.append(currentPosition.divide(portfolioPosition.getBalance(), MathContext.DECIMAL32)
                .setScale(2, RoundingMode.HALF_EVEN));
        message.append("</code>");
        message.append(" ").append(portfolioPosition.getExpectedYield().getCurrency());
        message.append("\n");

        if (portfolioPosition.getExpectedYield().getValue().compareTo(new BigDecimal(0)) < 0) {
            message.append("<i>Убыток: </i>");
        } else {
            message.append("<i>Прибыль: </i>");
        }
        message.append("<code>");
        message.append(portfolioPosition.getExpectedYield().getValue());
        message.append("</code>");
        message.append(" ").append(portfolioPosition.getExpectedYield().getCurrency());
        message.append(" (");
        message.append("<code>");
        if (currentPosition.compareTo(portfolioPosition.getAveragePositionPrice().getValue()) < 0) {
            message.append(currentPosition
                    .divide(portfolioPosition.getAveragePositionPrice().getValue(), MathContext.DECIMAL32)
                    .subtract(new BigDecimal(1))
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_EVEN));
        } else {
            //TODO где-то ошибка в подсчётах
            message.append("+").append(portfolioPosition.getAveragePositionPrice().getValue()
                    .multiply(portfolioPosition.getBalance())
                    .divide(currentPosition, MathContext.DECIMAL32)
                    .subtract(new BigDecimal(1))
                    .multiply(new BigDecimal(100))
                    .abs()
                    .setScale(2,RoundingMode.HALF_EVEN));
        }
        message.append("</code>");
        message.append("%)\n\n");
        return message.toString();
    }
}
