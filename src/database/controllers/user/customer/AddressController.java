/**
 * AddressController.java
 *
 * Address controller to handle creation, updating, finding by composite ID.
 */

package database.controllers.user.customer;

import database.controllers.DatabaseController;
import exceptions.InputTooLongException;
import exceptions.InvalidAddressException;
import models.user.customer.Address;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressController extends DatabaseController {
    private final static int MAX_INPUT_LENGTH = 20;

    /**
     * Find address by ID (i.e. composite of postcode and house number).
     *
     * @param address - address to be found.
     * @return full instance of the address, including road and city.
     * @throws SQLException
     */
    public Address findAddressById(Address address) throws SQLException {
        ResultSet result = null;
        String postcode = address.getPostcode();
        int houseNumber = address.getHouseNumber();

        try {
            openConnection();

            // Attempt to find the address by composite ID.
            String sqlQuery = "SELECT * FROM Addresses WHERE houseNumber = ? AND postcode = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setInt(1, houseNumber);
            sqlStatement.setString(2, postcode);
            result = sqlStatement.executeQuery();

            if (result.next()) {
                String foundRoad = result.getString("roadName");
                String foundCity = result.getString("cityName");
                address.setRoad(foundRoad);
                address.setCity(foundCity);
                return address;
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
     * Match address records by all its field.
     *
     * @param address
     * @return
     * @throws SQLException
     * @throws InvalidAddressException
     * @throws InputTooLongException
     */
    public boolean matchAddress(Address address)
            throws SQLException, InvalidAddressException, InputTooLongException {
        ResultSet result = null;
        String postcode = address.getPostcode();
        int houseNumber = address.getHouseNumber();
        String city = address.getCity();
        String road = address.getRoad();

        // Check if there are any length violation, before processing any queries.
        if (
                postcode.length() > MAX_INPUT_LENGTH ||
                city.length() > MAX_INPUT_LENGTH ||
                road.length() > MAX_INPUT_LENGTH
        ) {
            throw new InputTooLongException(MAX_INPUT_LENGTH);
        }

        try {
            openConnection();

            // Attempt to find the address by composite ID.
            String sqlQuery = "SELECT * FROM Addresses WHERE houseNumber = ? AND postcode = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setInt(1, houseNumber);
            sqlStatement.setString(2, postcode);
            result = sqlStatement.executeQuery();

            // If found, check if the rest of the details of the address are coherent.
            if (result.next()) {
                String foundRoad = result.getString("roadName");
                String foundCity = result.getString("cityName");

                if ((!road.equals(foundRoad)) || (!city.equals(foundCity))) {
                    throw new InvalidAddressException();
                }
                return true;
            }
            return false;
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }

    /**
     * Create a new address record in the database.
     *
     * @param address - new address instance to be created.
     * @return
     * @throws SQLException
     * @throws InvalidAddressException
     * @throws InputTooLongException
     */
    public boolean createAddress(Address address)
            throws SQLException, InvalidAddressException, InputTooLongException {
        if (matchAddress(address)) {
            throw new InvalidAddressException();
        }

        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of an address.
            String sqlQuery = "INSERT INTO Addresses " +
                              "(houseNumber, postcode, roadName, cityName) \n" +
                              "\t VALUES (?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setInt(1, address.getHouseNumber());
            sqlStatement.setString(2, address.getPostcode());
            sqlStatement.setString(3, address.getRoad());
            sqlStatement.setString(4, address.getCity());
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
     * Delete address by their composite ID (if exists).
     *
     * @param address - address instance to be deleted.
     * @return true if deleted successfully, false otherwise.
     * @throws SQLException
     * @throws InvalidAddressException
     * @throws InputTooLongException
     */
    public boolean deleteAddress(Address address)
            throws SQLException, InvalidAddressException, InputTooLongException {
        // Check if the provided address exists at all.
        if (!matchAddress(address)) {
            throw new InvalidAddressException();
        }

        try {
            openConnection();
            String sqlQuery = "DELETE FROM Addresses WHERE houseNumber = ? AND postcode = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setInt(1, address.getHouseNumber());
            sqlStatement.setString(2, address.getPostcode());
            int result = sqlStatement.executeUpdate();
            if (result != 1) {
                throw new SQLException("Address could not be deleted!");
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
