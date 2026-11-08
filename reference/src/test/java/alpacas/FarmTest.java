package alpacas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static alpacas.FleeceColor.BLACK;
import static alpacas.FleeceColor.BROWN;
import static org.junit.jupiter.api.Assertions.*;

public class FarmTest {

    private Pen createPen(String name, String... alpacaNames) {
        Herd herd = new Herd();
        for (String alpacaName : alpacaNames) {
            herd.addAlpaca(new Alpaca(alpacaName, 100.00, 5, BROWN));
        }
        return new Pen(name, herd);
    }

    @Test
    public void farmMustHaveAName() {
        assertThrows(IllegalArgumentException.class, () -> new Farm(null));
    }

    @Test
    public void farmHasHasNoPens() {
        Farm farm = new Farm("Summerslam");

        int pensCounter = farm.getPensCount();

        assertEquals(0, pensCounter);
    }

    @Test
    public void farmHasOnePen() {
        Farm farm = new Farm("Hell in a Cell");
        Pen pen = new Pen("Cell", new Herd());
        farm.addPen(pen);

        int pensCounter = farm.getPensCount();

        assertEquals(1, pensCounter);

    }

    @Test
    public void farmHasFivePensAndZeroAlpacas() {
        Farm farm = new Farm("Raw");
        farm.addPen(createPen("Ring 1"));
        farm.addPen(createPen("Ring 2"));
        farm.addPen(createPen("Ring 3"));
        farm.addPen(createPen("Ring 4"));
        farm.addPen(createPen("Ring 5"));

        int totalAlpacas = farm.totalAlpacas();

        assertEquals(0, totalAlpacas);
    }

    @Test
    public void farmHasOnePenAndSixAlpacas() {
        Pen pen = createPen("Ring", "Mr. Kennedy", "Paul London", "Chris Masters", "Carlito", "Charlie Haas", "Jimmy Wang Yang");
        Farm farm = new Farm("Smackdown");
        farm.addPen(pen);

        int totalAlpacas = farm.totalAlpacas();

        assertEquals(6, totalAlpacas);

    }

    @Test
    public void farmHasFivePensAndSixAlpacas() {
        Farm farm = new Farm("WrestleMania");
        farm.addPen(createPen("Round 1", "The Boogeymann"));
        farm.addPen(createPen("Round 2", "The Hurricane"));
        farm.addPen(createPen("Round 3", "Sylvain Grenier", "Nunzio"));
        farm.addPen(createPen("Round 4", "Super Crazy"));
        farm.addPen(createPen("Round 5", "Snitsky"));


        int totalAlpaca = farm.totalAlpacas();

        assertEquals(6, totalAlpaca);
    }

    @Test
    public void penShouldNeverBeNull() {
        Farm farm = new Farm("Inferno");

        assertThrows(PenNotFoundException.class, () -> farm.findPen(null));
    }

    @Test
    public void farmHasNoPenWhichIsWrongAndWeAreSearchingForRing() {
        Farm farm = new Farm("Raw");

        assertThrows(PenNotFoundException.class, () -> farm.findPen("Ring"));
    }

    @Test
    public void farmHasAPenNamedRingWeAreSearchingForRing() {
        Farm farm = new Farm("Raw");
        farm.addPen(createPen("Ring"));

        Pen pen = farm.findPen("Ring");

        assertEquals("Ring", pen.getName());

    }

    @Test
    public void farmHasAPenNamedRingWeAreSearchingForCell() {
        Farm farm = new Farm("Smackdown");
        farm.addPen(createPen("Ring"));

        assertThrows(PenNotFoundException.class, () -> farm.findPen("Cell"));
    }

    @Test
    public void farmHasTwoPensButIsSearchingForOne() {
        Farm farm = new Farm("Hell In A Cell");
        farm.addPen(createPen("Ring"));
        farm.addPen(createPen("Cell"));

        Pen pen = farm.findPen("Cell");

        assertEquals("Cell", pen.getName());
    }

    @Test
    public void farmHasOnePenNamedRingButWeAreSearchingTheLowercase() {
        Farm farm = new Farm("Smackdown vs Raw");
        farm.addPen(createPen("Ring"));

        Pen pen = farm.findPen("ring");

        assertEquals("Ring", pen.getName());
    }

    @Test
    public void farmHasNoAlpacasInPenAndIsNotReadyToShear() {
        Farm farm = new Farm("Inferno");
        farm.addPen(createPen("Fire Ring"));

        List<Alpaca> alpacasAreSheared = farm.allAlpacasReadyToShear();

        assertTrue(alpacasAreSheared.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5})
    public void farmHasOneAlpacaInPenWithDifferentCmFleece(int cmAdded) {
        Alpaca alpaca = new Alpaca("Lita", 47.00, cmAdded, BLACK);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca);
        Pen pen = new Pen("Ring", herd);
        Farm farm = new Farm("WrestleMania");
        farm.addPen(pen);

        List<Alpaca> alpacasAreSheared = farm.allAlpacasReadyToShear();

        assertTrue(alpacasAreSheared.isEmpty());


    }

    @Test
    public void farmHasOneAlpacaInPenWithFleeceIsReadyToShear() {
        Alpaca alpaca = new Alpaca("Lita", 47.00, 10, BROWN);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca);
        Pen pen = new Pen("Cage", herd);
        Farm farm = new Farm("War Games");
        farm.addPen(pen);

        List<Alpaca> alpacasAreSheared = farm.allAlpacasReadyToShear();

        assertTrue(alpacasAreSheared.contains(alpaca));
        assertEquals(1, alpacasAreSheared.size());
    }

    @Test
    public void farmHasTwoAlpacasInPenWithFleeceIsReadyToShear() {
        Alpaca alpaca1 = new Alpaca("Jeff Hardy", 99.00, 10, BLACK);
        Alpaca alpaca2 = new Alpaca("Matt Hardy", 106.00, 10, BLACK);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        Pen pen = new Pen("The Ring of Fire", herd);
        Farm farm = new Farm("Inferno");
        farm.addPen(pen);

        List<Alpaca> alpacasAreSheared = farm.allAlpacasReadyToShear();
        List<Alpaca> wanted = List.of(alpaca1, alpaca2);

        assertTrue(alpacasAreSheared.containsAll(wanted));
        assertEquals(2, alpacasAreSheared.size());
    }

    @Test
    public void farmHasTwoAlpacasInPenWithOneBeingFleeceIsReadyToShear() {
        Alpaca alpaca1 = new Alpaca("Jeff Hardy", 99.00, 10, BLACK);
        Alpaca alpaca2 = new Alpaca("Matt Hardy", 99.00, 6, BLACK);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        Pen pen = new Pen("Ladder Match", herd);
        Farm farm = new Farm("TLC");
        farm.addPen(pen);

        List<Alpaca> alpacasAreSheared = farm.allAlpacasReadyToShear();

        assertTrue(alpacasAreSheared.contains(alpaca1));
        assertEquals(1, alpacasAreSheared.size());
    }

    @Test
    public void farmHasFiveAlpacasInPenWithThreeBeingFleeceIsReadyToShear() {
        Alpaca alpaca1 = new Alpaca("Jeff Hardy", 99.00, 10, BLACK);
        Alpaca alpaca2 = new Alpaca("Matt Hardy", 99.00, 6, BLACK);
        Alpaca alpaca3 = new Alpaca("Jeff Hardy", 99.00, 10, BLACK);
        Alpaca alpaca4 = new Alpaca("Matt Hardy", 99.00, 6, BLACK);
        Alpaca alpaca5 = new Alpaca("Jeff Hardy", 99.00, 10, BLACK);
        Herd herd = new Herd();
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        herd.addAlpaca(alpaca3);
        herd.addAlpaca(alpaca4);
        herd.addAlpaca(alpaca5);
        Pen pen = new Pen("Ladder Match", herd);
        Farm farm = new Farm("TLC");
        farm.addPen(pen);

        List<Alpaca> alpacasAreSheard = farm.allAlpacasReadyToShear();
        List<Alpaca> wanted = List.of(alpaca1, alpaca3, alpaca5);

        assertTrue(alpacasAreSheard.containsAll(wanted));
        assertEquals(3, alpacasAreSheard.size());

    }

    @Test
    public void farmHasName() {
        Farm farm = new Farm("Summerslam");

        String getFarmName = farm.getName();

        assertEquals("Summerslam", getFarmName);
    }

    @Test
    void newFarmHasNoPens() {
        Farm farm = new Farm("Winterslam");

        List<Pen> pens = farm.getPens();

        assertIterableEquals(List.of(), pens);
    }

    @Test
    void weCanAddPensToFarm() {
        Farm farm = new Farm("Easterslam");
        List<Pen> addedPens = List.of(
                new Pen("Pen 1", new Herd()),
                new Pen("Pen 2", new Herd()),
                new Pen("Pen 3", new Herd())
        );
        addedPens.forEach(farm::addPen);


        List<Pen> pens = farm.getPens();

        assertIterableEquals(addedPens, pens);
    }

    @Test
    void twoPensCannotHaveTheSameName() {
        Farm farm = new Farm("Summerslam");
        farm.addPen(createPen("Ring"));

        assertThrows(IllegalStateException.class, () -> farm.addPen(createPen("ring")));
    }
}
