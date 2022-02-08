package telegrambot.commands.exceptions;

import java.util.concurrent.ExecutionException;

public class ExceptionManager {
    public static String check(Exception e) throws InvalidBrokerAccountIdException, ExceededRequestLimitException, DeadTokenException, LostApiConnectionException {
        if (e instanceof ExecutionException) {
            if (e.getMessage().contains("Unknown account")) {
                throw new InvalidBrokerAccountIdException();
            }
            throw new ExceededRequestLimitException();
        }
        if (e instanceof InterruptedException) {
            if (e.getMessage().contains("Попытка использовать неверный токен")) {
                throw new DeadTokenException();
            }
            throw new LostApiConnectionException();
        }
        return "Неизвестная ошибка";
    }
}
