/**
 * IdGenerator.java
 *
 * Service class used to generate IDs of specific length,
 * e.g. for bicycle serial number, or order number, etc.
 */

package services;

import java.util.Random;

public class IdGenerator {
    /**
     * Static method to generate ID of a specific length.
     *
     * @param idLength - desired length.
     * @return generated ID.
     */
    public static String generateId(int idLength) {
        char[] digits = "0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder idStrBuilder = new StringBuilder((100000 + rnd.nextInt(900000)));

        for (int i = 0; i < idLength; i++) {
            idStrBuilder.append(
                    digits[rnd.nextInt(digits.length)]
            );
        }

        String id = idStrBuilder.toString();
        return id;
    }
}
