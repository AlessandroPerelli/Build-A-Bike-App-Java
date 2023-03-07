/**
 * AddressControllerTest.java
 *
 * Unit tests for the AddressController class.
 */

package tests.database.controllers.user.customer;

import database.controllers.user.customer.AddressController;
import exceptions.InputTooLongException;
import exceptions.InvalidAddressException;
import models.user.customer.Address;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AddressControllerTest {
    private static AddressController aController;
    private static Address correctAddress;
    private static Address wrongAddress;

    @BeforeAll
    public static void setUp() {
        aController = new AddressController();

        // Create sample objects of the address.
        correctAddress = new Address(
                777777,
                "XXXXX",
                "XXX0XXX",
                "Nonexistent City"
        );
        wrongAddress = new Address(
                777777,
                "XXXXX",
                "DEFINITELETYTOOLONGPOSTCODE",
                "Nonexistent City"
        );
    }

    @Test
    @Order(1)
    public void test1CreateAddress()
            throws SQLException, InvalidAddressException, InputTooLongException {
        // Make sure a correct instance is created.
        assertTrue(
                aController.createAddress(correctAddress)
        );
    }

    @Test
    @Order(2)
    public void test2CreateAddress() {
        // Make sure the same correct instance cannot be created twice.
        assertThrows(
                InvalidAddressException.class, () -> {
                    aController.createAddress(correctAddress);
                }
        );
    }

    @Test
    @Order(3)
    public void test3CreateAddress() {
        // Make sure the incorrect instance cannot be created.
        assertThrows(
                InputTooLongException.class, () -> {
                    aController.createAddress(wrongAddress);
                }
        );
    }

    @Test
    @Order(4)
    public void testDeleteAddress()
            throws SQLException, InvalidAddressException, InputTooLongException {
        // Make sure a correct instance can be deleted from the database.
        assertTrue(
                aController.deleteAddress(correctAddress)
        );
    }

    @Test
    @Order(5)
    public void test1FindAddressById() throws SQLException {
        // Make sure the sample is not found anymore.
        assertNull(
                aController.findAddressById(correctAddress)
        );
    }

    @Test
    @Order(6)
    public void test2FindAddressById() throws SQLException {
        // Make sure the wrong sample cannot be found, since never created.
        assertNull(
                aController.findAddressById(wrongAddress)
        );
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("All tests done.");
    }
}
