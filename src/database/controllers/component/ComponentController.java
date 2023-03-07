/**
 * ComponentController.java
 *
 * Abstract component controller, which is used as base for other CRUD controllers.
 * The controller's functionalities are only available to Staff.
 */

package database.controllers.component;

import database.controllers.DatabaseController;
import exceptions.ComponentAlreadyExistsException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import models.component.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class ComponentController<ComponentType extends Component>
        extends DatabaseController {
    protected final static int MAX_INPUT_LENGTH = 20;

    /**
     * Abstract method to be implemented by each specific component controller
     * to get an instance of a component by passing its unique combination of
     * serial number and brand name.
     *
     * @param serialNumber - serial number of a component.
     * @param brandName - brand name of a component.
     * @return instance of the searched component (if found).
     */
    protected abstract ComponentType findComponentById(String serialNumber, String brandName)
            throws SQLException, ComponentNotFoundException, InputTooLongException;

    /**
     * Abstract method to be implemented by each specific component controller
     * to create a new instance of this component in the database records.
     *
     * @param component - instance of a new component record to be created.
     * @return true if created with success, false otherwise.
     * @throws SQLException
     * @throws ComponentAlreadyExistsException
     * @throws InputTooLongException
     */
    protected abstract boolean createComponent(ComponentType component)
            throws SQLException, ComponentAlreadyExistsException, InputTooLongException;

    /**
     * Abstract method to be implemented by each specific component controller
     * to update an existing component record in the database.
     *
     * @param component - instance of a component to be updated
     * @return true if updated with success, false otherwise.
     * @throws SQLException
     * @throws ComponentNotFoundException
     */
    protected abstract boolean updateComponent(ComponentType component)
            throws SQLException, ComponentNotFoundException, InputTooLongException;

    /**
     * Abstract method to get all database records of a specific component type.
     *
     * @param fullAccess - true for staff, false for shopper/customer.
     * @return array list of all found component (of specific type) records.
     *         - fullAccess: staff receives all the possible records;
     *         - !fullAccess: customer/shopper receives only the records with stock value > 0;
     * @throws SQLException
     */
    protected abstract ArrayList<ComponentType> readAllComponentsOfType(boolean fullAccess) throws SQLException;

    /**
     * Check if a component of any type exists in the database records.
     *
     * @param component - any instance of a component to be searched.
     * @param tableName - target DB table to be searched.
     * @return true/false (respectively meaning: exists/not exists).
     * @throws SQLException
     */
    public boolean componentExists(Component component, String tableName) throws SQLException {
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT EXISTS(\n" +
                    "\tSELECT * FROM " +
                    tableName +
                    " WHERE serialNumber = ? AND brandName = ?\n)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, component.getSerialNumber());
            sqlStatement.setString(2, component.getBrandName());

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
     * Delete a component of any type from the database records.
     *
     * @param component - component to be deleted (any subclass of Component).
     * @return true if deleted with success, false otherwise.
     * @throws SQLException
     * @throws ComponentNotFoundException
     */
    public boolean deleteComponent(Component component, String tableName)
            throws SQLException, ComponentNotFoundException {
        // Check if the component to be deleted exists at all in records.
        if (!componentExists(component, tableName)) {
            throw new ComponentNotFoundException(
                    component.getBrandName(), component.getSerialNumber()
            );
        }

        try {
            openConnection();
            String sqlQuery = "DELETE FROM "+
                              tableName +
                              " WHERE brandName = ? AND serialNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, component.getBrandName());
            sqlStatement.setString(2, component.getSerialNumber());

            int result = sqlStatement.executeUpdate();
            if (result != 1) {
                throw new SQLException("Component could not be deleted!");
            }

            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            closeConnection();
        }
    }
}
