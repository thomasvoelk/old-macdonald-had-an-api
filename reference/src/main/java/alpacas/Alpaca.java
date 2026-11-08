package alpacas;

public class Alpaca {
    private Long id;
    public String name;
    public double weight;
    public int fleeceLength;
    FleeceColor fleeceColor;

    public Alpaca(String name, double weight, int fleeceLength, FleeceColor fleeceColor) {
        this.name = name;
        this.weight = weight;
        this.fleeceLength = fleeceLength;
        this.fleeceColor = fleeceColor;

        if (fleeceColor == null) {
            throw new FleeceColorNotFoundException("An alpaca has to have a fleece color");
        }

        if (fleeceLength <= -1) {
            throw new IllegalArgumentException("An Alpaca can't have a fleece length under zero");
        } else if (fleeceLength >= 11) {
            throw new IllegalArgumentException("An Alpaca can't have a fleece length over ten");
        }
    }

    public Long getId() {
        return id;
    }

    public void assignId(long id) {
        if (this.id != null) {
            throw new IllegalStateException("This alpaca already has the id " + this.id);
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getFleeceLength(){
        return fleeceLength;
    }

    public double getWeight() {return weight;}

    public FleeceColor getFleeceColor() {return fleeceColor;}


    public void growFleece(int cm){

        int newFleeceLength = fleeceLength + cm;
        if (newFleeceLength > 10) {
            throw new IllegalArgumentException("An Alpaca can't have fleece length over ten cm");
        } else if (newFleeceLength <= -1) {
            throw new IllegalArgumentException("An Alpaca can't have fleece length under zero cm");
        }
        fleeceLength = newFleeceLength;


    }


    public boolean isHealthyWeight() {
        if (weight > 90.00) {
            System.out.println("The Alpaca is to unhealthy it should weight under 90.00 kilos!");
            return false;
        } else if (weight < 55.00) {
            System.out.println("The Alpaca is to unhealthy it should weight over 55.00 kilos!");
            return false;
        } else {
            System.out.println("The Alpaca is healthy. 😊");
            return true;
        }
    }

    public void shear() {
        this.fleeceLength = 0;
    }

    public boolean isReadyToShear() {
        if (fleeceLength >= 10) {
            System.out.println("The Alpaca fleece should not be over 10 cm or 10 cm if so shave the fleece!!");
            return true;
        } else {
            System.out.println("The Alpaca does not need to be shaved. 😊");
            return false;
        }
    }

    public String describe() {
       return String.format("%s weights %s kg and has %s cm of fleece.", name, weight, fleeceLength );
    }

    public String toString() {
        return describe();
    }
}
