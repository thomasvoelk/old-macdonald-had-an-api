package alpacas.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(FarmController.class)
@Import(InMemoryFarmConfig.class)
class FarmControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void getFarmReturnsNameAndCounts() {
        assertThat(mvc.get().uri("/farm"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.name").isEqualTo("Fiona's Farm");
        assertThat(mvc.get().uri("/farm"))
                .bodyJson()
                .extractingPath("$.pens").isEqualTo(2);
        assertThat(mvc.get().uri("/farm"))
                .bodyJson()
                .extractingPath("$.alpacas").isEqualTo(2);
    }
}
