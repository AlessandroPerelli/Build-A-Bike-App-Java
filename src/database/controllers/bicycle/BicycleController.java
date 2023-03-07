/**
 * BicycleController.java
 *
 * Bicycle controller responsible for create, delete, find, stock update.
 */

package database.controllers.bicycle;

import database.controllers.DatabaseController;
import database.controllers.component.FrameSetController;
import database.controllers.component.HandlebarController;
import database.controllers.component.PairOfWheelsController;
import exceptions.BicycleNotFoundException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import models.bicycle.Bicycle;
import models.component.frameset.FrameSet;
import models.component.handlebar.Handlebar;
import models.component.pairofwheels.PairOfWheels;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BicycleController extends DatabaseController {
    private static HandlebarController handlebarController = new HandlebarController();
    private static FrameSetController frameSetController = new FrameSetController();
    private static PairOfWheelsController pairOfWheelsController = new PairOfWheelsController();

    /**
     * Check if a bicycle instance with the serial number (ID) exists in DB records.
     *
     * @param serialNumber - denoted ID of a bicycle.
     * @return true/false (respectively meaning: exists/not exists).
     * @throws SQLException
     */
    public boolean bicycleExists(String serialNumber) throws SQLException {
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT EXISTS(\n\t" +
                              "SELECT * FROM Bicycles WHERE serialNumber = ?\n)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, serialNumber);
            result = sqlStatement.executeQuery();
            result.next();
            int exist = result.getInt(1);
            return (exist == 1);
        }
        finally {
            if (result != null) {
                result.close();
            }
            closeConnection();
        }
    }

    /**
     * Search for a bicycle by ID (i.e. serial number).
     *
     * @param serialNumber - serial number of the searched bicycle.
     * @return instance of the bicycle that match the passed serial number.
     * @throws SQLException
     * @throws InputTooLongException
     * @throws BicycleNotFoundException
     * @throws ComponentNotFoundException
     */
    public Bicycle findBicycleById (String serialNumber)
            throws SQLException, InputTooLongException, BicycleNotFoundException, ComponentNotFoundException {
        // Use component's controller to build an instance of the searched bicycle.
        HandlebarController handlebarController = new HandlebarController();
        FrameSetController frameSetController = new FrameSetController();
        PairOfWheelsController pairOfWheelsController = new PairOfWheelsController();

        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM Bicycles WHERE serialNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, serialNumber);
            result = sqlStatement.executeQuery();

            if (result.next()) {
                String customName = result.getString("customName");
                Handlebar bicycleHandlebar = handlebarController.findComponentById(
                        result.getString("handlebarSerialNumber"),
                        result.getString("handlebarBrandName")
                );
                FrameSet bicycleFrameSet = frameSetController.findComponentById(
                        result.getString("frameSetSerialNumber"),
                        result.getString("frameSetBrandName")
                );
                PairOfWheels bicyclePairOfWheels = pairOfWheelsController.findComponentById(
                        result.getString("pairOfWheelsSerialNumber"),
                        result.getString("pairOfWheelsBrandName")
                );

                Bicycle bicycle = new Bicycle(
                        customName,
                        bicycleHandlebar,
                        bicycleFrameSet,
                        bicyclePairOfWheels,
                        serialNumber
                );
                return bicycle;
            }
            else {
                throw new BicycleNotFoundException(serialNumber);
            }
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }

    /**
     * Delete an instance of a bicycle. The controller's method is invoked
     * e.g. by the OrderController when an order is deleted.
     *
     * @param serialNumber - serial number of a bicycle to be deleted.
     * @return true if deleted with success, false otherwise.
     * @throws SQLException
     * @throws BicycleNotFoundException
     * @throws InputTooLongException
     * @throws ComponentNotFoundException
     */
    public boolean deleteBicycle(String serialNumber)
            throws SQLException, BicycleNotFoundException, InputTooLongException, ComponentNotFoundException {
        // Check if the bicycle to be deleted exists at all in the records.
        if (!bicycleExists(serialNumber)) {
            throw new BicycleNotFoundException(serialNumber);
        }

        Bicycle deletedBicycle = findBicycleById(serialNumber);

        try {
            openConnection();
            connection.setAutoCommit(false);
            // Perform bicycle deletion.
            String sqlQuery = "DELETE FROM Bicycles WHERE serialNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, serialNumber);
            int result = sqlStatement.executeUpdate();

            if (result == 1) {
                connection.commit();
                // Increment each controller's stock by 1.
                changeStock(deletedBicycle, 1);
                return true;
            }
            else {
                connection.rollback();
                return false;
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Create a new bicycle record in the database.
     *
     * @param bicycle - a new bicycle instance to be created.
     * @return true if created with success, false otherwise.
     * @throws SQLException
     * @throws InputTooLongException
     * @throws ComponentNotFoundException
     */
    public boolean createBicycle(Bicycle bicycle)
            throws SQLException, InputTooLongException, ComponentNotFoundException {
        try {
            openConnection();
            connection.setAutoCommit(false);
            String sqlQuery = "INSERT INTO Bicycles" +
                              "(serialNumber, customName, brandName, " +
                              "handlebarSerialNumber, frameSetSerialNumber, pairOfWheelsSerialNumber, " +
                              "handlebarBrandName, frameSetBrandName, pairOfWheelsBrandName) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, bicycle.getSerialNumber());
            sqlStatement.setString(2, bicycle.getCustomName());
            sqlStatement.setString(3, bicycle.getBrandName());
            sqlStatement.setString(4, bicycle.getHandlebar().getSerialNumber());
            sqlStatement.setString(5, bicycle.getFrameSet().getSerialNumber());
            sqlStatement.setString(6, bicycle.getPairOfWheels().getSerialNumber());
            sqlStatement.setString(7, bicycle.getHandlebar().getBrandName());
            sqlStatement.setString(8, bicycle.getFrameSet().getBrandName());
            sqlStatement.setString(9, bicycle.getPairOfWheels().getBrandName());
            int result = sqlStatement.executeUpdate();

            if (result == 1) {
                connection.commit();
                // Decrement stock of each component by 1.
                changeStock(bicycle, -1);
                return true;
            }
            else {
                connection.rollback();
                return false;
            }
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Change bicycle's components stock. Increment when a bicycle is deleted
     * and decrement when one is created.
     *
     * @param bicycle - referenced bicycle instance.
     * @param stockChangeVal - amount to change the stock by.
     * @throws ComponentNotFoundException
     * @throws SQLException
     * @throws InputTooLongException
     */
    public void changeStock(Bicycle bicycle, int stockChangeVal)
            throws ComponentNotFoundException, SQLException, InputTooLongException {
        bicycle.getHandlebar().setStock(
                bicycle.getHandlebar().getStock() + stockChangeVal
        );
        bicycle.getFrameSet().setStock(
                bicycle.getFrameSet().getStock() + stockChangeVal
        );
        bicycle.getPairOfWheels().setStock(
                bicycle.getPairOfWheels().getStock() + stockChangeVal
        );
        handlebarController.updateComponent(bicycle.getHandlebar());
        frameSetController.updateComponent(bicycle.getFrameSet());
        pairOfWheelsController.updateComponent(bicycle.getPairOfWheels());
    }
}
