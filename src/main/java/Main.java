import telegrambot.BotManager;

public class Main {
    public static void main(String[] args) {
        var botManager = new BotManager(args[1]);
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
