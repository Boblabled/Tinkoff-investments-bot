import ApiManager.ApiManager;
import Portfolio.Portfolio;
import TelegramBot.BotManager;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ApiManager apiManager = new ApiManager(args[0], false);
        //final Portfolio portfolio = new Portfolio(apiManager, apiManager.getBrokerAccountId(), 10, 3);
        final BotManager botManager = new BotManager(args[1], apiManager);
        botManager.run();

        /*
        //максмум 300 подписок
        final var stopNotifier = new CompletableFuture<Void>();
        final Flowable<StreamingEvent> rxStreaming = Flowable.fromPublisher(api.getStreamingContext());
        final Disposable rxSubscription = rxStreaming
                .doOnError(stopNotifier::completeExceptionally)
                .doOnComplete(() -> stopNotifier.complete(null))
                .forEach(event -> System.out.println("Пришло новое событие из Streaming API\n" + event));
        String figi = api.getMarketContext().searchMarketInstrumentsByTicker("GAZP").get().getInstruments().get(0).getFigi();
        api.getStreamingContext().sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.DAY));
        */
    }

}
