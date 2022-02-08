package telegrambot.commands;

import telegrambot.apimanager.ApiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import telegrambot.commands.exceptions.*;

import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;

public class CommandBalance implements Command {

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
        } catch (ExecutionException | InterruptedException e) {
            try {
                return ExceptionManager.check(e);
            } catch (InvalidBrokerAccountIdException | ExceededRequestLimitException | DeadTokenException | LostApiConnectionException ex) {
                logger.warn(ex.logMessage());
                return ex.getMessage();
            }
        }
    }
}
