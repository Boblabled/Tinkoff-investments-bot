package telegrambot.user;

public record User(Long chatId, String token, String brokerAccountId,
                   ProcessMode processMode) {

    public Long getChatId() {
        return chatId;
    }

    public String getToken() {
        return token;
    }

    public String getBrokerAccountId() {
        return brokerAccountId;
    }

    public ProcessMode getProcessMode() {
        return processMode;
    }
}
