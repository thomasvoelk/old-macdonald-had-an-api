package alpacas.web;

import alpacas.web.api.ShearingApi;
import alpacas.web.model.AlpacaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ShearingController implements ShearingApi {

    private final ShearingService shearingService;

    ShearingController(ShearingService shearingService) {
        this.shearingService = shearingService;
    }

    @Override
    public ResponseEntity<AlpacaResponse> shearAlpaca(String penName, String alpacaName) {
        return ResponseEntity.ok(WebMapping.toResponse(shearingService.shear(penName, alpacaName)));
    }
}
