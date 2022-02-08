package strategy;

import portfolio.Portfolio;

public abstract class Strategy {
    private String token;
    private Portfolio portfolio;

    public Strategy(String token, Portfolio portfolio) {
        this.token = token;
        this.portfolio = portfolio;
    }


}
