package alpacas;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Shepherd {
    String name;
    Pen pen;
    List<String> logEntries = new ArrayList<>();

    public Shepherd(String name, Pen pen) {
        this.name = name;
        this.pen = pen;
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null");
        }
        if (pen == null) {
            throw new PenNotFoundException("Pen can't be Null");
        }
    }

    public void shear(Alpaca alpaca) {
        if (alpaca == null) {
            throw new IllegalArgumentException("Alpaca can not be null");
        }
        alpaca.shear();
        String logEntry = String.format("%s sheared %s on %s", name, alpaca.name, LocalDate.now());

        logEntries.add(logEntry);
    }

    public List<String> getLog() {
        return List.copyOf(logEntries);
    }

    public int logSize() {
      return getLog().size();
    }
}
