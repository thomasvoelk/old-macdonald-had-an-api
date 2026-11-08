package alpacas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static alpacas.FleeceColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlpacaTest {

    @ParameterizedTest
    @ValueSource(strings = {"Alma", "John Cena", "The Rock", "AJ Lee", "Paige"})
    public void alpacasHaveNames(String alpacaName) {
        Alpaca alpaca = new Alpaca(alpacaName, 70.00, 5, BLACK);

        String name = alpaca.getName();

        assertEquals(alpacaName, name);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "77.00, true",
            "114.00, false",
            "118.00, false",
            "52.00, false",
            "55.00, true"})
    public void alpacasHaveWeight(double alpacaWeight, boolean expectedHealthy) {
        Alpaca alpaca = new Alpaca("The OGs", alpacaWeight, 5, BEIGE);

        boolean healthy = alpaca.isHealthyWeight();

        assertEquals(expectedHealthy, healthy);
    }


    @ParameterizedTest
    @ValueSource(doubles = {55.00, 90.00})
    public void alpacasAreHealthy(double healthyWeight) {
        Alpaca alpaca = new Alpaca("Test", healthyWeight, 5, WHITE);

        boolean weight = alpaca.isHealthyWeight();

        assertTrue(weight);

    }


    @ParameterizedTest
    @ValueSource(doubles = {54.90, 90.01})
    public void alpcasAreUnhealthy(double unhealthyWeight) {
        Alpaca alpaca = new Alpaca("Test", unhealthyWeight, 5, GREY );

        boolean weight = alpaca.isHealthyWeight();

        assertFalse(weight);
    }


    @Test
    public void alpcaHasZeroFleeceLength() {
        Alpaca alpaca = new Alpaca("Zero", 70.00, 0, WHITE);

        boolean fleeceLength = alpaca.isReadyToShear();

        assertFalse(fleeceLength);

    }


    @Test
    public void alpacaHasOneFleeceLength() {
        Alpaca alpaca = new Alpaca("One", 60.00, 1, GREY);

        boolean fleeceLength = alpaca.isReadyToShear();

        assertFalse(fleeceLength);
    }

    @Test
    public void alpacaHasFiveFleeceLength(){
        Alpaca alpaca = new Alpaca("Five", 80.00, 5, BROWN);

        boolean fleeceLength = alpaca.isReadyToShear();

        assertFalse(fleeceLength);
    }

    @Test
    public void alpacaHasTenFleeceLength(){
        Alpaca alpaca = new Alpaca("Ten", 50.00, 10, GREY);

        boolean fleeceLength = alpaca.isReadyToShear();

        assertTrue(fleeceLength);
    }

    @Test
    public void alpacaHasMinusOneFleeceLength() {
        assertThrows(IllegalArgumentException.class, () -> new Alpaca("Anna", 50.00, -1, BEIGE));
    }

    @Test
    public void alpacaHasFleeceLengthOfTenAndWeShear(){
        Alpaca alpaca = new Alpaca("Ten and Shear", 70.00, 10, BLACK);

         alpaca.shear();
         int fleeceLength = alpaca.getFleeceLength();

        assertEquals(0, fleeceLength);
    }

    @ParameterizedTest
    @ValueSource (ints = {0, 1, 2, 5, 10 })
    public void alpacaHasZeroFleeceLengthSoWeAddCm(int cmAdded) {
        Alpaca alpaca = new Alpaca("Zero and we add cm", 66.66, 0, WHITE);

        alpaca.growFleece(cmAdded);
        int fleeceLength = alpaca.getFleeceLength();

        assertEquals(cmAdded, fleeceLength);

    }

    @ParameterizedTest
    @CsvSource ({
            "5, 5, 10",
            "2, 1, 3",
            "3, 0, 3" })
    public void alpacaHasFleeceLengthSoWeAddCm(int pickedFleeceLength, int cmAdded, int expectedFleeceLength) {
        Alpaca alpaca = new Alpaca("Fleece Length and we add cm", 87.00, pickedFleeceLength, GREY);

        alpaca.growFleece(cmAdded);
        int fleeceLength = alpaca.getFleeceLength();

        assertEquals(expectedFleeceLength, fleeceLength);

   }

   @Test
   public void alpacaHasOverTenFleeceLength() {
        assertThrows(IllegalArgumentException.class, () -> new Alpaca("Anna", 78.00, 11, BLACK));
   }

   @Test
   public void alpacaHasThreeFleeceLengthSoWeAddedMinusFourCm() {
        Alpaca alpaca = new Alpaca("Three and we add Minus Four", 98.00, 3, BROWN);

        assertThrows(IllegalArgumentException.class, () -> alpaca.growFleece(-4));
        assertEquals(3, alpaca.getFleeceLength());
   }

   @Test
   public void alpacaAlmaHasADescription() {
        Alpaca alpaca = new Alpaca("Alma", 65.00, 8, GREY);

        String description = alpaca.describe();

        assertEquals("Alma weights 65.0 kg and has 8 cm of fleece.", description);

   }

   @Test
    void toStringLearning(){
        Alpaca alpaca = new Alpaca("Learning what toString() is", 79.00, 4, BROWN);

        String s = alpaca.toString();

        assertEquals("Learning what toString() is weights 79.0 kg and has 4 cm of fleece.", s);
   }

   @Test
    public void fleeceColorOfAlpacaCanNotBeNull() {
        assertThrows(FleeceColorNotFoundException.class, () -> new Alpaca("James", 88.88, 7, null));
   }


}
