package telegrambot.commands.exceptions;

public class LostApiConnectionException extends Exception implements LogMessager{
    public LostApiConnectionException(){
        super("Соединение с Tinkoff API прервано");
    }

    @Override
    public String logMessage() {
        return "У пользователя прервалось соединение с tinkoff api";
    }
}
