/**
 * BicycleControllerTest.java
 *
 * Unit tests for the BicycleController class responsible
 * for Bicycle CRUD and search in the database records.
 */

package tests.database.controllers.bicycle;

import static org.junit.jupiter.api.Assertions.*;

import database.controllers.bicycle.BicycleController;
import database.controllers.component.FrameSetController;
import database.controllers.component.HandlebarController;
import database.controllers.component.PairOfWheelsController;
import exceptions.BicycleNotFoundException;
import exceptions.ComponentAlreadyExistsException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import models.bicycle.Bicycle;
import models.component.frameset.FrameSet;
import models.component.handlebar.Handlebar;
import models.component.handlebar.HandlebarType;
import models.component.pairofwheels.BrakeType;
import models.component.pairofwheels.PairOfWheels;
import models.component.pairofwheels.TyreType;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BicycleControllerTest {
    private static BicycleController bController;
    private static Bicycle correctBicycle;
    private static HandlebarController hController;
    private static Handlebar correctHandlebar;
    private static FrameSetController fController;
    private static FrameSet correctFrameSet;
    private static PairOfWheelsController powController;
    private static PairOfWheels correctPairOfWheels;

    @BeforeAll
    public static void setUp() {
        // Set up controllers.
        bController = new BicycleController();
        fController = new FrameSetController();
        hController = new HandlebarController();
        powController = new PairOfWheelsController();

        // Create sample objects of components to build a bicycle from.
        correctHandlebar = new Handlebar(
                "0000",
                "good sample",
                "test sample 1",
                BigDecimal.valueOf(100),
                100,
                HandlebarType.HIGH
        );
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

        // Create sample object of Bicycle class to test using the controller.
        correctBicycle = new Bicycle(
                "test bike 1",
                correctHandlebar,
                correctFrameSet,
                correctPairOfWheels,
                "0"
        );

        System.out.println("Performing tests for BicycleController...");
    }

    @Test
    @Order(1)
    public void testCreateComponents()
        throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Create database records for each component sample to be able
        // to furthermore create an instance of a bicycle.
        assertTrue(
                fController.createComponent(correctFrameSet)
        );
        assertTrue(
                hController.createComponent(correctHandlebar)
        );
        assertTrue(
                powController.createComponent(correctPairOfWheels)
        );
    }

    @Test
    @Order(2)
    public void test1CreateBicycle()
            throws SQLException, InputTooLongException, ComponentNotFoundException, ComponentAlreadyExistsException {
        // Make sure a correct sample is created.
        assertTrue(
                bController.createBicycle(correctBicycle)
        );
    }

    @Test
    @Order(3)
    public void test1CheckStock() throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Make sure that the stock of each component is decremented.
        int fStock = fController.findComponentById(
                correctFrameSet.getSerialNumber(), correctFrameSet.getBrandName()
        ).getStock();
        assertEquals(
                fStock, correctFrameSet.getStock()
        );
        int hStock = hController.findComponentById(
                correctHandlebar.getSerialNumber(), correctHandlebar.getBrandName()
        ).getStock();
        assertEquals(
                hStock, correctHandlebar.getStock()
        );
        int powStock = powController.findComponentById(
                correctPairOfWheels.getSerialNumber(), correctPairOfWheels.getBrandName()
        ).getStock();
        assertEquals(
                powStock, correctPairOfWheels.getStock()
        );
    }

    @Test
    @Order(4)
    public void test1DeleteBicycle()
            throws SQLException, BicycleNotFoundException, InputTooLongException, ComponentNotFoundException {
        // Should successfully delete bicycle from records.
        assertTrue(
                bController.deleteBicycle(correctBicycle.getSerialNumber())
        );
    }

    @Test
    @Order(5)
    public void test2DeleteBicycle()
            throws SQLException, BicycleNotFoundException, InputTooLongException, ComponentNotFoundException {
        // Should not succeed to delete a bike again, since it does not exist anymore.
        assertThrows(
                BicycleNotFoundException.class, () -> {
                    bController.deleteBicycle(correctBicycle.getSerialNumber());
                }
        );
    }

    @Test
    @Order(6)
    public void test2CheckStock() throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Make sure that the stock of each component is incremented.
        correctFrameSet.setStock(correctFrameSet.getStock() + 1);
        int fStock = fController.findComponentById(
                correctFrameSet.getSerialNumber(), correctFrameSet.getBrandName()
        ).getStock();
        assertEquals(
                fStock, correctFrameSet.getStock()
        );
        correctHandlebar.setStock(correctHandlebar.getStock() + 1);
        int hStock = hController.findComponentById(
                correctHandlebar.getSerialNumber(), correctHandlebar.getBrandName()
        ).getStock();
        assertEquals(
                hStock, correctHandlebar.getStock()
        );
        correctPairOfWheels.setStock(correctPairOfWheels.getStock() + 1);
        int powStock = powController.findComponentById(
                correctPairOfWheels.getSerialNumber(), correctPairOfWheels.getBrandName()
        ).getStock();
        assertEquals(
                powStock, correctPairOfWheels.getStock()
        );
    }

    @Test
    @Order(7)
    public void testDeleteAllComponents() throws SQLException, ComponentNotFoundException {
        // Make sure that all previously created component records are deleted.
        assertTrue(
                powController.deleteComponent(correctPairOfWheels, "PairsOfWheels")
        );
        assertTrue(
                hController.deleteComponent(correctHandlebar, "Handlebars")
        );
        assertTrue(
                fController.deleteComponent(correctFrameSet, "FrameSets")
        );
    }

    @Test
    @Order(8)
    public void testFindComponentsById() throws SQLException {
        // Make sure that the deleted components cannot be found anymore.
        assertFalse(
                powController.componentExists(correctPairOfWheels, "PairsOfWheels")
        );
        assertFalse(
                hController.componentExists(correctHandlebar, "Handlebars")
        );
        assertFalse(
                fController.componentExists(correctFrameSet, "FrameSets")
        );
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("All tests done.");
    }
}
