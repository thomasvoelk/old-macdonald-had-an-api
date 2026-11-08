package alpacas.web;

import alpacas.PenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FarmConfigTest {

    @Autowired
    PenRepository pens;

    @Test
    void repositoryIsAvailableAsBean() {
        assertThat(pens).isNotNull();
    }

    @Test
    void farmComesWithTwoPens() {
        assertThat(pens.findAll()).extracting(pen -> pen.getName()).containsExactly("Ring", "Cell");
    }
}
