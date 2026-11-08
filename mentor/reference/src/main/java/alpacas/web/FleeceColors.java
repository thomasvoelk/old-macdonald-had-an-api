package alpacas.web;

import alpacas.FleeceColor;

import java.util.Arrays;

final class FleeceColors {

    private FleeceColors() {
    }

    static FleeceColor parse(String text) {
        return Arrays.stream(FleeceColor.values())
                .filter(color -> color.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown fleece color: " + text));
    }
}
