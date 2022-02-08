package telegrambot.commands.exceptions;

public class DeadTokenException extends Exception implements LogMessager{
    public DeadTokenException() {
        super("Срок жизни вашего токена подошёл к концу, необходимо его обновить\n\n<i>*Отправьте токен следующим сообщением</i>");
    }
    @Override
    public String logMessage() {
        return "У пользователя истёк срок жизни токена";
    }
}
