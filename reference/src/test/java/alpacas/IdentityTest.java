package alpacas;

import org.junit.jupiter.api.Test;

import static alpacas.FleeceColor.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentityTest {

    @Test
    void aNewAlpacaHasNoIdYet() {
        Alpaca alpaca = new Alpaca("Lita", 70, 4, WHITE);

        assertNull(alpaca.getId());
    }

    @Test
    void anAlpacaCanBeGivenAnIdOnce() {
        Alpaca alpaca = new Alpaca("Lita", 70, 4, WHITE);

        alpaca.assignId(7);

        assertEquals(7L, alpaca.getId());
    }

    @Test
    void anAlpacaKeepsItsIdForLife() {
        Alpaca alpaca = new Alpaca("Lita", 70, 4, WHITE);
        alpaca.assignId(7);

        assertThrows(IllegalStateException.class, () -> alpaca.assignId(8));
    }

    @Test
    void aNewPenHasNoIdYet() {
        Pen pen = new Pen("Ring", new Herd());

        assertNull(pen.getId());
    }

    @Test
    void aPenCanBeGivenAnIdOnce() {
        Pen pen = new Pen("Ring", new Herd());

        pen.assignId(3);

        assertEquals(3L, pen.getId());
        assertThrows(IllegalStateException.class, () -> pen.assignId(4));
    }
}
