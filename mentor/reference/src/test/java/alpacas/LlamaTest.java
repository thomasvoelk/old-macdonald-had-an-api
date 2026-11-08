package alpacas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LlamaTest {

    @Test
    public void nameCanNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new Llama(null, 200));
    }

    @Test
    public void weightCanNotBeFiveKgOrUnderFiveKg() {
      assertThrows(IllegalArgumentException.class, () -> new Llama("Charles", 5));
    }

    @Test
    public void weightCanNotBeTwoHundredAndSeventyOneKgOrOver() {
        assertThrows(IllegalArgumentException.class, () -> new Llama("Charles", 271));
    }

    @Test
    public void lookingForLlamasName() {
        Llama llama = new Llama("Orange Cassidy", 162);

        String getNameOfLlama = llama.getName();

        assertEquals("Orange Cassidy", getNameOfLlama);
    }

    @Test
    public void lookingForLlamaWeight() {
        Llama llama = new Llama("Mercedes Moné", 136);

        int getWeightOfLlama = llama.getWeight();

        assertEquals(136, getWeightOfLlama);

    }

    @ParameterizedTest
    @ValueSource (ints = {6, 50, 100, 129})
    public void llamasThatAreUnhealthyUnderweight(int weight) {
        Llama llama = new Llama("Orange Cassidy", weight);

        boolean llamasHealth = llama.isHealthyWeight();

        assertFalse(llamasHealth);
    }

    @ParameterizedTest
    @ValueSource (ints = {130, 200})
    public void llamaThatAreHealthy(int weight) {
        Llama llama = new Llama("Emi Sakura", weight);

        boolean llamaHealth = llama.isHealthyWeight();

        assertTrue(llamaHealth);
    }

    @ParameterizedTest
    @ValueSource (ints = {201, 250, 270})
    public void llamaThatAreUnhealthyOverweight(int weight) {
        Llama llama = new Llama("Samoa Joe", weight);

        boolean llamaHealth = llama.isHealthyWeight();

        assertFalse(llamaHealth);
    }

}
