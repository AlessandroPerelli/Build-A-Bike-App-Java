/**
 * IdGeneratorTest.java
 *
 * Unit tests to verify the ID generator's correctness.
 */

package tests.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import services.IdGenerator;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdGeneratorTest {
    private static final int[] ID_LENGTHS = {5, 10, 15, 20, 25, 30};

    @BeforeAll
    public void setup() {
        System.out.println("Testing IdGeneratorClass...");
    }

    @Test
    public void testGenerateIds() {
        // Test if the returned generated IDs match the required length.
        for (int currIdLength : ID_LENGTHS) {
            Assertions.assertEquals(
                    IdGenerator.generateId(currIdLength).length(),
                    currIdLength
            );
        }
    }

    @AfterAll
    public void tearDown() {
        System.out.println("All tests done.");
    }
}
