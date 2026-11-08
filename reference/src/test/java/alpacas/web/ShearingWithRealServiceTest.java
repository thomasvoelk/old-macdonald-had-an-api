package alpacas.web;

import alpacas.Alpaca;
import alpacas.PenRepository;
import alpacas.FleeceColor;
import alpacas.Herd;
import alpacas.Pen;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(ShearingController.class)
@Import({InMemoryFarmConfig.class, ShearingService.class})
class ShearingWithRealServiceTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    PenRepository pens;

    @Test
    void shearsAnAlpacaOfTheRealFarm() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Triple H", 85.5, 9, FleeceColor.BLACK));
        pens.save(new Pen("Barbershop", herd));

        assertThat(mvc.post().uri("/pens/{pen}/alpacas/{alpaca}/shear", "Barbershop", "Triple H"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.fleeceLengthCm").isEqualTo(0);
        assertThat(herd.findByName("Triple H").orElseThrow().getFleeceLength()).isEqualTo(0);
    }

    @Test
    void anUnknownAlpacaIsNotFound() {
        assertThat(mvc.post().uri("/pens/Ring/alpacas/Nobody/shear")).hasStatus(404);
    }
}
