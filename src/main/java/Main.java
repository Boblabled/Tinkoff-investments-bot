import ApiManager.ApiManager;
import TelegramBot.BotManager;
import Portfolio.Portfolio;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        ApiManager apiManager = new ApiManager(args[0], false);
        Portfolio portfolio = new Portfolio(apiManager, apiManager.getBrokerAccountId(), 10, 3);
        BotManager botManager = new BotManager(args[1], apiManager);

        //api.getMarketContext().getMarketStocks().get().getInstruments().forEach(m -> System.out.println(m.toString()));

        /*
        final var stopNotifier = new CompletableFuture<Void>();
        final Flowable<StreamingEvent> rxStreaming = Flowable.fromPublisher(apiManager.getApi().getStreamingContext());
        final Disposable rxSubscription = rxStreaming
                .doOnError(stopNotifier::completeExceptionally)
                .doOnComplete(() -> stopNotifier.complete(null))
                .forEach(event -> System.out.println("Пришло новое событие из Streaming API\n" + event));
        String figi = apiManager.getApi().getMarketContext().searchMarketInstrumentsByTicker("IDCC").get().getInstruments().get(0).getFigi();
        apiManager.getApi().getStreamingContext().sendRequest(StreamingRequest.subscribeInstrumentInfo(figi));
        */

        /*
        api.getOrdersContext().placeMarketOrder("figi",
                new MarketOrder(1, Operation.Sell),
                api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get();
         */

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

    private static org.slf4j.Logger initLogger() throws IOException {
        final var logManager = LogManager.getLogManager();
        final var classLoader = Main.class.getClassLoader();

        try (final InputStream input = classLoader.getResourceAsStream("logging.properties")) {

            if (input == null) {
                throw new FileNotFoundException();
            }

            Files.createDirectories(Paths.get("./logs"));
            logManager.readConfiguration(input);
        }

        return org.slf4j.LoggerFactory.getLogger(Main.class);
    }

}
