package alpacas.web;

import alpacas.Alpaca;
import alpacas.Pen;
import alpacas.PenRepository;
import alpacas.Shepherd;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
class ShearingService {

    private final PenRepository pens;
    private final Map<String, Shepherd> shepherds = new HashMap<>();

    ShearingService(PenRepository pens) {
        this.pens = pens;
    }

    Alpaca shear(String penName, String alpacaName) {
        Pen pen = pens.getByName(penName);
        Alpaca alpaca = pen.getHerd().findByName(alpacaName)
                .orElseThrow(() -> new AlpacaNotFoundException(alpacaName, penName));
        shepherdOf(pen).shear(alpaca);
        pens.save(pen);
        return alpaca;
    }

    List<String> logOf(String penName) {
        return shepherdOf(pens.getByName(penName)).getLog();
    }

    private Shepherd shepherdOf(Pen pen) {
        return shepherds.computeIfAbsent(pen.getName().toLowerCase(Locale.ROOT),
                key -> new Shepherd("Paul Heyman", pen));
    }
}
