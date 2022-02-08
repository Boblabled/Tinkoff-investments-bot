package telegrambot.commands;

import telegrambot.apimanager.ApiManager;

public class CommandCancelOrder implements Command{

    private ApiManager apiManager;

    public CommandCancelOrder(ApiManager apiManager){
        this.apiManager = apiManager;
    }

    @Override
    public String description() {
        return "/cancel_order - отменить заявку по её id";
    }

    @Override
    public String execute() {
        //TODO сперва надо завести базу данных
        return "null";
    }
}
