/**
 * OrderControllerTest.java
 *
 * Unit tests for the OrderController class responsible
 * for Order CRUD and search, etc.
 */

package tests.database.controllers.order;

import static org.junit.jupiter.api.Assertions.*;

import database.controllers.bicycle.BicycleController;
import database.controllers.component.FrameSetController;
import database.controllers.component.HandlebarController;
import database.controllers.component.PairOfWheelsController;
import database.controllers.order.OrderController;
import exceptions.*;
import models.bicycle.Bicycle;
import models.component.frameset.FrameSet;
import models.component.handlebar.Handlebar;
import models.component.handlebar.HandlebarType;
import models.component.pairofwheels.BrakeType;
import models.component.pairofwheels.PairOfWheels;
import models.component.pairofwheels.TyreType;
import models.order.OrderStatus;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {
    private static BicycleController bController;
    private static Bicycle correctBicycle;
    private static HandlebarController hController;
    private static Handlebar correctHandlebar;
    private static FrameSetController fController;
    private static FrameSet correctFrameSet;
    private static PairOfWheelsController powController;
    private static PairOfWheels correctPairOfWheels;
    private static OrderController oController;
    private static models.order.Order correctOrder;
    private static String sampleCustomerId;
    private static String sampleStaffUsername;

    @BeforeAll
    public static void setUp() {
        // Set up controllers.
        oController = new OrderController();
        bController = new BicycleController();
        fController = new FrameSetController();
        hController = new HandlebarController();
        powController = new PairOfWheelsController();

        // Create sample credentials of users, that do NOT have to be
        // additionally stored in the database for the purpose of testing.
        sampleStaffUsername = "sampleTestStaffUsername";
        sampleCustomerId = "00000000000";

        // Create sample objects that will be stored in the database
        // for the purpose of executing the tests.
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

        // Create sample Order object.
        correctOrder = new models.order.Order();
        correctOrder.prepareOrder(null, correctBicycle);

        System.out.println("Performing tests for BicycleController...");
    }

    @Test
    @Order(1)
    public void testCreateComponents()
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Create database records for each component sample to be able
        // to furthermore create an instance of a bicycle and order.
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
    public void testCreateBicycle()
            throws SQLException, InputTooLongException, ComponentNotFoundException, ComponentAlreadyExistsException {
        // Make sure a correct bicycle sample is created.
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
    public void testCreateOrder() throws SQLException, InvalidOrderException {
        // Make sure a correct order sample is created.
        assertTrue(
                oController.createOrder(correctOrder)
        );
    }

    @Test
    @Order(5)
    public void testFindOrderById()
            throws SQLException, InvalidOrderException, OrderNotFoundException {
        // Make sure a correct order sample is found.
        assertEquals(
                oController.findOrderById(correctOrder.getOrderNumber()).getOrderNumber(), correctOrder.getOrderNumber()
        );
    }

    @Test
    @Order(6)
    public void testUpdateOrderStatus() throws SQLException {
        // Attempt to update an order status.
        correctOrder.setStatus(OrderStatus.CONFIRMED);
        assertTrue(
                oController.updateOrderStatus(correctOrder)
        );
    }

    @Test
    @Order(7)
    public void testDeleteOrderAndBicycle() throws SQLException, OrderNotFoundException, InputTooLongException,
            ComponentNotFoundException, BicycleNotFoundException {
        assertTrue(
                oController.deleteOrder(correctOrder)
        );
    }

    @Test
    @Order(8)
    public void testFindOrderByIdDeleted()
            throws SQLException, InvalidOrderException, OrderNotFoundException {
        // Make sure a deleted sample is not found anymore.
        assertThrows(
                OrderNotFoundException.class, () -> {
                    oController.findOrderById(correctOrder.getOrderNumber());
                }
        );
    }

    @Test
    @Order(9)
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
    @Order(10)
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
