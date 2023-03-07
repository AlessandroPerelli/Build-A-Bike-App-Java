/**
 * FrameSetControllerTest.java
 *
 * Unit tests for the FrameSetController class responsible
 * for FrameSet CRUD and search in the database records.
 */

package tests.database.controllers.component;

import database.controllers.component.FrameSetController;

import exceptions.ComponentAlreadyExistsException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;

import models.component.frameset.FrameSet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FrameSetControllerTest {
    private static FrameSetController fsController;
    private static FrameSet correctFrameSet;
    private static FrameSet wrongFrameSet;

    @BeforeAll
    public static void setUp() {
        fsController = new FrameSetController();

        // Create sample objects of FrameSet class to test it using its controller.
        correctFrameSet = new FrameSet(
                "00000000",
                "good sample",
                "test sample 1",
                BigDecimal.valueOf(300),
                25,
                "nice gears",
                "nice forks",
                BigDecimal.valueOf(80.50),
                true
        );
        wrongFrameSet = new FrameSet(
                "0000",
                "deliberatelytoolongnamenottobevalidatedwhilecreating",
                "test sample 2",
                BigDecimal.valueOf(400.99),
                25,
                "bad gears",
                "bad forks",
                BigDecimal.valueOf(55),
                true

        );

        System.out.println("Performing tests for FrameSetController...");
    }

    @Test
    @Order(1)
    public void test1CreateComponent()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Create the correct sample record in the database.
        assertTrue(
                fsController.createComponent(correctFrameSet)
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
                    fsController.createComponent(correctFrameSet);
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
                    fsController.createComponent(wrongFrameSet);
                }
        );
    }

    @Test
    @Order(4)
    public void test1FindComponentById()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should be able to find the correct sample.
        FrameSet foundFrameSet = fsController.findComponentById(
                correctFrameSet.getSerialNumber(),
                correctFrameSet.getBrandName()
        );
        assertEquals(
                correctFrameSet.getSerialNumber(),
                foundFrameSet.getSerialNumber()
        );
        assertEquals(
                correctFrameSet.getBrandName(),
                foundFrameSet.getBrandName()
        );
        assertEquals(
                foundFrameSet.getComponentName(),
                correctFrameSet.getComponentName()
        );
    }

    @Test
    @Order(5)
    public void test2FindComponentById()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should not be able to find the wrong sample and throw an exception.
        assertThrows(
                ComponentNotFoundException.class, () -> {
                    fsController.findComponentById(
                            wrongFrameSet.getSerialNumber(),
                            wrongFrameSet.getBrandName()
                    );
                }
        );
    }

    @Test
    @Order(6)
    public void test1UpdateComponent()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Should validate the performed update.
        correctFrameSet.setForkSetName("new name");
        assertTrue(
                fsController.updateComponent(correctFrameSet)
        );
    }


    @Test
    @Order(7)
    public void test2UpdateComponent()
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        correctFrameSet.setForkSetName(
                "verylongnameverylongnameverylongnameverylongnameverylongname"
        );

        // Should not validate since the one String is longer than permitted.
        assertThrows(
                InputTooLongException.class, () -> {
                    fsController.updateComponent(correctFrameSet);
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
                    fsController.updateComponent(wrongFrameSet);
                }
        );
    }

    @Test
    @Order(9)
    public void test1ComponentExists() throws SQLException {
        // Should find the correct instance.
        assertTrue(
                fsController.componentExists(correctFrameSet, "FrameSets")
        );
    }

    @Test
    @Order(10)
    public void test2ComponentExists() throws SQLException {
        // Should not find the correct instance in the wrong table.
        assertFalse(
                fsController.componentExists(correctFrameSet, "PairsOfWheels")
        );
    }

    @Test
    @Order(11)
    public void test1DeleteComponent() throws SQLException, ComponentNotFoundException {
        // Should successfully delete the correct instance.
        assertTrue(
                fsController.deleteComponent(correctFrameSet, "FrameSets")
        );
    }

    @Test
    @Order(12)
    public void test2DeleteComponent() throws SQLException, ComponentNotFoundException {
        // Should throw an exception, since the instance never existed in records.
        assertThrows(
                ComponentNotFoundException.class, () -> {
                    fsController.deleteComponent(wrongFrameSet, "FrameSets");
                }
        );
    }

    @Test
    @Order(13)
    public void test3ComponentExists() throws SQLException {
        // Should not find the correct instance after deletion.
        assertFalse(
                fsController.componentExists(correctFrameSet, "FrameSets")
        );
    }

    @Test
    @Order(14)
    public void test4ComponentExists() throws SQLException {
        // Should not find the wrong instance, since it never existed.
        assertFalse(
                fsController.componentExists(wrongFrameSet, "FrameSets")
        );
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("All tests done.");
    }
}
