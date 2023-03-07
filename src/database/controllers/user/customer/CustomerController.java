/**
 * CustomerController.java
 *
 * Customer controller used for customer creation, updating
 * their details, authentication and searching by ID.
 */

package database.controllers.user.customer;

import database.controllers.DatabaseController;
import exceptions.InputTooLongException;
import exceptions.InvalidAddressException;
import exceptions.UserNotFoundException;
import models.user.customer.Address;
import models.user.customer.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerController extends DatabaseController {
    private final static AddressController aController = new AddressController();
    private final static int MAX_INPUT_LENGTH = 20;

    /**
     * Verify if customer with the unique ID exists in the database.
     *
     * @param customerId
     * @return true if exists, false otherwise.
     * @throws SQLException
     */
    public boolean customerExists(String customerId) throws SQLException {
        ResultSet result = null;

        try {
            openConnection();

            // Attempt to find the customer by their ID.
            String sqlQuery = "SELECT EXISTS(SELECT * FROM Customers WHERE customerId = ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, customerId);

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
     * Create a new customer record in the database.
     *
     * @param customer - new customer instance to be created.
     * @return true if performed with success, false otherwise.
     * @throws SQLException
     */
    public boolean createCustomer(Customer customer) throws SQLException {
        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of a customer.
            String sqlQuery = "INSERT INTO Customers " +
                    "(customerId, forename, surname, houseNumber, postcode) " +
                    "VALUES (?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, customer.getCustomerId());
            sqlStatement.setString(2, customer.getForename());
            sqlStatement.setString(3, customer.getSurname());
            sqlStatement.setInt(4, customer.getAddress().getHouseNumber());
            sqlStatement.setString(5, customer.getAddress().getPostcode());
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
     * Authenticate a customer by their forename, surname, address postcode and house number.
     *
     * @param customer - customer instance to be authenticated.
     * @return if authenticated, returns a complete instance of the customer,
     *         which includes the customer ID value.
     * @throws UserNotFoundException
     * @throws InputTooLongException
     * @throws SQLException
     */
    public Customer authenticateCustomer(Customer customer)
            throws UserNotFoundException, InputTooLongException, SQLException {
        ResultSet result = null;

        try {
            openConnection();
            // Try to authenticate the customer by their forename, surname, postcode, house number.
            String sqlQuery = "SELECT customerId FROM Customers " +
                              "WHERE forename = ? AND surname = ? AND postcode = ? AND houseNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, customer.getForename());
            sqlStatement.setString(2, customer.getSurname());
            sqlStatement.setString(3, customer.getAddress().getPostcode());
            sqlStatement.setInt(4, customer.getAddress().getHouseNumber());
            result = sqlStatement.executeQuery();

            if (result.next()) {
                String customerId = result.getString("customerId");
                customer.setCustomerId(customerId);
                return customer;
            }

            return null;
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }

    /**
     * Read a list of all the customers (available to staff only).
     *
     * @return array list of all customers of the store..
     * @throws SQLException
     */
    public ArrayList<Customer> readAllCustomers() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();

            // Prepare query according to the user's access.
            String sqlQuery = "SELECT * FROM Customers";
            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                String customerId = result.getString("customerId");
                String forename = result.getString("forename");
                String surname = result.getString("surname");
                String postcode = result.getString("postcode");
                int houseNumber = result.getInt("houseNumber");

                Address address = new Address(
                        houseNumber,
                        null,
                        postcode,
                        null
                );
                Customer customer = new Customer(
                        customerId,
                        forename,
                        surname,
                        address
                );
                customers.add(customer);
            }

            return customers;
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
     * Update customer details, e.g. forename, surname, address.
     *
     * @param customer - updated instance of the customer (but the ID remains the same).
     * @return true if updated with success, false otherwise.
     * @throws SQLException
     * @throws UserNotFoundException
     * @throws InputTooLongException
     */
    public boolean updateDetails(Customer customer)
            throws SQLException, UserNotFoundException, InputTooLongException, InvalidAddressException {
        // Check if the customer exists at all.
        if (!customerExists(customer.getCustomerId())) {
            throw new UserNotFoundException();
        }
        // Check if any of the inputs violate the minimum length condition.
        else if (
                customer.getForename().length() > MAX_INPUT_LENGTH ||
                customer.getSurname().length() > MAX_INPUT_LENGTH
        ) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            int result;
            openConnection();
            connection.setAutoCommit(false);

            // Attempt to update together with a new address, if necessary.
            if (!aController.matchAddress(customer.getAddress())) {
                aController.createAddress(customer.getAddress());
                String sqlQuery = "UPDATE Customers " +
                                  "SET forename = ?, surname = ?, houseNumber = ?, postcode = ? " +
                                  "WHERE customerId = ?";
                sqlStatement = connection.prepareStatement(sqlQuery);
                sqlStatement.setString(1, customer.getForename());
                sqlStatement.setString(2, customer.getSurname());
                sqlStatement.setInt(3, customer.getAddress().getHouseNumber());
                sqlStatement.setString(4, customer.getAddress().getPostcode());
                sqlStatement.setString(5, customer.getCustomerId());
            }
            // Otherwise, update just the forename and surname of the customer.
            else {
                String sqlQuery = "UPDATE Customers SET forename = ?, surname = ? WHERE customerId = ?";
                sqlStatement = connection.prepareStatement(sqlQuery);
                sqlStatement.setString(1, customer.getForename());
                sqlStatement.setString(2, customer.getSurname());
                sqlStatement.setString(3, customer.getCustomerId());
            }

            result = sqlStatement.executeUpdate();
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
}
