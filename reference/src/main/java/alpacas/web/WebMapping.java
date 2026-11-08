package alpacas.web;

import alpacas.Alpaca;
import alpacas.Pen;
import alpacas.web.model.AlpacaResponse;
import alpacas.web.model.CreateAlpacaRequest;
import alpacas.web.model.PenSummaryResponse;
import alpacas.web.model.UpdateAlpacaRequest;

final class WebMapping {

    private WebMapping() {
    }

    static AlpacaResponse toResponse(Alpaca alpaca) {
        return new AlpacaResponse(
                alpaca.getId(),
                alpaca.getName(),
                alpaca.getWeight(),
                alpaca.getFleeceLength(),
                alpaca.getFleeceColor().name());
    }

    static PenSummaryResponse toResponse(Pen pen) {
        return new PenSummaryResponse(pen.getId(), pen.getName(), pen.totalAlpacas());
    }

    static Alpaca toAlpaca(CreateAlpacaRequest request) {
        return new Alpaca(request.name(), request.weight(), request.fleeceLengthCm(),
                FleeceColors.parse(request.fleeceColor()));
    }

    static Alpaca toAlpaca(String name, UpdateAlpacaRequest request) {
        return new Alpaca(name, request.weight(), request.fleeceLengthCm(),
                FleeceColors.parse(request.fleeceColor()));
    }
}
