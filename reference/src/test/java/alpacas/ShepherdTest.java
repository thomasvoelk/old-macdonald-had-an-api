package alpacas;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static alpacas.FleeceColor.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShepherdTest {

    @Test
    public void nameCanNotBeNull() {
        Pen pen = new Pen("Ring", new Herd());
        assertThrows(IllegalArgumentException.class, () -> new Shepherd(null, pen));
    }

    @Test
    public void penCanNotBeNull() {
        assertThrows(PenNotFoundException.class, () -> new Shepherd("Tommy", null));
    }

    @Test
    public void shepherdCanNotBeNull() {
        Herd herd = new Herd();
        Pen pen = new Pen("Hell in a Cell", herd);
        Shepherd shepherd = new Shepherd("Eric Bischoff", pen);

        assertThrows(IllegalArgumentException.class, () -> shepherd.shear(null));
    }

    @Test
    public void shepherdHasOneAlpacaToShearFromOneAlpaca() {
        Alpaca alpaca = new Alpaca("The Rock", 110.00, 9, GREY);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca);
        Pen pen = new Pen("Ring", herd);
        Shepherd shepherd = new Shepherd("Miss Elizabeth", pen);

        shepherd.shear(alpaca);

        assertEquals(0, alpaca.fleeceLength);
    }

    @Test
    public void shepherdHasTwoAlpacasToShearFromTwoAlpacas() {
        Alpaca alpaca1 = new Alpaca("Roman Reigns", 120.00, 10, BLACK);
        Alpaca alpaca2 = new Alpaca("Cody Rhodes", 102.00, 10, BROWN);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        Pen pen = new Pen("Hell in a Cell", herd);
        Shepherd shepherd = new Shepherd("Paul Heyman", pen);

        shepherd.shear(alpaca1);
        shepherd.shear(alpaca2);

        assertEquals(0, alpaca1.fleeceLength);
        assertEquals(0, alpaca2.fleeceLength);
    }

    @Test
    public void shepherdHasOneAlpacaToShearThatAlsoHasALogEntry() {
        Alpaca alpaca = new Alpaca("Ronda Rousey", 61.00, 10, BROWN);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca);
        Pen pen = new Pen("Ring", herd);
        Shepherd shepherd = new Shepherd("Roddy Piper", pen);

        shepherd.shear(alpaca);
        String logEntry = shepherd.getLog().getFirst();

        assertEquals("Roddy Piper sheared Ronda Rousey on " + LocalDate.now(), logEntry);
    }

    @Test
    public void shepherdHasTwoAlpacasToShearThatAlsoHasALogEntry() {
        Alpaca alpaca1 = new Alpaca("Drew Mclntyre", 125.00, 5, BLACK);
        Alpaca alpaca2 = new Alpaca("Lita", 61.00, 10, BROWN);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        Pen pen = new Pen("Ladder Match", herd);
        Shepherd shepherd = new Shepherd("William Regal", pen);

        shepherd.shear(alpaca1);
        shepherd.shear(alpaca2);

        List<String> expectedLogs = List.of(
                "William Regal sheared Drew Mclntyre on " + LocalDate.now(),
                "William Regal sheared Lita on " + LocalDate.now());
        assertEquals(expectedLogs, shepherd.getLog());

    }

    @Test
    public void shepherdHasFiveAlpacasToShearThatAlsoHaveALogEntry() {
        Alpaca alpaca1 = new Alpaca("John Cena", 114.00, 4, BLACK);
        Alpaca alpaca2 = new Alpaca("Rey Mysterio", 79.00, 9, BROWN);
        Alpaca alpaca3 = new Alpaca("Randy Orten", 113.00, 3, BROWN);
        Alpaca alpaca4 = new Alpaca("Kelly Kelly", 49.00, 9, BROWN);
        Alpaca alpaca5 = new Alpaca("Maryse", 52.00, 2, BROWN);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        herd.addAlpaca(alpaca3);
        herd.addAlpaca(alpaca4);
        herd.addAlpaca(alpaca5);
        Pen pen = new Pen("Rumble ring", herd);
        Shepherd shepherd = new Shepherd("Zelina Vega", pen);

        shepherd.shear(alpaca1);
        shepherd.shear(alpaca2);
        shepherd.shear(alpaca3);
        shepherd.shear(alpaca4);
        shepherd.shear(alpaca5);

        List<String> wanted = List.of("Zelina Vega sheared John Cena on " + LocalDate.now(),
                "Zelina Vega sheared Rey Mysterio on " + LocalDate.now(),
                "Zelina Vega sheared Randy Orten on " + LocalDate.now(),
                "Zelina Vega sheared Kelly Kelly on " + LocalDate.now(),
                "Zelina Vega sheared Maryse on " + LocalDate.now()
        );

        assertEquals(wanted, shepherd.getLog());
    }

    @Test
    public void shepherdHasALogSizeThatIsZero(){
    Herd herd = new Herd();
    Pen pen = new Pen("Table Match", herd);
    Shepherd shepherd = new Shepherd("Lana", pen);


    int logSize = shepherd.logSize();

    assertEquals(0, logSize);

    }

    @Test
    public void shepHerdHasALogSizeThatIsOne() {
        Alpaca alpaca = new Alpaca("Shotzi", 55.00, 5, BLACK);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca);
        Pen pen = new Pen("Table Match", herd);
        Shepherd shepherd = new Shepherd("Lana", pen);

        shepherd.shear(alpaca);

        int logSize = shepherd.logSize();

        assertEquals(1, logSize);
    }

    @Test
    public void shepherdHasALogSizeThatIsThree(){
        Alpaca alpaca1 = new Alpaca("Mandy Rose", 54.00, 4, BEIGE);
        Alpaca alpaca2 = new Alpaca("Sonya Deville", 59.00, 4, BLACK);
        Alpaca alpaca3 = new Alpaca("Paige", 54.00, 4, BLACK);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        herd.addAlpaca(alpaca3);
        Pen pen = new Pen("Ring", herd);
        Shepherd shepherd = new Shepherd("Aj Lee", pen);

        shepherd.shear(alpaca1);
        shepherd.shear(alpaca2);
        shepherd.shear(alpaca3);

        int logSize = shepherd.logSize();

        assertEquals(3, logSize);
    }


}
