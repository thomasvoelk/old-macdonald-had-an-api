package alpacas.web;

import alpacas.Alpaca;
import alpacas.FleeceColor;
import alpacas.Herd;
import alpacas.InMemoryPenRepository;
import alpacas.Pen;
import alpacas.PenRepository;
import org.springframework.context.annotation.Bean;

class InMemoryFarmConfig {

    @Bean
    PenRepository penRepository() {
        Herd ringHerd = new Herd();
        ringHerd.addAlpaca(new Alpaca("Lita", 70, 4, FleeceColor.WHITE));
        ringHerd.addAlpaca(new Alpaca("Batista", 80, 10, FleeceColor.BROWN));

        PenRepository pens = new InMemoryPenRepository();
        pens.save(new Pen("Ring", ringHerd));
        pens.save(new Pen("Cell", new Herd()));
        return pens;
    }
}
