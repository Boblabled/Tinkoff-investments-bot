package algorithms;

import java.util.function.Function;

public abstract class Algorithm {
    private String figi;
    private Function<Integer, Boolean> func;

    public Algorithm(String figi, Function<Integer, Boolean> func) {
        this.figi = figi;
        this.func = func;
    }
}
