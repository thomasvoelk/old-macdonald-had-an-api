package alpacas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Herd {
    private final List<Alpaca> alpacas;
    public int capacity;

    public Herd() {
        this.capacity = 30;
        this.alpacas = new ArrayList<>();
    }

    public void addAlpaca(Alpaca alpaca) {
        int countedAlpacas = getAlpacaCount();
        boolean sameAlpaca = contains(alpaca);
        if (alpaca == null) {
            throw new IllegalArgumentException("Do not add null and it should always have an Alpaca in it.");
        }
        if (sameAlpaca) {
            throw new IllegalArgumentException("Do not add the same Alpaca twice.");
        }

        if (countedAlpacas < capacity) {
            this.alpacas.add(alpaca);
        }
        if (countedAlpacas == capacity) {
            throw new IllegalStateException("The herd is full you can't add more than 30.");
        }

    }

    public int getAlpacaCount() {

        return alpacas.size();
    }

    public boolean contains(Alpaca alpaca) {
        return alpacas.contains(alpaca);

    }

    public Optional<Alpaca> findByName(String name) {
        for (Alpaca alpaca : alpacas) {
            if (alpaca.getName().equalsIgnoreCase(name)) {
                return Optional.of(alpaca);
            }
        }
        return Optional.empty();
    }

    public boolean removeByName(String name) {
        return alpacas.removeIf(alpaca -> alpaca.getName().equalsIgnoreCase(name));
    }

    public boolean replace(Alpaca replacement) {
        for (int i = 0; i < alpacas.size(); i++) {
            if (alpacas.get(i).getName().equalsIgnoreCase(replacement.getName())) {
                alpacas.set(i, replacement);
                return true;
            }
        }
        return false;
    }

    public List<Alpaca> getAlpacasReadyToShear() {
        List<Alpaca> alpacasAreReadyToShear = new ArrayList<>();
        for (Alpaca alpaca : alpacas) {
            if (alpaca.isReadyToShear()) {
                alpacasAreReadyToShear.add(alpaca);
            }
        }
        return alpacasAreReadyToShear;
    }

    public int totalFleeceLength() {
        int sum = 0;
        for (Alpaca alpaca : alpacas) {
            sum += alpaca.getFleeceLength();
        }
        return sum;
    }

    public double averageWeight() {
        double sum = 0.00;
        if (alpacas.isEmpty())
            return 0.00;

        for (Alpaca alpaca : alpacas) {
            sum += alpaca.getWeight();
        }

        return sum / alpacas.size();
    }

    public Alpaca heaviestAlpaca() {
        Alpaca heaviestAlpaca = null;

        for (Alpaca alpaca : alpacas) {
            if (heaviestAlpaca == null || alpaca.getWeight() > heaviestAlpaca.getWeight()) {
                heaviestAlpaca = alpaca;
            }
        }
        return heaviestAlpaca;
    }

    public List<Alpaca> getAlpacasByColor(FleeceColor fleeceColor) {
        validateFleeceColor(fleeceColor);
        return getAllSameColorAlpacas(fleeceColor);
    }

    List<Alpaca> getAllSameColorAlpacas(FleeceColor fleeceColor) {
        List<Alpaca> allSameColorAlpacas = new ArrayList<>();
        for (Alpaca alpaca : alpacas)
            if (alpaca.getFleeceColor().equals(fleeceColor)) {
                allSameColorAlpacas.add(alpaca);
        }
        return allSameColorAlpacas;
    }

    private static void validateFleeceColor(FleeceColor fleeceColor) {
        if (fleeceColor == null) {
            throw new FleeceColorNotFoundException("Fleece color can not be null");
        }
    }

    public List<Alpaca> getAlpacas() {
        return new ArrayList<>(alpacas);
    }
}
