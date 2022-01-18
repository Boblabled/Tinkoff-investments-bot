package Portfolio;

import ApiManager.ApiManager;
import ru.tinkoff.invest.openapi.model.rest.Currency;
import ru.tinkoff.invest.openapi.model.rest.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Portfolio {
    private final ApiManager apiManager;
    private final String brokerAccountId;
    private final BigDecimal meanShare; //средняя доля одной позиции
    private final BigDecimal priceStep; //отклонение позиции от среднего значения
    private HashSet<MarketInstrument> validStocks;
    private HashMap<Currency,String> currency;

    public Portfolio(ApiManager apiManager, String brokerAccountId, double meanShare, double priceStep) throws ExecutionException, InterruptedException {
        this.apiManager = apiManager;
        this.brokerAccountId = brokerAccountId;
        this.meanShare = new BigDecimal(meanShare);
        this.priceStep = new BigDecimal(priceStep);
        this.currency = apiManager.getCurrencyPrice();
    }

    public void setValidStocks() throws ExecutionException, InterruptedException, IOException {
        //через с подписку на инфо об компании и подписку на свечи
        System.out.println(apiManager.getBalance());
    }

}
