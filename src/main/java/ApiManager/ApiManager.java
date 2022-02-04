package ApiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.*;
import ru.tinkoff.invest.openapi.model.rest.Currency;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static ru.tinkoff.invest.openapi.model.rest.CandleResolution.DAY;

public class ApiManager {
    private final OpenApi api;
    private String brokerAccountId = null;

    private static final Logger logger = LoggerFactory.getLogger(ApiManager.class);

    public ApiManager(String token, String brokerAccountId) throws ExecutionException, InterruptedException {
        this.api =  new OkHttpOpenApi(token, false, Executors.newCachedThreadPool());
        getMarketStocks();
        this.brokerAccountId = brokerAccountId;
        logger.debug("Соединение с Tinkoff API установленно");
    }

    public static boolean checkToken(String token) {
        OpenApi api = new OkHttpOpenApi(token, false, Executors.newCachedThreadPool());
        try {
            api.getMarketContext().getMarketStocks().get();
        } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
            logger.debug(e.getMessage());
            return false;
        } finally {
            try {
                api.close();
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
        return true;
    }

    public static boolean checkBrokerAccount(String token, String brokerAccountId) {
        OpenApi api = new OkHttpOpenApi(token, false, Executors.newCachedThreadPool());
        List<UserAccount> accounts = null;
        try {
            accounts = api.getUserContext().getAccounts().get().getAccounts().stream()
                    .filter(account -> account.getBrokerAccountId().equals(brokerAccountId)).collect(Collectors.toList());
            return !accounts.isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            logger.debug(e.getMessage());
            return false;
        }
    }

    public static List<UserAccount> getBrokerAccounts(String token) throws InterruptedException, ExecutionException, IllegalArgumentException {
        OpenApi api = new OkHttpOpenApi(token, false, Executors.newCachedThreadPool());
        return api.getUserContext().getAccounts().get().getAccounts();
    }

    public List<PortfolioPosition> getPortfolioPositions() throws ExecutionException, InterruptedException {
        return api.getPortfolioContext().getPortfolio(brokerAccountId).get().getPositions();
    }

    public List<CurrencyPosition> getPortfolioCurrencies() throws ExecutionException, InterruptedException {
        return api.getPortfolioContext().getPortfolioCurrencies(brokerAccountId).get().getCurrencies();
    }

    public List<MarketInstrument> getMarketStocks() throws ExecutionException, InterruptedException {
        return api.getMarketContext().getMarketStocks().get().getInstruments();
    }

    public List<MarketInstrument> getMarketCurrencies() throws ExecutionException, InterruptedException {
        return api.getMarketContext().getMarketCurrencies().get().getInstruments();
    }

    public List<MarketInstrument> getMarketEtfs() throws ExecutionException, InterruptedException {
        return api.getMarketContext().getMarketEtfs().get().getInstruments();
    }

    public List<MarketInstrument> getMarketBonds() throws ExecutionException, InterruptedException {
        return api.getMarketContext().getMarketBonds().get().getInstruments();
    }

    public List<Candle> getCandles(String figi, OffsetDateTime openTime, OffsetDateTime closeTime, CandleResolution candleResolution) throws ExecutionException, InterruptedException {
        return Objects.requireNonNull(api.getMarketContext().getMarketCandles(figi, openTime, closeTime, candleResolution).get().orElse(null)).getCandles();
    }

    public BigDecimal getCurrentPrice(String figi) throws ExecutionException, InterruptedException, IndexOutOfBoundsException {
        List<Candle> candle = getCandles(figi, OffsetDateTime.now().minusWeeks(1), OffsetDateTime.now(), DAY);
        return candle.get(candle.size()-1).getC();
    }

    public HashMap<Currency,String> getCurrencyPrice() throws ExecutionException, InterruptedException {
        HashMap<Currency,String> currency = new HashMap<>();
        List<MarketInstrument> currencyList = getMarketCurrencies();
        for (Currency currencyKey : Currency.values()) {
            if (currencyKey.equals(Currency.RUB)) {
                currency.put(currencyKey, null);
            } else {
                try {
                    String value = currencyList.stream().filter(c -> (c.getTicker().contains(currencyKey.getValue())))
                            .collect(Collectors.toList()).get(0).getFigi();
                    currency.put(currencyKey, value);
                } catch (IndexOutOfBoundsException e) {
                    logger.debug("{} валюта не найдена", currencyKey.getValue());
                }
            }
        }

        return currency;
    }

    public String getFigiByTicker(String ticker) throws ExecutionException, InterruptedException {
        return api.getMarketContext().searchMarketInstrumentsByTicker(ticker).get().getInstruments().get(0).getFigi();
    }

    public List<Order> getActiveOrders() throws ExecutionException, InterruptedException {
        return api.getOrdersContext().getOrders(brokerAccountId).get();
    }

    public BigDecimal getBalance() throws ExecutionException, InterruptedException {
        //делает всего 2 account request и 1 market request
        BigDecimal balance = new BigDecimal(0);
        HashMap<Currency,String> currency = getCurrencyPrice();
        List<PortfolioPosition> positions = getPortfolioPositions();

        for (PortfolioPosition pos : positions) {
            ru.tinkoff.invest.openapi.model.rest.Currency stockCurrency = ru.tinkoff.invest.openapi.model.rest.Currency.RUB;

            stockCurrency = Objects.requireNonNull(pos.getAveragePositionPrice()).getCurrency();

            if (stockCurrency.equals(ru.tinkoff.invest.openapi.model.rest.Currency.RUB)) {

                // среднняя в портфеле * колличество штук + текущая прибыль/убыток
                balance = balance.add(pos.getBalance().multiply(pos.getAveragePositionPrice()
                        .getValue())).add(pos.getExpectedYield().getValue());
            }

            else {
                try {
                    // (среднняя в портфеле * колличество штук +  текущая прибыль/убыток) * курс валюты
                    balance = balance.add((pos.getAveragePositionPrice().getValue()
                            .multiply(pos.getBalance())).add(pos.getExpectedYield()
                            .getValue())).multiply(getCurrentPrice(currency.get(stockCurrency)));
                } catch (NullPointerException e) {
                    logger.error("Невозможно получить курс {}", stockCurrency);
                }
            }
        }

        List<CurrencyPosition> rubBalance = getPortfolioCurrencies()
                .stream().filter( p ->(p.getCurrency().equals(Currency.RUB))).collect(Collectors.toList());

        // свободный кэш в рублях (если есть)
        if (!rubBalance.isEmpty()) {
            balance = balance.add(rubBalance.get(0).getBalance());
        }
        System.out.println(balance.floatValue());
        return balance;
    }

    public void cancelOrder(String id) {
        api.getOrdersContext().cancelOrder(id, brokerAccountId);
    }

    public void setBalance(SandboxCurrency currency, float value) {
        if (api.isSandboxMode()) {
            SandboxSetCurrencyBalanceRequest request = new SandboxSetCurrencyBalanceRequest();
            request.setCurrency(currency);
            request.setBalance(new BigDecimal(value));
            api.getSandboxContext().setCurrencyBalance(request, brokerAccountId);
            logger.debug("Балланс установлен");
        } else {
            logger.debug("Это не sandboxMode");
        }
    }

    public void setMarketOrder(String figi, int lots, OperationType operation) {
        MarketOrderRequest request = new MarketOrderRequest();
        request.setLots(lots);
        request.setOperation(operation);
        api.getOrdersContext().placeMarketOrder(figi, request, brokerAccountId);
    }
}
