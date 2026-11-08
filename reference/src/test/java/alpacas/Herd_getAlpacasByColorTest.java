package alpacas;

import org.junit.jupiter.api.Test;

import java.util.List;

import static alpacas.FleeceColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class Herd_getAlpacasByColorTest {

    @Test
    public void shouldThrowWhenFleeceColorIsNull() {
        MethodExecutionCountingHerd herd = new MethodExecutionCountingHerd();
        Alpaca alpaca = new Alpaca("James", 77.88, 6, BLACK);
        herd.addAlpaca(alpaca);

        assertThrows(FleeceColorNotFoundException.class, () -> herd.getAlpacasByColor(null));
        assertEquals(0, herd.getAllSameColorAlpacasCounter());
    }

    @Test
    public void shouldReturnNoAlpaca_whenHerdHasNoAlpacas() {
        Herd herd = new Herd();

        List<Alpaca> allAlpacasWithTheSameColor = herd.getAlpacasByColor(GREY);

        assertTrue(allAlpacasWithTheSameColor.isEmpty());
    }

    @Test
    public void shouldReturnNoAlpaca_whenHerdHasAlpacaWithOneColor_andWeLookForDifferentColor() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("James", 77.66, 7, GREY);
        herd.addAlpaca(alpaca);

        List<Alpaca> allAlpacasWithTheSameColor = herd.getAlpacasByColor(BEIGE);

        assertTrue(allAlpacasWithTheSameColor.isEmpty());
    }

    @Test
    public void shouldReturnOneAlpaca_whenHerdHasAlpacaWithOneColor_andWeLookForTheSameColor() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("Batista", 131.00, 1, GREY);
        herd.addAlpaca(alpaca);

        List<Alpaca> allAlpacasWithTheSameColor = herd.getAlpacasByColor(GREY);

        assertEquals(1, allAlpacasWithTheSameColor.size());
    }

    @Test
    public void shouldReturnTwoAlpacas_whenHerdHasAlpacasWithOneColor_andWeLookForTheSameColor() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Ric Flair", 110.00, 10, WHITE));
        herd.addAlpaca(new Alpaca("Charlotte Flair", 66.00, 6, WHITE));

        List<Alpaca> allAlpacasWithTheSameColor = herd.getAlpacasByColor(WHITE);

        assertEquals(2, allAlpacasWithTheSameColor.size());
    }

    @Test
    public void shouldReturnOneAlpaca_whenHerdHasAlpacasWithTwoColors_andWeLookForOneColor() {
        Herd herd = new Herd();
        Alpaca alpaca1 = new Alpaca("Randy Orton", 125.00, 5, GREY);
        Alpaca alpaca2 = new Alpaca("Micky James", 59.00, 5, BEIGE);
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);

        List<Alpaca> allAlpacasWithTheSameColor = herd.getAlpacasByColor(BEIGE);

        assertEquals(1, allAlpacasWithTheSameColor.size());
        assertTrue(allAlpacasWithTheSameColor.contains(alpaca2));
    }

    @Test
    public void shouldReturnTwoAlpacas_whenHerdHasAlpacasWithTwoColors_andWeLookForOneColor() {
        Herd herd = new Herd();
        Alpaca alpaca1 = new Alpaca("William Regal", 110.00, 10, GREY);
        Alpaca alpaca2 = new Alpaca("AJ Lee", 52.00, 2, BLACK);
        Alpaca alpaca3 = new Alpaca("Cm Punk", 99.00, 9, BLACK);
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        herd.addAlpaca(alpaca3);

        List<Alpaca> allAlpacasWithTheSameColor = herd.getAlpacasByColor(BLACK);

        List<Alpaca> wanted = List.of(alpaca2, alpaca3);

        assertEquals(2, allAlpacasWithTheSameColor.size());
        assertTrue(allAlpacasWithTheSameColor.containsAll(wanted));
    }
}
