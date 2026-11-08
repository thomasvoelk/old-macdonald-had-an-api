package alpacas.web;

import alpacas.Pen;
import alpacas.PenRepository;
import alpacas.web.api.FarmApi;
import alpacas.web.model.FarmResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class FarmController implements FarmApi {

    private static final String FARM_NAME = "Fiona's Farm";

    private final PenRepository pens;

    FarmController(PenRepository pens) {
        this.pens = pens;
    }

    @Override
    public ResponseEntity<FarmResponse> getFarm() {
        int totalAlpacas = pens.findAll().stream().mapToInt(Pen::totalAlpacas).sum();
        return ResponseEntity.ok(new FarmResponse(FARM_NAME, pens.findAll().size(), totalAlpacas));
    }
}
