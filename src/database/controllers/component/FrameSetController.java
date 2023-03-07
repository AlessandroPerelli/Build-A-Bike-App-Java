/**
 * FrameSetController.java
 *
 * FrameSet controller used for CRUD, finding by ID, filtering.
 * The controller's functionalities are only available to Staff.
 */

package database.controllers.component;

import exceptions.ComponentAlreadyExistsException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import exceptions.NoComponentForFilterException;
import models.component.frameset.FrameSet;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FrameSetController extends ComponentController<FrameSet> {
    /**
     * Search for a unique frame-set instance in the database records by its composite ID.
     *
     * @param serialNumber - serial number of a frame-set.
     * @param brandName - brand name of a frame-set.
     * @return unique instance of a frame-set.
     * @throws SQLException
     * @throws ComponentNotFoundException
     * @throws InputTooLongException
     */
    @Override
    public FrameSet findComponentById(String serialNumber, String brandName)
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        ResultSet result = null;

        // Check the input validity before processing any queries.
        if (serialNumber.length() > MAX_INPUT_LENGTH || brandName.length() > MAX_INPUT_LENGTH) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM FrameSets " +
                              "WHERE serialNumber = ? AND brandName = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, serialNumber);
            sqlStatement.setString(2, brandName);
            result = sqlStatement.executeQuery();

            if (result.next()) {
                String componentName =  result.getString("frameSetName");
                String forkSetName =  result.getString("forkSetName");
                String gearSetName = result.getString("gearSetName");
                boolean hasShocks = result.getBoolean("hasShocks");
                BigDecimal size = result.getBigDecimal("size");
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");

                FrameSet frameSet = new FrameSet(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        forkSetName,
                        gearSetName,
                        size,
                        hasShocks
                );
                return frameSet;
            }
            else {
                throw new ComponentNotFoundException(brandName, serialNumber);
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
     * Read all the frame-set records existing in the database according to access.
     *
     * @param fullAccess - true for staff, false otherwise.
     * @return array list of all found frame-set records.
     * @throws SQLException
     */
    @Override
    public ArrayList<FrameSet> readAllComponentsOfType(boolean fullAccess) throws SQLException {
        ArrayList<FrameSet> frameSets = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            // Prepare query according to the user's access.
            String sqlQuery = "SELECT * FROM FrameSets";
            if (!fullAccess) {
                sqlQuery += " WHERE stock > 0 ORDER BY cost ASC";
            }
            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                String serialNumber = result.getString("serialNumber");
                String brandName = result.getString("brandName");
                String componentName =  result.getString("frameSetName");
                String forkSetName =  result.getString("forkSetName");
                String gearSetName = result.getString("gearSetName");
                boolean hasShocks = result.getBoolean("hasShocks");
                BigDecimal size = result.getBigDecimal("size");
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");

                FrameSet frameSet = new FrameSet(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        forkSetName,
                        gearSetName,
                        size,
                        hasShocks
                );
                frameSets.add(frameSet);
            }

            return frameSets;
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }

    /**
     * Create a new frame-set record in the database records.
     *
     * @param frameSet - instance of a new frame-set record to be created.
     * @return true if performed with success, false otherwise.
     * @throws SQLException
     * @throws ComponentAlreadyExistsException
     * @throws InputTooLongException
     */
    @Override
    public boolean createComponent(FrameSet frameSet)
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Check if the component with the brand name and serial number exists yet.
        if (componentExists(frameSet, "FrameSets")) {
            throw new ComponentAlreadyExistsException();
        }
        // Check if any of the inputs are longer than permitted.
        else if (
                frameSet.getComponentName().length() > MAX_INPUT_LENGTH ||
                frameSet.getBrandName().length() > MAX_INPUT_LENGTH ||
                frameSet.getSerialNumber().length() > MAX_INPUT_LENGTH ||
                frameSet.getGearSetName().length() > MAX_INPUT_LENGTH ||
                frameSet.getForkSetName().length() > MAX_INPUT_LENGTH
        ) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of a frame-set.
            String sqlQuery = "INSERT INTO FrameSets " +
                    "(serialNumber, brandName, frameSetName, forkSetName, " +
                    "gearSetName, hasShocks, size, cost, stock) \n" +
                    "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, frameSet.getSerialNumber());
            sqlStatement.setString(2, frameSet.getBrandName());
            sqlStatement.setString(3, frameSet.getComponentName());
            sqlStatement.setString(4, frameSet.getForkSetName());
            sqlStatement.setString(5, frameSet.getGearSetName());
            sqlStatement.setBoolean(6, frameSet.isHasShocks());
            sqlStatement.setBigDecimal(7, frameSet.getSize());
            sqlStatement.setBigDecimal(8, frameSet.getCost());
            sqlStatement.setInt(9, frameSet.getStock());
            int result = sqlStatement.executeUpdate();

            if (result == 1) {
                connection.commit();
                return true;
            }
            else {
                connection.rollback();
                return false;
            }
        }
        catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Update an existing frame-set in the database records.
     *
     * @param frameSet
     * @return true if updated with success, false otherwise.
     * @throws SQLException
     * @throws ComponentNotFoundException
     * @throws InputTooLongException
     */
    @Override
    public boolean updateComponent(FrameSet frameSet)
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Check if the frame-set to be updated exists at all in records.
        if (!componentExists(frameSet, "FrameSets")) {
            throw new ComponentNotFoundException(
                    frameSet.getBrandName(), frameSet.getSerialNumber()
            );
        }
        // Check if any of the inputs is longer than permitted.
        else if (
                frameSet.getComponentName().length() > MAX_INPUT_LENGTH ||
                frameSet.getGearSetName().length() > MAX_INPUT_LENGTH ||
                frameSet.getForkSetName().length() > MAX_INPUT_LENGTH
        ) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Update a DB record for a frame-set.
            String sqlQuery = "UPDATE FrameSets " +
                              "SET frameSetName = ?, forkSetName = ?, gearSetName = ?, " +
                              "hasShocks = ?, size = ?, cost = ?, stock = ? " +
                              "WHERE serialNumber = ? AND brandName = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, frameSet.getComponentName());
            sqlStatement.setString(2, frameSet.getForkSetName());
            sqlStatement.setString(3, frameSet.getGearSetName());
            sqlStatement.setBoolean(4, frameSet.isHasShocks());
            sqlStatement.setBigDecimal(5, frameSet.getSize());
            sqlStatement.setBigDecimal(6, frameSet.getCost());
            sqlStatement.setInt(7, frameSet.getStock());
            sqlStatement.setString(8, frameSet.getSerialNumber());
            sqlStatement.setString(9, frameSet.getBrandName());
            int result = sqlStatement.executeUpdate();

            if (result == 1) {
                connection.commit();
                return true;
            }
            else {
                connection.rollback();
                return false;
            }
        }
        catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Get a unique set of available sizes of frame-sets.
     *
     * @return array of available sizes (i.e. the ones with stock > 0).
     * @throws SQLException
     */
    public ArrayList<BigDecimal> getUniqueSizeSet() throws SQLException {
        ArrayList<BigDecimal> sizes = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT DISTINCT size " +
                              "FROM FrameSets " +
                              "WHERE stock > 0 " +
                              "ORDER BY size ASC";
            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                sizes.add(
                        result.getBigDecimal("size")
                );
            }

            return sizes;
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }

    /**
     * Apply frame-set filter by their sizes and inclusion of shocks in the setup.
     *
     * @param fSize - desired size of the frame-set.
     * @param fHasShocks - boolean denoting whether shocks should be included.
     * @return array list of frame-set fulfilling the filter requirements.
     * @throws SQLException
     */
    public ArrayList<FrameSet> filterFrameSets(BigDecimal fSize, Boolean fHasShocks)
            throws SQLException, NoComponentForFilterException {
        ArrayList<FrameSet> frameSets = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();

            String sqlQuery = "SELECT * FROM FrameSets WHERE stock > 0 AND " +
                              "hasShocks LIKE ? AND size LIKE ? " +
                              "ORDER BY cost ASC";
            sqlStatement = connection.prepareStatement(sqlQuery);

            // Handle different combinations of passed filters, since some might be null.
            if (fHasShocks != null) {
                sqlStatement.setBoolean(1, fHasShocks);
            }
            else {
                sqlStatement.setString(1, "%");
            }
            if (fSize != null) {
                sqlStatement.setBigDecimal(2, fSize);
            }
            else {
                sqlStatement.setString(2, "%");
            }

            result = sqlStatement.executeQuery();

            while (result.next()) {
                String serialNumber = result.getString("serialNumber");
                String componentName = result.getString("frameSetName");
                String brandName = result.getString("brandName");
                String forkSetName = result.getString("forkSetName");
                String gearSetName = result.getString("gearSetName");
                BigDecimal cost = result.getBigDecimal("cost");
                BigDecimal size = result.getBigDecimal("size");
                boolean hasShocks = result.getBoolean("hasShocks");
                int stock = result.getInt("stock");

                FrameSet frameSet = new FrameSet(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        forkSetName,
                        gearSetName,
                        size,
                        hasShocks
                );
                frameSets.add(frameSet);
            }

            if (frameSets.size() == 0) {
                throw new NoComponentForFilterException("frame-set");
            }
            else {
                return frameSets;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }
}
