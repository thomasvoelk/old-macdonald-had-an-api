package alpacas.web;

import alpacas.Alpaca;
import alpacas.Herd;
import alpacas.Pen;
import alpacas.PenRepository;
import alpacas.web.api.PensApi;
import alpacas.web.model.AlpacaResponse;
import alpacas.web.model.CreateAlpacaRequest;
import alpacas.web.model.CreatePenRequest;
import alpacas.web.model.PenSummaryResponse;
import alpacas.web.model.UpdateAlpacaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
class PenController implements PensApi {

    private final PenRepository pens;

    PenController(PenRepository pens) {
        this.pens = pens;
    }

    @Override
    public ResponseEntity<List<PenSummaryResponse>> getPens() {
        return ResponseEntity.ok(pens.findAll().stream()
                .map(WebMapping::toResponse)
                .toList());
    }

    @Override
    public ResponseEntity<PenSummaryResponse> createPen(CreatePenRequest request) {
        Pen pen = pens.save(new Pen(request.name(), new Herd()));
        URI location = UriComponentsBuilder.fromPath("/pens/{penName}")
                .buildAndExpand(pen.getName()).encode().toUri();
        return ResponseEntity.created(location).body(WebMapping.toResponse(pen));
    }

    @Override
    public ResponseEntity<List<AlpacaResponse>> getAlpacasOfPen(String penName) {
        return ResponseEntity.ok(pens.getByName(penName).getHerd().getAlpacas().stream()
                .map(WebMapping::toResponse)
                .toList());
    }

    @Override
    public ResponseEntity<AlpacaResponse> getAlpaca(String penName, String alpacaName) {
        Pen pen = pens.getByName(penName);
        return ResponseEntity.ok(pen.getHerd().findByName(alpacaName)
                .map(WebMapping::toResponse)
                .orElseThrow(() -> new AlpacaNotFoundException(alpacaName, penName)));
    }

    @Override
    public ResponseEntity<List<AlpacaResponse>> getAlpacasReadyToShear() {
        return ResponseEntity.ok(pens.findAll().stream()
                .flatMap(pen -> pen.allAlpacasAreReadyToShear().stream())
                .map(WebMapping::toResponse)
                .toList());
    }

    @Override
    public ResponseEntity<AlpacaResponse> createAlpaca(String penName, CreateAlpacaRequest request) {
        Pen pen = pens.getByName(penName);
        if (pen.getHerd().findByName(request.name()).isPresent()) {
            throw new AlpacaAlreadyExistsException(request.name(), penName);
        }
        Alpaca alpaca = WebMapping.toAlpaca(request);
        pen.getHerd().addAlpaca(alpaca);
        pens.save(pen);
        URI location = UriComponentsBuilder.fromPath("/pens/{penName}/alpacas/{alpacaName}")
                .buildAndExpand(pen.getName(), alpaca.getName()).encode().toUri();
        return ResponseEntity.created(location).body(WebMapping.toResponse(alpaca));
    }

    @Override
    public ResponseEntity<AlpacaResponse> replaceAlpaca(String penName, String alpacaName,
                                                        UpdateAlpacaRequest request) {
        Pen pen = pens.getByName(penName);
        Alpaca existing = pen.getHerd().findByName(alpacaName)
                .orElseThrow(() -> new AlpacaNotFoundException(alpacaName, penName));
        Alpaca replacement = WebMapping.toAlpaca(alpacaName, request);
        replacement.assignId(existing.getId());
        pen.getHerd().replace(replacement);
        pens.save(pen);
        return ResponseEntity.ok(WebMapping.toResponse(replacement));
    }

    @Override
    public ResponseEntity<Void> deleteAlpaca(String penName, String alpacaName) {
        Pen pen = pens.getByName(penName);
        if (!pen.getHerd().removeByName(alpacaName)) {
            throw new AlpacaNotFoundException(alpacaName, penName);
        }
        pens.save(pen);
        return ResponseEntity.noContent().build();
    }
}
