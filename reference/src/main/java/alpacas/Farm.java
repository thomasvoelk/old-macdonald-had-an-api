package alpacas;

import java.util.ArrayList;
import java.util.List;

public class Farm {
    private final List<Pen> pens;
private final String name;

    public Farm(String name) {
       if (name == null) {
           throw new IllegalArgumentException("Farm has to have a name");
       }
       this.name = name;
       pens = new ArrayList<>();
    }

    public int getPensCount() {
        return pens.size();
    }

    public void addPen(Pen pen) {
       for (Pen existing : pens) {
           if (existing.getName().equalsIgnoreCase(pen.getName())) {
               throw new IllegalStateException("There is already a pen named " + pen.getName());
           }
       }
       pens.add(pen);
    }

    public int totalAlpacas() {
        int totalAlpacas = 0;

        for (Pen pen : pens) {
            totalAlpacas = totalAlpacas + pen.totalAlpacas();
        }

        return totalAlpacas;
    }

    public Pen findPen(String name) {
        if (name == null) {
            throw new PenNotFoundException("Pen can not be null");
        }
        for (Pen pen: pens) {
          if (pen.getName().equalsIgnoreCase(name)) {
              return pen;
          }
      }
        throw new PenNotFoundException("The Pen you are looking for " + name + " does not exist");
    }

    public List<Alpaca> allAlpacasReadyToShear() {
        List<Alpaca> readyToShear = new ArrayList<>();

        for (Pen pen: pens) {
            readyToShear.addAll(pen.allAlpacasAreReadyToShear());
        }
        return readyToShear;
    }


    public String getName() {
        return name;
    }

    public List<Pen> getPens() {
        return new ArrayList<>(pens);
    }
}
