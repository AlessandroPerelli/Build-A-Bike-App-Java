/**
 * EncryptionHandler.java
 *
 * Service class used to encrypt data using SHA-256.
 *
 * The algorithm is an example of one-way encryption system (i.e. cannot be decrypted).
 */

package services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionHandler {
    private static MessageDigest mdEncryptor;
    private static final String MD_ALGORITHM = "SHA-256";
    private final static int DATA_MAX_LENGTH = 20;
    private final static char SALT = '$';

    /**
     * First step of performing encryption - add salt to data.
     *
     * @param rawData - string to be "salted".
     * @return "salted" data that has maximum allowed length.
     */
    private static String addSalt(String rawData) {
        StringBuilder salted = new StringBuilder(rawData);

        while(salted.length() < DATA_MAX_LENGTH){
            salted.append(SALT);
        }

        return salted.toString();
    }

    /**
     * Second step of performing encryption - use cryptographic hash SHA-256.
     *
     * @param saltedData - data to be digested using the selected algorithm.
     * @return data digested using SHA-256 algorithm (in bytes).
     * @throws NoSuchAlgorithmException
     */
    private static byte[] digestData(String saltedData) throws NoSuchAlgorithmException {
        mdEncryptor = MessageDigest.getInstance(MD_ALGORITHM);
        byte[] dataAsBytes = saltedData.getBytes(StandardCharsets.UTF_8);
        return mdEncryptor.digest(dataAsBytes);
    }

    /**
     * Third step of encryption - convert hashed data in bytes to String.
     *
     * @param hashData - hash in bytes.
     * @return encrypted data expressed as String.
     */
    private static String convertHashToStr(byte[] hashData) {
        StringBuilder hexStr = new StringBuilder();

        for (byte hByte : hashData) {
            String hex = Integer.toHexString(0xff & hByte);

            if (hex.length() == 1) {
                hexStr.append(SALT);
            }

            hexStr.append(hex);
        }

        return hexStr.toString();
    }

    /**
     * Perform full data encryption, following all the subsequent steps.
     *
     * @param rawData - raw, unencrypted data.
     * @return fully encrypted String data with salt and digested using SHA-256.
     */
    public static String encryptData(String rawData) throws NoSuchAlgorithmException {
        String saltedData = addSalt(rawData);
        byte[] hashData = digestData(saltedData);
        String encryptedData = convertHashToStr(hashData);
        return encryptedData;
    }
}
