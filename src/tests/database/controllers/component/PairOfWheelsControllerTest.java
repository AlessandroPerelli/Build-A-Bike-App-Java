/**
 * PairOfWheelsControllerTest.java
 *
 * Unit tests for the PairOfWheelsController class responsible
 * for PairOfWheel CRUD and search in the database records.
 */

package tests.database.controllers.component;

import database.controllers.component.PairOfWheelsController;

import exceptions.ComponentAlreadyExistsException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;

import models.component.pairofwheels.PairOfWheels;
import models.component.pairofwheels.BrakeType;
import models.component.pairofwheels.TyreType;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PairOfWheelsControllerTest {
    private static PairOfWheelsController powController;
    private static PairOfWheels correctPairOfWheels;
    private static PairOfWheels wrongPairOfWheels;

    @BeforeAll
    public static void setUp() {
        powController = new PairOfWheelsController();

        // Create sample objects of PairOfWheels class to test it using its controller.
        correctPairOfWheels = new PairOfWheels(
                "00000",
                "good sample",
                "test sample 1",
                BigDecimal.valueOf(50),
                200,
                BigDecimal.valueOf(55),
                TyreType.HYBRID,
                BrakeType.DISK
        );
        wrongPairOfWheels = new PairOfWheels(
                "0000000",
                "wrong sample",
                "deliberatelytoolongnamenottobevalidatedwhilecreating",
                BigDecimal.valueOf(50),
                200,
                BigDecimal.valueOf(55),
                TyreType.HYBRID,
                BrakeType.DISK
        );

        System.out.println("Performing tests for PairOfWheelsController...");
    }

    @Test
    @Order(1)
    public void test1CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Create the correct sample record in the database.
        assertTrue(
                powController.createComponent(correctPairOfWheels)
        );
    }

    @Test
    @Order(2)
    public void test2CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Repeating the creation process for the good sample should not
        // validate and throw an exception, since it already exists.
        assertThrows(
                ComponentAlreadyExistsException.class, () -> {
                    powController.createComponent(correctPairOfWheels);
                }
        );
    }

    @Test
    @Order(3)
    public void test3CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // The wrong one should throw an exception, since it contains invalid input.
        assertThrows(
                InputTooLongException.class, () -> {
                    powController.createComponent(wrongPairOfWheels);
                }
        );
    }

    @Test
    @Order(4)
    public void test1FindComponentById()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should be able to find the correct sample.
        PairOfWheels foundPairOfWheels = powController.findComponentById(
                correctPairOfWheels.getSerialNumber(),
                correctPairOfWheels.getBrandName()
        );
        assertEquals(
                correctPairOfWheels.getSerialNumber(),
                foundPairOfWheels.getSerialNumber()
        );
        assertEquals(
                correctPairOfWheels.getBrandName(),
                foundPairOfWheels.getBrandName()
        );
        assertEquals(
                correctPairOfWheels.getComponentName(),
                foundPairOfWheels.getComponentName()
        );
    }

    @Test
    @Order(5)
    public void test2FindComponentById()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should not be able to find the wrong sample and throw an exception.
        assertThrows(
                InputTooLongException.class, () -> {
                    powController.findComponentById(
                            wrongPairOfWheels.getSerialNumber(),
                            wrongPairOfWheels.getBrandName()
                    );
                }
        );
    }

    @Test
    @Order(6)
    public void test1UpdateComponent()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should validate the performed update.
        correctPairOfWheels.setBrakeType(BrakeType.RIM);
        assertTrue(
                powController.updateComponent(correctPairOfWheels)
        );
    }

    @Test
    @Order(7)
    public void test2UpdateComponent()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        correctPairOfWheels.setComponentName(
                "verylongnameverylongnameverylongnameverylongnameverylongname"
        );

        // Should not validate since the one String is longer than permitted.
        assertThrows(
                InputTooLongException.class, () -> {
                    powController.updateComponent(correctPairOfWheels);
                }
        );
    }

    @Test
    @Order(8)
    public void test3UpdateComponent()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should not validate, since the instance does not exist in records.
        assertThrows(
                ComponentNotFoundException.class, () -> {
                    powController.updateComponent(wrongPairOfWheels);
                }
        );
    }

    @Test
    @Order(9)
    public void test1ComponentExists() throws SQLException {
        // Should find the correct instance.
        assertTrue(
                powController.componentExists(correctPairOfWheels, "PairsOfWheels")
        );
    }

    @Test
    @Order(10)
    public void test2ComponentExists() throws SQLException {
        // Should not find the correct instance in the wrong table.
        assertFalse(
                powController.componentExists(correctPairOfWheels, "FrameSets")
        );
    }

    @Test
    @Order(11)
    public void test1DeleteComponent() throws SQLException, ComponentNotFoundException {
        // Should successfully delete the correct instance.
        assertTrue(
                powController.deleteComponent(correctPairOfWheels, "PairsOfWheels")
        );
    }

    @Test
    @Order(12)
    public void test2DeleteComponent() throws SQLException, ComponentNotFoundException {
        // Should throw an exception, since the instance never existed in records.
        assertThrows(
                ComponentNotFoundException.class, () -> {
                    powController.deleteComponent(wrongPairOfWheels, "PairsOfWheels");
                }
        );
    }

    @Test
    @Order(13)
    public void test3ComponentExists() throws SQLException {
        // Should not find the correct instance after deletion.
        assertFalse(
                powController.componentExists(correctPairOfWheels, "PairsOfWheels")
        );
    }

    @Test
    @Order(14)
    public void test4ComponentExists() throws SQLException {
        // Should not find the wrong instance, since it never existed.
        assertFalse(
                powController.componentExists(wrongPairOfWheels, "PairsOfWheels")
        );
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("All tests done.");
    }
}
