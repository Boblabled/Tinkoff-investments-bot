package telegrambot.commands.exceptions;

public class ExceededRequestLimitException extends Exception implements LogMessager{
    public ExceededRequestLimitException() {
        super("Превышен лимит запросов, повторите попытку позже");
    }
    @Override
    public String logMessage() {
        return "Пользователь превысил лимит запросов";
    }
}
