package telegrambot.commands;

import telegrambot.apimanager.ApiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.OperationType;
import ru.tinkoff.invest.openapi.model.rest.Order;
import ru.tinkoff.invest.openapi.model.rest.OrderType;
import telegrambot.commands.exceptions.*;

import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CommandOrders implements Command{

    private final ApiManager apiManager;
    private final Logger logger = LoggerFactory.getLogger(CommandOrders.class);

    public CommandOrders(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    @Override
    public String description() {
        return "/orders - список активных заявок";
    }

    @Override
    public String execute() {
        StringBuilder message = new StringBuilder();
        try {
            List<MarketInstrument> instruments = apiManager.getMarketStocks();
            instruments.addAll(apiManager.getMarketBonds());
            instruments.addAll(apiManager.getMarketEtfs());
            instruments.addAll(apiManager.getMarketCurrencies());

            apiManager.getActiveOrders().forEach(order -> {
               message.append(format(order, instruments));
           });
        } catch (ExecutionException | InterruptedException e) {
            try {
                return ExceptionManager.check(e);
            } catch (InvalidBrokerAccountIdException | ExceededRequestLimitException | DeadTokenException | LostApiConnectionException ex) {
                logger.warn(ex.logMessage());
                return ex.getMessage();
            }
        }

        if (message.toString().equals("")) {
            logger.debug("Нету активных заявок");
            message.append("Нету активных заявок");
        }
        return message.toString();
    }

    public String format(Order order, List<MarketInstrument> instruments) {
        StringBuilder message = new StringBuilder();

        message.append("<b>");
        MarketInstrument instrument = null;
        try {
            instrument = instruments.stream().filter(i -> i.getFigi().equals(order.getFigi())).collect(Collectors.toList()).get(0);
            message.append(instrument.getTicker());
            message.append(" | ");
            message.append(instrument.getName());
        } catch (IndexOutOfBoundsException e) {
            logger.warn("Нету актива с таким figi");
            message.append("Неизвестный актив");
        }
        message.append("</b>");
        message.append("\n");

        message.append("<i>Id заявки: </i>");
        message.append("<code>");
        message.append(order.getOrderId());
        message.append("</code>");
        message.append("\n");

        message.append("<i>Тип заявки: </i>");
        if (order.getType().equals(OrderType.LIMIT)) {
            message.append("лимитная");
        } else {
            message.append("рыночная");
        }
        if (order.getOperation().equals(OperationType.BUY)) {
            message.append(" заявка на покупку по ");
        } else {
            message.append(" заявка на продажу по ");
        }
        message.append("<code>");
        message.append(order.getPrice().setScale(2, RoundingMode.HALF_EVEN));
        message.append("</code>");
        message.append(" ");
        if (instrument != null) {
            message.append(instrument.getCurrency());
        }
        message.append("\n");

        message.append("<i>Колличество: </i>");
        message.append("<code>");
        message.append(order.getRequestedLots());
        message.append("</code> шт.");
        message.append("\n");

        message.append("<i>Исполненно: </i>");
        message.append("<code>");
        message.append(order.getExecutedLots());
        message.append("</code> шт.");
        message.append("\n\n");

        return message.toString();
    }
}
