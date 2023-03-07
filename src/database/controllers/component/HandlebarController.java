/**
 * HandlebarController.java
 *
 * Handlebar controller used for CRUD, finding by ID, filtering.
 * The controller's functionalities are only available to Staff.
 */

package database.controllers.component;

import exceptions.*;
import models.component.handlebar.Handlebar;
import models.component.handlebar.HandlebarType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HandlebarController extends ComponentController<Handlebar> {
    /**
     * Search for a unique handlebar instance in the database records.
     *
     * @param serialNumber - serial number of a handlebar.
     * @param brandName - brand name of a handlebar.
     * @return unique instance of a handlebar.
     * @throws SQLException
     * @throws ComponentNotFoundException
     * @throws InputTooLongException
     */
    @Override
    public Handlebar findComponentById(String serialNumber, String brandName)
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Check the input validity before processing any queries.
        if (serialNumber.length() > MAX_INPUT_LENGTH || brandName.length() > MAX_INPUT_LENGTH) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        ResultSet result = null;

        try {
            openConnection();

            String sqlQuery = "SELECT * FROM Handlebars " +
                    "WHERE serialNumber = ? AND brandName = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, serialNumber);
            sqlStatement.setString(2, brandName);
            result = sqlStatement.executeQuery();

            if (result.next()) {
                String componentName = result.getString("handlebarName");
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");
                HandlebarType type = HandlebarType.valueOf(result.getString("type"));

                Handlebar handlebar = new Handlebar(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        type
                );
                return handlebar;
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
     * Read all the handlebar records existing in the database according to access.
     *
     * @param fullAccess - true for staff, false otherwise.
     * @return array list of all found handlebar records according to access.
     * @throws SQLException
     */
    @Override
    public ArrayList<Handlebar> readAllComponentsOfType(boolean fullAccess) throws SQLException {
        ArrayList<Handlebar> handlebars = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            // Prepare query according to the user's access.
            String sqlQuery = "SELECT * FROM Handlebars";
            if (!fullAccess) {
                sqlQuery += " WHERE stock > 0 ORDER BY cost ASC";
            }

            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                String serialNumber = result.getString("serialNumber");
                String componentName = result.getString("handlebarName");
                String brandName = result.getString("brandName");
                HandlebarType type = HandlebarType.valueOf(result.getString("type"));
                BigDecimal cost = result.getBigDecimal("cost");
                int stock = result.getInt("stock");

                Handlebar handlebar = new Handlebar(
                        serialNumber,
                        componentName,
                        brandName,
                        cost,
                        stock,
                        type
                );
                handlebars.add(handlebar);
            }

            return handlebars;
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
     * Create a new handlebar record in the database.
     *
     * @param handlebar - instance of a new handlebar record to be created.
     * @return true if created successfully, false otherwise.
     * @throws SQLException
     * @throws ComponentAlreadyExistsException
     * @throws InputTooLongException
     */
    @Override
    public boolean createComponent(Handlebar handlebar)
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException {
        // Check if the handlebar instance is not repeated in the records.
        if (componentExists(handlebar, "Handlebars")) {
            throw new ComponentAlreadyExistsException();
        }
        else if (
                handlebar.getComponentName().length() > MAX_INPUT_LENGTH ||
                handlebar.getBrandName().length() > MAX_INPUT_LENGTH ||
                handlebar.getSerialNumber().length() > MAX_INPUT_LENGTH
        ) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of a handlebar.
            String sqlQuery = "INSERT INTO Handlebars " +
                              "(serialNumber, handlebarName, brandName, type, cost, stock)\n" +
                              "\tVALUES (?, ?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, handlebar.getSerialNumber());
            sqlStatement.setString(2, handlebar.getComponentName());
            sqlStatement.setString(3, handlebar.getBrandName());
            sqlStatement.setString(4, String.valueOf(handlebar.getType()));
            sqlStatement.setBigDecimal(5, handlebar.getCost());
            sqlStatement.setInt(6, handlebar.getStock());
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
     * Update an existing handlebar record in the database.
     *
     * @param handlebar - handlebar instance to be updated.
     * @return true if updated successfully, false otherwise.
     * @throws SQLException
     * @throws ComponentNotFoundException
     * @throws InputTooLongException
     */
    @Override
    public boolean updateComponent(Handlebar handlebar)
            throws SQLException, ComponentNotFoundException, InputTooLongException {
        // Check if the handlebar to be updated exists at all in records.
        if (!componentExists(handlebar, "Handlebars")) {
            throw new ComponentNotFoundException(
                    handlebar.getBrandName(), handlebar.getSerialNumber()
            );
        }
        // Check if the permitted String input is not too long.
        else if (handlebar.getComponentName().length() > MAX_INPUT_LENGTH) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Update a DB record for a handlebar.
            String sqlQuery = "UPDATE Handlebars " +
                    "SET handlebarName = ?, type = ?, cost = ?, stock = ? WHERE\n" +
                    "\tserialNumber = ? AND brandName = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, handlebar.getComponentName());
            sqlStatement.setString(2, String.valueOf(handlebar.getType()));
            sqlStatement.setBigDecimal(3, handlebar.getCost());
            sqlStatement.setInt(4, handlebar.getStock());
            sqlStatement.setString(5, handlebar.getSerialNumber());
            sqlStatement.setString(6, handlebar.getBrandName());
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
     * Filter handlebars instances by HandlebarType.
     *
     * @param fHandlebarType - desired handlebar filter.
     * @return array list of handlebars of the selected type.
     * @throws SQLException
     */
    public ArrayList<Handlebar> filterHandlebars(HandlebarType fHandlebarType)
            throws SQLException, NoComponentForFilterException {
        ArrayList<Handlebar> handlebars = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM Handlebars " +
                              "WHERE type = ? AND stock > 0";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, fHandlebarType.toString());
            result = sqlStatement.executeQuery();

            if (result == null) {
                throw new NoComponentForFilterException("handlebar");
            }
            else {
                while (result.next()) {
                    String serialNumber = result.getString("serialNumber");
                    String componentName = result.getString("handlebarName");
                    String brandName = result.getString("brandName");
                    HandlebarType type = HandlebarType.valueOf(result.getString("type"));
                    BigDecimal cost = result.getBigDecimal("cost");
                    int stock = result.getInt("stock");

                    Handlebar handlebar = new Handlebar(
                            serialNumber,
                            componentName,
                            brandName,
                            cost,
                            stock,
                            type
                    );
                    handlebars.add(handlebar);
                }

                return handlebars;
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
