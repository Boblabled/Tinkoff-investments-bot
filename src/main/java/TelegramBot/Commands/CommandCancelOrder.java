package TelegramBot.Commands;

import ApiManager.ApiManager;

public class CommandCancelOrder extends Command{

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
        return null;
    }
}
