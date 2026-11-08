package alpacas.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(PenController.class)
@Import(InMemoryFarmConfig.class)
class PenControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void listsAllPensWithTheirAlpacaCount() {
        assertThat(mvc.get().uri("/pens"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].name").isEqualTo("Ring");
        assertThat(mvc.get().uri("/pens"))
                .bodyJson()
                .extractingPath("$[0].alpacas").isEqualTo(2);
        assertThat(mvc.get().uri("/pens"))
                .bodyJson()
                .extractingPath("$[1].alpacas").isEqualTo(0);
    }

    @Test
    void listsTheAlpacasOfAPen() {
        assertThat(mvc.get().uri("/pens/Ring/alpacas"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].name").isEqualTo("Lita");
        assertThat(mvc.get().uri("/pens/Ring/alpacas"))
                .bodyJson()
                .extractingPath("$[0].fleeceLengthCm").isEqualTo(4);
        assertThat(mvc.get().uri("/pens/Ring/alpacas"))
                .bodyJson()
                .extractingPath("$[1].fleeceColor").isEqualTo("BROWN");
    }

    @Test
    void penNamesAreCaseInsensitive() {
        assertThat(mvc.get().uri("/pens/ring/alpacas")).hasStatusOk();
    }

    @Test
    void unknownPenIsNotFound() {
        assertThat(mvc.get().uri("/pens/Nowhere/alpacas"))
                .hasStatus(404)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("The Pen you are looking for Nowhere does not exist");
    }

    @Test
    void returnsASingleAlpacaByName() {
        assertThat(mvc.get().uri("/pens/Ring/alpacas/lita"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.name").isEqualTo("Lita");
    }

    @Test
    void unknownAlpacaIsNotFound() {
        assertThat(mvc.get().uri("/pens/Ring/alpacas/Nobody"))
                .hasStatus(404)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("No alpaca named Nobody in pen Ring");
    }

    @Test
    void alpacaInUnknownPenIsNotFound() {
        assertThat(mvc.get().uri("/pens/Nowhere/alpacas/Lita")).hasStatus(404);
    }

    @Test
    void listsAlpacasReadyToShearAcrossTheFarm() {
        assertThat(mvc.get().uri("/alpacas/ready-to-shear"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()").isEqualTo(1);
        assertThat(mvc.get().uri("/alpacas/ready-to-shear"))
                .bodyJson()
                .extractingPath("$[0].name").isEqualTo("Batista");
    }

    @Test
    void pensAndAlpacasShowTheirIds() {
        assertThat(mvc.get().uri("/pens")).bodyJson()
                .extractingPath("$[0].id").isNotNull();
        assertThat(mvc.get().uri("/pens/Ring/alpacas")).bodyJson()
                .extractingPath("$[0].id").isNotNull();
        assertThat(mvc.get().uri("/pens/Ring/alpacas/Lita")).bodyJson()
                .extractingPath("$.id").isNotNull();
    }
}
