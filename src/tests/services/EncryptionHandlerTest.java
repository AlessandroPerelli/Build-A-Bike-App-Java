/**
 * EncryptionHandler.java
 *
 * Sample unit tests for verifying the encryption class.
 */

package tests.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import services.EncryptionHandler;

import java.security.NoSuchAlgorithmException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EncryptionHandlerTest {
    // Set up some sample passwords with equivalent encrypted versions.
    String rawData1 = "Pa$sw0rD";
    String encryptedData1 = "a3cbc1a9b3408ed1e8ea58cf773d131cc58e$9e8a272883fa36349c8a1$b8321";
    String rawData2 = "0123456";
    String encryptedData2 = "2372bb361f51982b19$33f9c3b59a1b784afab2285$3$3cf8bf3f97685c4e98d";
    String rawData3 = "toughToCrackPa$$";
    String encryptedData3 = "9d4dfa39$a3b6140f46247f613dfe59e95$334c8316f9c10adad1e94c03c4070";
    String rawData4 = "ot#erExPassword";
    String encryptedData4 = "34df1f1ae71d6169d2$ae6c2e3536ac7754e71399fc637a29397b8209bd9ec8f";
    String rawData5 = "weak!";
    String encryptedData5 = "1482917219$89b141e77189fd5ba$eabc9fa5e6d$8fee14c5c7785d3c1ed63$7";

    @BeforeAll
    public void setup() {
        System.out.println("Testing EncryptionHandler class...");
    }

    @Test
    public void test1EncryptData() throws NoSuchAlgorithmException {
        Assertions.assertEquals(
                EncryptionHandler.encryptData(rawData1), encryptedData1
        );
    }

    @Test
    public void test2EncryptData() throws NoSuchAlgorithmException {
        Assertions.assertEquals(
                EncryptionHandler.encryptData(rawData2), encryptedData2
        );
    }

    @Test
    public void test3EncryptData() throws NoSuchAlgorithmException {
        Assertions.assertEquals(
                EncryptionHandler.encryptData(rawData3), encryptedData3
        );
    }

    @Test
    public void test4EncryptData() throws NoSuchAlgorithmException {
        Assertions.assertEquals(
                EncryptionHandler.encryptData(rawData4), encryptedData4
        );
    }

    @Test
    public void test5EncryptData() throws NoSuchAlgorithmException {
        Assertions.assertEquals(
                EncryptionHandler.encryptData(rawData5), encryptedData5
        );
    }

    @AfterAll
    public void tearDown() {
        System.out.println("All tests done.");
    }
}
