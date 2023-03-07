/**
 * PairOfWheelsController.java
 *
 * PairOfWheels controller used for CRUD, finding by ID, filtering.
 * The controller's functionalities are only available to Staff.
 */

package database.controllers.component;

import exceptions.*;
import models.component.pairofwheels.BrakeType;
import models.component.pairofwheels.TyreType;
import models.component.pairofwheels.PairOfWheels;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PairOfWheelsController extends ComponentController<PairOfWheels> {
    /**
     * Search for a unique pair of wheels instance in the database records.
     *
     * @param serialNumber - serial number of a pair of wheels.
     * @param brandName - brand name of a pair of wheels.
     * @return unique instance of a pair of wheels.
     * @throws SQLException
     * @throws ComponentNotFoundException
     * @throws InputTooLongException
     */
    @Override
    public PairOfWheels findComponentById(String serialNumber, String brandName)
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Check the input validity before processing any queries.
        if (serialNumber.length() > MAX_INPUT_LENGTH || brandName.length() > MAX_INPUT_LENGTH) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM PairsOfWheels " +
                              "WHERE serialNumber = ? AND brandName = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, serialNumber);
            sqlStatement.setString(2, brandName);
            result = sqlStatement.executeQuery();

            if (result.next()) {
                String pairName = result.getString("pairOfWheelsName");
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");
                BigDecimal diameter = result.getBigDecimal("diameter");
                TyreType tyreType = TyreType.valueOf(result.getString("tyreType"));
                BrakeType brakeType = BrakeType.valueOf(result.getString("brakeType"));

                PairOfWheels pairOfWheels = new PairOfWheels(
                        serialNumber,
                        pairName,
                        brandName,
                        cost,
                        stock,
                        diameter,
                        tyreType,
                        brakeType
                );
                return pairOfWheels;
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
     * Read all the pair of wheel records existing in the database according to access.
     *
     * @param fullAccess - true for stock, false otherwise.
     * @return array list of all found pair of wheel records according to access.
     * @throws SQLException
     */
    @Override
    public ArrayList<PairOfWheels> readAllComponentsOfType(boolean fullAccess) throws SQLException {
        ArrayList<PairOfWheels> pairsOfWheels = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            // Prepare query according to the user's access.
            String sqlQuery = "SELECT * FROM PairsOfWheels";
            if (!fullAccess) {
                sqlQuery += " WHERE stock > 0 ORDER BY cost ASC";
            }

            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                String serialNumber = result.getString("serialNumber");
                String componentName = result.getString("pairOfWheelsName");
                String brandName = result.getString("brandName");
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");
                BigDecimal diameter = result.getBigDecimal("diameter");
                TyreType tyreType = TyreType.valueOf(result.getString("tyreType"));
                BrakeType brakeType = BrakeType.valueOf(result.getString("brakeType"));

                PairOfWheels pairOfWheels = new PairOfWheels(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        diameter,
                        tyreType,
                        brakeType
                );
                pairsOfWheels.add(pairOfWheels);
            }

            return pairsOfWheels;
        }
        catch (Exception e) {
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
     * Create a new pair of wheel record in the database.
     *
     * @param pairOfWheels - instance of a new pair of wheel record to be created.
     * @return true if created successfully, false otherwise.
     * @throws SQLException
     * @throws ComponentAlreadyExistsException
     * @throws InputTooLongException
     */
    @Override
    public boolean createComponent(PairOfWheels pairOfWheels)
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        if (componentExists(pairOfWheels, "PairsOfWheels")) {
            throw new ComponentAlreadyExistsException();
        }
        else if (
                pairOfWheels.getComponentName().length() > MAX_INPUT_LENGTH ||
                        pairOfWheels.getBrandName().length() > MAX_INPUT_LENGTH ||
                        pairOfWheels.getSerialNumber().length() > MAX_INPUT_LENGTH
        ) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of a wheel.
            String sqlQuery = "INSERT INTO PairsOfWheels " +
                    "(serialNumber, pairOfWheelsName, brandName, diameter, " +
                    "tyreType, brakeType, cost, stock) \n" +
                    "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, pairOfWheels.getSerialNumber());
            sqlStatement.setString(2, pairOfWheels.getComponentName());
            sqlStatement.setString(3, pairOfWheels.getBrandName());
            sqlStatement.setBigDecimal(4, pairOfWheels.getDiameter());
            sqlStatement.setString(5, String.valueOf(pairOfWheels.getTyreType()));
            sqlStatement.setString(6, String.valueOf(pairOfWheels.getBrakeType()));
            sqlStatement.setBigDecimal(7, pairOfWheels.getCost());
            sqlStatement.setInt(8, pairOfWheels.getStock());
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
        catch (Exception e) {
            connection.rollback();
            throw e;
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Update an existing pair of wheels record in the database.
     *
     * @param pairOfWheels - an instance of a wheel to be updated.
     * @return true if updated successfully, false otherwise.
     * @throws SQLException
     * @throws ComponentNotFoundException
     * @throws InputTooLongException
     */
    @Override
    public boolean updateComponent(PairOfWheels pairOfWheels)
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Check if the pair of wheels to be updated exists at all in records.
        if (!componentExists(pairOfWheels, "PairsOfWheels")) {
            throw new ComponentNotFoundException(
                    pairOfWheels.getBrandName(), pairOfWheels.getSerialNumber()
            );
        }
        else if (pairOfWheels.getComponentName().length() > MAX_INPUT_LENGTH) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Update a DB record for a pair of wheels.
            String sqlQuery = "UPDATE PairsOfWheels " +
                    "SET pairOfWheelsName = ?, diameter = ?, tyreType = ?, " +
                    "brakeType = ?, cost = ?, stock = ? " +
                    "WHERE serialNumber = ? AND brandName = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, pairOfWheels.getComponentName());
            sqlStatement.setBigDecimal(2, pairOfWheels.getDiameter());
            sqlStatement.setString(3, String.valueOf(pairOfWheels.getTyreType()));
            sqlStatement.setString(4, String.valueOf(pairOfWheels.getBrakeType()));
            sqlStatement.setBigDecimal(5, pairOfWheels.getCost());
            sqlStatement.setInt(6, pairOfWheels.getStock());
            sqlStatement.setString(7, pairOfWheels.getSerialNumber());
            sqlStatement.setString(8, pairOfWheels.getBrandName());
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
     * Get a unique set of available diameters of wheels.
     *
     * @return array of available diameters.
     * @throws SQLException
     */
    public ArrayList<BigDecimal> getUniqueDiameterSet() throws SQLException {
        ArrayList<BigDecimal> diameters = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT DISTINCT diameter " +
                              "FROM PairsOfWheels " +
                              "WHERE stock > 0 " +
                              "ORDER BY diameter ASC";
            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                diameters.add(
                        result.getBigDecimal("diameter")
                );
            }

            return diameters;
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
     * Apply wheels filters which includes diameter, tyre type and brake type.
     *
     * @param fTyreType - desired TyreType filter.
     * @param fBrakeType - desired BrakeType filter.
     * @param fDiameter - desired diameter filter.
     * @return array list of pair of wheels instances satisfying filter requirements.
     * @throws SQLException
     * @throws NoComponentForFilterException
     */
    public ArrayList<PairOfWheels> filterWheels(TyreType fTyreType, BrakeType fBrakeType, BigDecimal fDiameter)
            throws SQLException, NoComponentForFilterException {
        ArrayList<PairOfWheels> pairsOfWheels = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();

            // Use LIKE to avoid complicated if-else structure, in case some filter is null.
            String sqlQuery = "SELECT * FROM PairsOfWheels WHERE " +
                              "stock > 0 AND " +
                              "diameter LIKE ? AND " +
                              "brakeType LIKE ? AND " +
                              "tyreType LIKE ? " +
                              "ORDER BY cost ASC";
            sqlStatement = connection.prepareStatement(sqlQuery);

            // Handle different scenarios for passing filters (i.e. some might be passed as null).
            if (fDiameter != null) {
                sqlStatement.setBigDecimal(1, fDiameter);
            }
            else {
                sqlStatement.setString(1, "%");
            }
            if (fBrakeType != null) {
                sqlStatement.setString(2, fBrakeType.toString());
            }
            else {
                sqlStatement.setString(2, "%");
            }
            if (fTyreType != null) {
                sqlStatement.setString(3, fTyreType.toString());
            }
            else {
                sqlStatement.setString(3, "%");
            }

            result = sqlStatement.executeQuery();

            while (result.next()) {
                String serialNumber = result.getString("serialNumber");
                String componentName = result.getString("pairOfWheelsName");
                String brandName = result.getString("brandName");
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");
                BigDecimal diameter = result.getBigDecimal("diameter");
                TyreType tyreType = TyreType.valueOf(result.getString("tyreType"));
                BrakeType brakeType = BrakeType.valueOf(result.getString("brakeType"));

                PairOfWheels pairOfWheels = new PairOfWheels(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        diameter,
                        tyreType,
                        brakeType
                );
                pairsOfWheels.add(pairOfWheels);
            }

            if (pairsOfWheels.size() == 0) {
                throw new NoComponentForFilterException("pair of wheels");
            }
            else {
                return pairsOfWheels;
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
