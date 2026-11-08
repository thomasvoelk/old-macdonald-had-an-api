package alpacas.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(PenController.class)
@Import(InMemoryFarmConfig.class)
class PenChangesTest {

    private static final String TRIPLE_H = """
            {"name": "Triple H", "weight": 85.5, "fleeceLengthCm": 3, "fleeceColor": "BLACK"}""";

    @Autowired
    MockMvcTester mvc;

    // The farm bean is shared by all tests of the cached context, so every test
    // that changes something works in a pen of its own and leaves Ring and Cell alone.
    private void createPen(String name) {
        assertThat(mvc.post().uri("/pens")
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"" + name + "\"}"))
                .hasStatus(201);
    }

    @Test
    void createsAnAlpacaInAPen() {
        createPen("Nursery");
        assertThat(mvc.post().uri("/pens/Nursery/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content(TRIPLE_H))
                .hasStatus(201)
                .hasHeader("Location", "/pens/Nursery/alpacas/Triple%20H")
                .bodyJson()
                .extractingPath("$.fleeceColor").isEqualTo("BLACK");
        assertThat(mvc.get().uri("/pens/Nursery/alpacas")).bodyJson()
                .extractingPath("$.length()").isEqualTo(1);
    }

    @Test
    void createsAPen() {
        assertThat(mvc.post().uri("/pens")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Stable"}"""))
                .hasStatus(201)
                .hasHeader("Location", "/pens/Stable")
                .bodyJson()
                .extractingPath("$.alpacas").isEqualTo(0);
    }

    @Test
    void aPenNameCanOnlyBeUsedOnce() {
        assertThat(mvc.post().uri("/pens")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "ring"}"""))
                .hasStatus(409)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void aNameInAPenCanOnlyBeUsedOnce() {
        assertThat(mvc.post().uri("/pens/Ring/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Lita", "weight": 60, "fleeceLengthCm": 1, "fleeceColor": "GREY"}"""))
                .hasStatus(409)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("There is already an alpaca named Lita in pen Ring");
    }

    @Test
    void creatingInAnUnknownPenIsNotFound() {
        assertThat(mvc.post().uri("/pens/Nowhere/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content(TRIPLE_H))
                .hasStatus(404);
    }

    @Test
    void invalidFieldsAreReportedTogether() {
        assertThat(mvc.post().uri("/pens/Cell/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": " ", "weight": -5, "fleeceLengthCm": 11, "fleeceColor": "BLACK"}"""))
                .hasStatus(400)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .extractingPath("$.errors.length()").isEqualTo(3);
    }

    @Test
    void aMissingFieldIsAnError() {
        assertThat(mvc.post().uri("/pens/Cell/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Triple H", "weight": 85.5, "fleeceColor": "BLACK"}"""))
                .hasStatus(400)
                .bodyJson()
                .extractingPath("$.errors.fleeceLengthCm").isEqualTo("must not be null");
    }

    @Test
    void anUnknownFleeceColorIsABadRequest() {
        assertThat(mvc.post().uri("/pens/Cell/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Triple H", "weight": 85.5, "fleeceLengthCm": 3, "fleeceColor": "PURPLE"}"""))
                .hasStatus(400)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Unknown fleece color: PURPLE");
    }

    @Test
    void brokenJsonIsABadRequest() {
        assertThat(mvc.post().uri("/pens/Cell/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content("{ this is not json"))
                .hasStatus(400)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void aFullPenConflicts() {
        createPen("Crowd");
        for (int i = 1; i <= 30; i++) {
            assertThat(mvc.post().uri("/pens/Crowd/alpacas")
                    .contentType(MediaType.APPLICATION_JSON).content(
                            "{\"name\": \"Extra " + i + "\", \"weight\": 60, \"fleeceLengthCm\": 1, \"fleeceColor\": \"GREY\"}"))
                    .hasStatus(201);
        }
        assertThat(mvc.post().uri("/pens/Crowd/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content(TRIPLE_H))
                .hasStatus(409);
    }

    @Test
    void replacesAnAlpaca() {
        createPen("Workshop");
        assertThat(mvc.post().uri("/pens/Workshop/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content(TRIPLE_H)).hasStatus(201);
        assertThat(mvc.put().uri("/pens/Workshop/alpacas/Triple H")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"weight": 72.5, "fleeceLengthCm": 0, "fleeceColor": "GREY"}"""))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.weight").isEqualTo(72.5);
        assertThat(mvc.get().uri("/pens/Workshop/alpacas/Triple H")).bodyJson()
                .extractingPath("$.fleeceColor").isEqualTo("GREY");
    }

    @Test
    void replacingAnAlpacaKeepsItsId() throws Exception {
        createPen("Atelier");
        assertThat(mvc.post().uri("/pens/Atelier/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content(TRIPLE_H)).hasStatus(201);
        Object before = JsonPath.read(mvc.get().uri("/pens/Atelier/alpacas/Triple H")
                .exchange().getResponse().getContentAsString(), "$.id");

        assertThat(mvc.put().uri("/pens/Atelier/alpacas/Triple H")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"weight": 72.5, "fleeceLengthCm": 0, "fleeceColor": "GREY"}"""))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.id").isEqualTo(before);
    }

    @Test
    void replacingAnUnknownAlpacaIsNotFound() {
        assertThat(mvc.put().uri("/pens/Ring/alpacas/Nobody")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"weight": 72.5, "fleeceLengthCm": 0, "fleeceColor": "GREY"}"""))
                .hasStatus(404);
    }

    @Test
    void deletesAnAlpaca() {
        createPen("Auction");
        assertThat(mvc.post().uri("/pens/Auction/alpacas")
                .contentType(MediaType.APPLICATION_JSON).content(TRIPLE_H)).hasStatus(201);
        assertThat(mvc.delete().uri("/pens/Auction/alpacas/Triple H")).hasStatus(204);
        assertThat(mvc.get().uri("/pens/Auction/alpacas/Triple H")).hasStatus(404);
    }

    @Test
    void deletingAnUnknownAlpacaIsNotFound() {
        assertThat(mvc.delete().uri("/pens/Ring/alpacas/Nobody")).hasStatus(404);
    }
}
