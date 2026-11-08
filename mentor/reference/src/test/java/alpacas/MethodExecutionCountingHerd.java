package alpacas;

import java.util.List;

public class MethodExecutionCountingHerd extends Herd {
    int counter = 0;

    @Override
    List<Alpaca> getAllSameColorAlpacas(FleeceColor fleeceColor) {
        counter++;
        return super.getAllSameColorAlpacas(fleeceColor);
    }

    public int getAllSameColorAlpacasCounter() {
        return counter;
    }
}
