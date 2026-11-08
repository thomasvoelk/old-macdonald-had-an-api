package alpacas.web;

import alpacas.Alpaca;
import alpacas.FleeceColor;
import alpacas.PenNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebMvcTest(ShearingController.class)
class ShearingControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    ShearingService shearingService;

    @Test
    void shearsAnAlpacaAndReturnsItWithoutFleece() {
        given(shearingService.shear("Ring", "Lita"))
                .willReturn(new Alpaca("Lita", 70, 0, FleeceColor.WHITE));

        assertThat(mvc.post().uri("/pens/Ring/alpacas/Lita/shear"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.fleeceLengthCm").isEqualTo(0);
        verify(shearingService).shear("Ring", "Lita");
    }

    @Test
    void anUnknownPenIsNotFound() {
        given(shearingService.shear("Nowhere", "Lita"))
                .willThrow(new PenNotFoundException("The Pen you are looking for Nowhere does not exist"));

        assertThat(mvc.post().uri("/pens/Nowhere/alpacas/Lita/shear"))
                .hasStatus(404)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void anUnknownAlpacaIsNotFound() {
        given(shearingService.shear("Ring", "Nobody"))
                .willThrow(new AlpacaNotFoundException("Nobody", "Ring"));

        assertThat(mvc.post().uri("/pens/Ring/alpacas/Nobody/shear"))
                .hasStatus(404)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("No alpaca named Nobody in pen Ring");
    }
}
