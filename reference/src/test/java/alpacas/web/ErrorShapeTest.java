package alpacas.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ErrorShapeTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void anUnknownAddressAnswersWithAProblem() {
        assertThat(mvc.get().uri("/nothing"))
                .hasStatus(404)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void aWrongMethodAnswersWithAProblem() {
        assertThat(mvc.get().uri("/pens/Ring/alpacas/Lita/shear"))
                .hasStatus(405)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }
}
