package alpacas.web;

import alpacas.Alpaca;
import alpacas.FleeceColor;
import alpacas.Herd;
import alpacas.InMemoryPenRepository;
import alpacas.Pen;
import alpacas.PenRepository;
import alpacas.PenNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShearingServiceTest {

    private ShearingService service;

    @BeforeEach
    void setUp() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Lita", 70, 10, FleeceColor.WHITE));
        PenRepository pens = new InMemoryPenRepository();
        pens.save(new Pen("Ring", herd));
        service = new ShearingService(pens);
    }

    @Test
    void shearingTakesAllTheFleeceOff() {
        Alpaca sheared = service.shear("Ring", "Lita");

        assertEquals("Lita", sheared.getName());
        assertEquals(0, sheared.getFleeceLength());
    }

    @Test
    void theShepherdWritesALogEntry() {
        service.shear("ring", "lita");

        assertEquals(List.of("Paul Heyman sheared Lita on " + LocalDate.now()), service.logOf("Ring"));
    }

    @Test
    void anUnknownPenIsNotFound() {
        assertThrows(PenNotFoundException.class, () -> service.shear("Nowhere", "Lita"));
    }

    @Test
    void anUnknownAlpacaIsNotFoundAndNothingIsLogged() {
        assertThrows(AlpacaNotFoundException.class, () -> service.shear("Ring", "Nobody"));

        assertEquals(List.of(), service.logOf("Ring"));
    }
}
