package telegrambot.commands.exceptions;

public class InvalidBrokerAccountIdException extends Exception implements LogMessager{
    public InvalidBrokerAccountIdException() {
        super("У вас нету брокерского счёта");
    }
    @Override
    public String logMessage() {
        return "У пользователя нет счёта";
    }
}
