package alpacas;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
class DatabaseConfig {

    @Bean
    PenRepository penRepository(JdbcClient jdbcClient) {
        return new JdbcPenRepository(jdbcClient);
    }

    @Bean
    ApplicationRunner stockTheFarm(PenRepository pens) {
        return args -> {
            if (pens.findAll().isEmpty()) {
                Herd ringHerd = new Herd();
                ringHerd.addAlpaca(new Alpaca("Lita", 70, 4, FleeceColor.WHITE));
                ringHerd.addAlpaca(new Alpaca("Batista", 80, 10, FleeceColor.BROWN));
                pens.save(new Pen("Ring", ringHerd));
                pens.save(new Pen("Cell", new Herd()));
            }
        };
    }
}
