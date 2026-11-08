package alpacas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.List;

import static alpacas.FleeceColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class HerdTest {

    @Test
    public void herdShouldNotHaveNull() {
        Herd herd = new Herd();

        assertThrows(IllegalArgumentException.class, () -> herd.addAlpaca(null));
    }

    @Test
    public void newHerdHasZeroAlpaca() {
        Herd herd = new Herd();

        int alpacaCounter = herd.getAlpacaCount();

        assertEquals(0, alpacaCounter);
    }


    @Test
    public void herdHasOneAlpaca() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("One", 100.00, 1, BLACK);

        herd.addAlpaca(alpaca);
        int alpacaCounter = herd.getAlpacaCount();


        assertEquals(1, alpacaCounter);
    }

    @Test
    public void herdHasTwoAlpacas() {
        Herd herd = new Herd();

        for (int i = 1; i <= 2; ++i) {
            Alpaca alpaca = new Alpaca("Two", 120.00, 2, BEIGE);
            herd.addAlpaca(alpaca);
        }

        int alpacaCounter = herd.getAlpacaCount();

        assertEquals(2, alpacaCounter);
    }

    @Test
    public void herdHasTenAlpacas() {
        Herd herd = new Herd();

        for (int i = 1; i <= 10; ++i) {
            Alpaca alpaca = new Alpaca("Ten", 100.00, 10, BROWN);
            herd.addAlpaca(alpaca);
        }

        int alpacaCounter = herd.getAlpacaCount();

        assertEquals(10, alpacaCounter);
    }

    @Test
    public void herdHasContains() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("The Rock", 118.00, 8, BROWN);

        herd.addAlpaca(alpaca);

        boolean isAlpacaContainedInHerd = herd.contains(alpaca);

        assertTrue(isAlpacaContainedInHerd);
    }

    @Test
    public void findByNameReturnsEmptyIfHerdIsEmpty() {
        Herd herd = new Herd();

        Optional<Alpaca> alpaca = herd.findByName("John Cena");

        assertTrue(alpaca.isEmpty());
    }


    @Test
    public void findByNameReturnsEmptyIfNameNotFound() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Hui Buh", 0.0, 0, WHITE));

        Optional<Alpaca> alpaca = herd.findByName("John Cena");

        assertTrue(alpaca.isEmpty());
    }

    @Test
    public void findByNameReturnsAlpacaIfNameIsFound_withOneAlpacaInHerd() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("John Cena", 114.00, 4, WHITE));

        Optional<Alpaca> alpaca = herd.findByName("John Cena");

        assertEquals("John Cena", alpaca.orElseThrow().getName());

    }

    @Test
    public void findByNameReturnsAlpacaIfNameIsFound_withTwoAlpacasInHerd() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Cm Punk", 98.00, 8, BLACK));
        herd.addAlpaca(new Alpaca("John Cena", 114.00, 4, WHITE));

        Optional<Alpaca> alpaca = herd.findByName("John Cena");

        assertEquals("John Cena", alpaca.orElseThrow().getName());

    }

    @ParameterizedTest
    @ValueSource(strings = {"CM PUNK", "cm punk", "CM punk", "cm Punk", "Cm punk", "cM PUNK", "Cm Punk"})
    public void findByNameMultipleAlpacasAreCaseInsensitive(String differentCastNames) {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Cm Punk", 99.00, 9, BLACK));

        Optional<Alpaca> alpaca = herd.findByName(differentCastNames);

        assertEquals("Cm Punk", alpaca.orElseThrow().getName());

    }

    @Test
    public void zeroAlpacasAreReadyToBeSheared() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Kelly Kelly", 49.00, 9,WHITE));
        herd.addAlpaca(new Alpaca("Trish Stratus", 57.00, 7,WHITE));

        List<Alpaca> result = herd.getAlpacasReadyToShear();

        assertEquals(0, result.size());
    }

    @Test
    public void oneAlpacaIsReadyToBeSheared() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Randy Orton", 125.00, 5, GREY));
        herd.addAlpaca(new Alpaca("The Miz", 100.00, 10, BEIGE));

        List<Alpaca> result = herd.getAlpacasReadyToShear();
        Alpaca alpaca = result.getFirst();

        assertEquals(1, result.size());
        assertEquals("The Miz", alpaca.getName());
    }

    @Test
    public void tenAlpacasAreReadyToBeSheared() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Ember Moon", 59.00, 10, BLACK));
        herd.addAlpaca(new Alpaca("The Undertaker", 140.00, 10,BLACK));
        herd.addAlpaca(new Alpaca("Seth Rollins", 98.00, 10, BLACK));
        herd.addAlpaca(new Alpaca("Charlotte Flair", 65.00, 10, BEIGE));
        herd.addAlpaca(new Alpaca("Edge", 109.00, 10, BEIGE));
        herd.addAlpaca(new Alpaca("Becky Lynch", 61.00, 10, BEIGE));
        herd.addAlpaca(new Alpaca("Kevin Owens", 121.00, 10, BROWN));
        herd.addAlpaca(new Alpaca("Batista", 131.00, 10, GREY));
        herd.addAlpaca(new Alpaca("Rey Mysterio", 79.00, 10, BROWN));
        herd.addAlpaca(new Alpaca("Kane", 147.00, 10, BLACK));

        List<Alpaca> result = herd.getAlpacasReadyToShear();

        assertEquals(10, result.size());
    }

    @Test
    public void threeOutOfFiveAlpacasAreReadyToShear() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Bianca Belair", 75.00, 5, BLACK));
        herd.addAlpaca(new Alpaca("Gunter", 130.00, 10, BROWN));
        herd.addAlpaca(new Alpaca("Asuka", 62.00, 10, BEIGE));
        herd.addAlpaca(new Alpaca("Kevin Owens", 121.00, 10, BROWN));
        herd.addAlpaca(new Alpaca("Bayley", 54.00, 4, BROWN));

        List<Alpaca> result = herd.getAlpacasReadyToShear();

        assertEquals(3, result.size());
    }

    @Test
    public void noAlpacasForTotalFleeceLength() {
        Herd herd = new Herd();

        int totalFleeceLength = herd.totalFleeceLength();

        assertEquals(0, totalFleeceLength);
    }

    @Test
    public void oneAlpacaWithZeroTotalFleeceLength() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Bianca Belair", 75.00, 0, BLACK));

        int totalFleeceLength = herd.totalFleeceLength();

        assertEquals(0, totalFleeceLength);
    }

    @Test
    public void oneAlpacaHasOneTotalFleeceLength() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Bayley", 54.00, 1, BROWN));

        int totalFleeceLength = herd.totalFleeceLength();

        assertEquals(1, totalFleeceLength);
    }

    @Test
    public void fiveAlpacasHaveFiveTotalFleeceLength() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Gunter", 130.00, 1, BROWN));
        herd.addAlpaca(new Alpaca("Becky Lynch", 61.00, 1, BEIGE));
        herd.addAlpaca(new Alpaca("Kevin Owens", 121.00, 1, BROWN));
        herd.addAlpaca(new Alpaca("Batista", 131.00, 1, GREY));
        herd.addAlpaca(new Alpaca("Charlotte Flair", 65.00, 1, WHITE));

        int totalFleeceLength = herd.totalFleeceLength();

        assertEquals(5, totalFleeceLength);
    }

    @Test
    public void fiveAlpacasHaveThirtyTwoTotalFleeceLength() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Asuka", 62.00, 5, BEIGE));
        herd.addAlpaca(new Alpaca("The Miz", 100.00, 8, BROWN));
        herd.addAlpaca(new Alpaca("Kelly Kelly", 49.00, 9, BEIGE));
        herd.addAlpaca(new Alpaca("Trish Stratus", 57.00, 7, BEIGE));
        herd.addAlpaca(new Alpaca("Cm Punk", 99.00, 3, BLACK));

        int totalFleeceLength = herd.totalFleeceLength();

        assertEquals(32, totalFleeceLength);
    }

    @Test
    public void noAlpacasForAverageWeight() {
        Herd herd = new Herd();

        double averageWeight = herd.averageWeight();

        assertEquals(0.00, averageWeight);
    }

    @Test
    public void oneAlpacaHasZeroKgWeight() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Iyo Sky", 0.00, 3, BROWN));

        double averageWeight = herd.averageWeight();

        assertEquals(0.00, averageWeight);

    }

    @Test
    public void oneAlpacaWithOneKgWeight() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Kairi Sane", 1.00, 3, WHITE));

        double averageWeight = herd.averageWeight();

        assertEquals(1.00, averageWeight);
    }

    @Test
    public void fiveAlpacasWithTheSameWeight() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Lyra Valkyria", 57.00, 4, BROWN));
        herd.addAlpaca(new Alpaca("Chelsea Green", 57.00, 4, BROWN));
        herd.addAlpaca(new Alpaca("Piper Niven", 57.00, 4, BEIGE));
        herd.addAlpaca(new Alpaca("Bron Breaker", 57.00, 4, BROWN));
        herd.addAlpaca(new Alpaca("Carmelo Hayes", 57.00, 4, BROWN));

        double averageWeight = herd.averageWeight();

        assertEquals(57.00, averageWeight);
    }

    @Test
    public void fiveAlpacasWithDifferentWeight() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Dragon Lee", 75.00, 4, BLACK));
        herd.addAlpaca(new Alpaca("Dominik Mysterio", 91.00, 4, BLACK));
        herd.addAlpaca(new Alpaca("Rhea Ripley", 62.00, 4, BLACK));
        herd.addAlpaca(new Alpaca("Tiffany Stratton", 57.00, 4, BEIGE));
        herd.addAlpaca(new Alpaca("LA Knight", 109.00, 4, BROWN));

        double averageWeight = herd.averageWeight();

        assertEquals(78.8, averageWeight);
    }

    @Test
    public void noAlpacasForHeaviestWeight() {
        Herd herd = new Herd();

        Alpaca alpaca = herd.heaviestAlpaca();

        assertNull(alpaca);
    }

    @Test
    public void oneAlpacaHasZeroWeightForHeaviestWeight() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("Chyna", 0.00, 0, BLACK);
        herd.addAlpaca(alpaca);

        Alpaca heaviestAlpaca = herd.heaviestAlpaca();

        assertEquals(alpaca, heaviestAlpaca);
        assertEquals("Chyna", alpaca.getName());
    }

    @Test
    public void oneAlpacaHasOneKgWeightForHeaviestWeight() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("Gunter", 1.00, 1, BROWN);
        herd.addAlpaca(alpaca);

        Alpaca heaviestAlpaca = herd.heaviestAlpaca();

        assertEquals(alpaca, heaviestAlpaca);
        assertEquals("Gunter", alpaca.getName());
    }

    @Test
    public void fiveAlpacasHaveDifferentWeightForHeaviestWeight() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("Dragon Lee", 75.00, 4, BROWN);
        Alpaca alpaca1 = new Alpaca("Dominik Mysterio", 91.00, 4, BLACK);
        Alpaca alpaca2 = new Alpaca("Rhea Ripley", 62.00, 4, BLACK);
        Alpaca alpaca3 = new Alpaca("Tiffany Stratton", 57.00, 4, BEIGE);
        Alpaca alpaca4 = new Alpaca("LA Knight", 109.00, 4, BROWN);
        herd.addAlpaca(alpaca);
        herd.addAlpaca(alpaca1);
        herd.addAlpaca(alpaca2);
        herd.addAlpaca(alpaca3);
        herd.addAlpaca(alpaca4);

        Alpaca heaviestAlpaca = herd.heaviestAlpaca();

        assertEquals(alpaca4, heaviestAlpaca);

        assertEquals("LA Knight", alpaca4.getName());

    }

    @Test
    public void thereCantBeMoreThanNintyNineAlpacas() {
        Herd herd = new Herd();
        for (int i = 0; i < 30; i++) {
            herd.addAlpaca(new Alpaca("James", 68.00, 8, GREY));
        }

        assertThrows(IllegalStateException.class, () -> herd.addAlpaca(new Alpaca("James", 68.00, 8, GREY)));
    }

    @Test
    public void thereCantBeTheSameAlpacaTwice() {
        Herd herd = new Herd();
        Alpaca alpaca = new Alpaca("James", 68.00, 8, GREY);
        herd.addAlpaca(alpaca);

        assertThrows(IllegalArgumentException.class, () -> herd.addAlpaca(alpaca));
    }

    @Test
    void newHerdHasNoAlpaca() {
        Herd herd = new Herd();

        List<Alpaca> alpacas = herd.getAlpacas();

        assertIterableEquals(List.of(), alpacas);
    }

    @Test
    void weCanAddAlpacasToHerd() {
        Herd herd = new Herd();
        List<Alpaca> addedAlpacas = List.of(
                new Alpaca("Alpaca 1", 100, 1, BLACK),
                new Alpaca("Alpaca 2", 200, 2, BEIGE),
                new Alpaca("Alpaca 3", 300, 3, WHITE)
        );
        addedAlpacas.forEach(herd::addAlpaca);


        List<Alpaca> alpacas = herd.getAlpacas();

        assertIterableEquals(addedAlpacas, alpacas);
    }

    @Test
    void removeByNameTakesTheAlpacaOutOfTheHerd() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Lita", 70, 4, WHITE));

        boolean removed = herd.removeByName("LITA");

        assertTrue(removed);
        assertEquals(0, herd.getAlpacaCount());
    }

    @Test
    void removeByNameReturnsFalseForAnUnknownName() {
        Herd herd = new Herd();

        assertFalse(herd.removeByName("Lita"));
    }

    @Test
    void replaceSwapsTheAlpacaWithTheSameName() {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Lita", 70, 4, WHITE));
        herd.addAlpaca(new Alpaca("Batista", 80, 10, BROWN));

        boolean replaced = herd.replace(new Alpaca("Lita", 72, 0, GREY));

        assertTrue(replaced);
        assertEquals(2, herd.getAlpacaCount());
        assertEquals(72, herd.getAlpacas().get(0).getWeight());
        assertEquals("Batista", herd.getAlpacas().get(1).getName());
    }

    @Test
    void replaceReturnsFalseWhenNoAlpacaHasThatName() {
        Herd herd = new Herd();

        assertFalse(herd.replace(new Alpaca("Lita", 70, 4, WHITE)));
    }
}
