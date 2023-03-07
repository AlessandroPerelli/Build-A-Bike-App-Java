/**
 * HandlebarControllerTest.java
 *
 * Unit tests for the HandlebarController class responsible
 * for Handlebar CRUD and search in the database records.
 */

package tests.database.controllers.component;

import database.controllers.component.HandlebarController;

import exceptions.ComponentAlreadyExistsException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;

import models.component.handlebar.Handlebar;
import models.component.handlebar.HandlebarType;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HandlebarControllerTest {
    private static HandlebarController hController;
    private static Handlebar correctHandlebar;
    private static Handlebar wrongHandlebar;

    @BeforeAll
    public static void setUp() {
        hController = new HandlebarController();

        // Create sample objects of Handlebar class to test using the controller.
        correctHandlebar = new Handlebar(
                "0000",
                "good sample",
                "test sample 1",
                BigDecimal.valueOf(100),
                100,
                HandlebarType.HIGH
        );
        wrongHandlebar = new Handlebar(
                "00000",
                "deliberatelytoolongnamenottobevalidatedwhilecreating",
                "test sample 2",
                BigDecimal.valueOf(100),
                50,
                HandlebarType.DROPPED
        );

        System.out.println("Performing tests for HandlebarController...");
    }

    @Test
    @Order(1)
    public void test1CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Create the correct sample record in the database.
        assertTrue(
                hController.createComponent(correctHandlebar)
        );
    }

    @Test
    @Order(2)
    public void test2CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Repeating the creation process for the good sample should not
        // validate and throw an exception.
        assertThrows(
                ComponentAlreadyExistsException.class, () -> {
                    hController.createComponent(correctHandlebar);
                }
        );
    }

    @Test
    @Order(3)
    public void test3CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // The wrong one should throw an exception for invalid input.
        assertThrows(
                InputTooLongException.class, () -> {
                    hController.createComponent(wrongHandlebar);
                }
        );
    }

    @Test
    @Order(4)
    public void test1FindComponentById()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should be able to find the correct sample.
        Handlebar foundHandlebar = hController.findComponentById(
                correctHandlebar.getSerialNumber(),
                correctHandlebar.getBrandName()
        );
        assertEquals(
                correctHandlebar.getSerialNumber(),
                foundHandlebar.getSerialNumber()
        );
        assertEquals(
                correctHandlebar.getBrandName(),
                foundHandlebar.getBrandName()
        );
    }

    @Test
    @Order(5)
    public void test2FindComponentById()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should not be able to find the wrong sample and throw an exception.
        assertThrows(
                ComponentNotFoundException.class, () -> {
                    hController.findComponentById(
                            wrongHandlebar.getSerialNumber(),
                            wrongHandlebar.getBrandName()
                    );
                }
        );
    }

    @Test
    @Order(6)
    public void test1UpdateComponent()
        throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should validate the performed update.
        correctHandlebar.setType(HandlebarType.DROPPED);
        assertTrue(
                hController.updateComponent(correctHandlebar)
        );
    }

    @Test
    @Order(7)
    public void test2UpdateComponent()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        correctHandlebar.setComponentName(
                "verylongnameverylongnameverylongnameverylongnameverylongname"
        );

        // Should not validate since the one String is longer than permitted.
        assertThrows(
                InputTooLongException.class, () -> {
                    hController.updateComponent(correctHandlebar);
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
                    hController.updateComponent(wrongHandlebar);
                }
        );
    }

    @Test
    @Order(9)
    public void test1ComponentExists() throws SQLException {
        // Should find the correct instance.
        assertTrue(
                hController.componentExists(correctHandlebar, "Handlebars")
        );
    }

    @Test
    @Order(10)
    public void test2ComponentExists() throws SQLException {
        // Should not find the correct instance in the wrong table.
        assertFalse(
                hController.componentExists(correctHandlebar, "PairsOfWheels")
        );
    }

    @Test
    @Order(11)
    public void test1DeleteComponent() throws SQLException, ComponentNotFoundException {
        // Should successfully delete the correct instance.
        assertTrue(
                hController.deleteComponent(correctHandlebar, "Handlebars")
        );
    }

    @Test
    @Order(12)
    public void test2DeleteComponent() throws SQLException, ComponentNotFoundException {
        // Should throw an exception, since the instance never existed in records.
        assertThrows(
                ComponentNotFoundException.class, () -> {
                    hController.deleteComponent(wrongHandlebar, "Handlebars");
                }
        );
    }

    @Test
    @Order(13)
    public void test3ComponentExists() throws SQLException {
        // Should not find the correct instance after deletion.
        assertFalse(
                hController.componentExists(correctHandlebar, "Handlebars")
        );
    }

    @Test
    @Order(14)
    public void test4ComponentExists() throws SQLException {
        // Should not find the wrong instance, since it never existed.
        assertFalse(
                hController.componentExists(wrongHandlebar, "Handlebars")
        );
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("All tests done.");
    }
}
