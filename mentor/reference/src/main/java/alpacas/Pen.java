package alpacas;


import java.util.List;

public class Pen {
    private final Herd herd;
    private final String name;
    private Long id;


    public Pen(String name, Herd herd) {
        if (herd == null || name == null) {
            throw new IllegalArgumentException("Herd can't be null.");
        }
        this.name = name;
        this.herd = herd;

    }

    public int totalAlpacas() {
        return herd.getAlpacaCount();

    }

    public List<Alpaca> allAlpacasAreReadyToShear() {
        return herd.getAlpacasReadyToShear();
    }

    public Long getId() {
        return id;
    }

    public void assignId(long id) {
        if (this.id != null) {
            throw new IllegalStateException("This pen already has the id " + this.id);
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Herd getHerd() {
        return herd;
    }
}
