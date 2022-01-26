package ApiManager;

import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.*;
import ru.tinkoff.invest.openapi.model.rest.Currency;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static ru.tinkoff.invest.openapi.model.rest.CandleResolution.DAY;

public class ApiManager {
    private final OpenApi api;
    private final String brokerAccountId;

    public ApiManager(String token, Boolean sandboxMode) throws ExecutionException, InterruptedException {
        this.api =  new OkHttpOpenApi(token, sandboxMode, Executors.newCachedThreadPool());
        this.brokerAccountId = api.getUserContext().getAccounts().get().getAccounts().get(0).getBrokerAccountId();
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
        return api.getMarketContext().getMarketCandles(figi, openTime, closeTime, candleResolution).get().get().getCandles();
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
            }
            else {
                try {
                    String value = currencyList.stream().filter(c -> (c.getTicker().contains(currencyKey.getValue())))
                            .collect(Collectors.toList()).get(0).getFigi();
                    currency.put(currencyKey, value);
                } catch (Exception e) {
                    //TODO добавить exception
                }
            }
        }

        return currency;
    }

    public BigDecimal getBalance() throws ExecutionException, InterruptedException {
        BigDecimal ballance = new BigDecimal(0);
        HashMap<Currency,String> currency = getCurrencyPrice();
        List<PortfolioPosition> positions = getPortfolioPositions();

        for (PortfolioPosition pos : positions) {
            ru.tinkoff.invest.openapi.model.rest.Currency stockCurrency = pos.getAveragePositionPrice().getCurrency();

            if (stockCurrency.equals(ru.tinkoff.invest.openapi.model.rest.Currency.RUB)) {

                // среднняя в портфеле * колличество штук +  текущая прибыль/убыток
                ballance = ballance.add(pos.getBalance().multiply(pos.getAveragePositionPrice()
                        .getValue())).add(pos.getExpectedYield().getValue());
            }

            else {

                // (среднняя в портфеле * колличество штук +  текущая прибыль/убыток) * курс валюты
                ballance = ballance.add((pos.getAveragePositionPrice().getValue()
                        .multiply(pos.getBalance())).add(pos.getExpectedYield()
                        .getValue())).multiply(getCurrentPrice(currency.get(stockCurrency)));
            }
        }

        List<CurrencyPosition> portfolioRub = getPortfolioCurrencies()
                .stream().filter( p ->(p.getCurrency().equals(Currency.RUB))).collect(Collectors.toList());

        // свободный кэш в рублях (если есть)
        if (!portfolioRub.isEmpty()) {
            ballance = ballance.add(portfolioRub.get(0).getBalance());
        }

        return ballance;
    }

    public OpenApi getApi() {
        return api;
    }

    public String getBrokerAccountId() {
        return brokerAccountId;
    }
}
