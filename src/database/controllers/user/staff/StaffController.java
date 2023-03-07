/**
 * StaffController.java
 *
 * Staff controller used for authentication of staff users.
 */

package database.controllers.user.staff;

import database.controllers.DatabaseController;
import exceptions.UserNotFoundException;
import models.user.staff.Staff;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffController extends DatabaseController {
    /**
     * Verify if staff member with the provided username exists in records.
     *
     * @param username - attempted staff username.
     * @return true/false (i.e. exists/not).
     * @throws SQLException
     */
    private boolean staffExists(String username) throws SQLException {
        ResultSet result = null;

        try {
            openConnection();

            // Attempt to find the staff by the username.
            String sqlQuery = "SELECT EXISTS(\n" +
                    "\tSELECT * FROM Staff WHERE \n" +
                    "\t\tstaffUsername = ?\n\t)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, username);

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
     * Handle login attempt for a staff user account.
     *
     * @param staff
     * @return
     * @throws UserNotFoundException
     * @throws SQLException
     */
    public Staff attemptLogIn(Staff staff) throws UserNotFoundException, SQLException {
        ResultSet result = null;
        String username = staff.getUsername();
        String password = staff.getPassword();

        if (!staffExists(username)) {
            throw new UserNotFoundException();
        }

        try {
            openConnection();

            // Try to authenticate the staff user by their password and username.
            String sqlQuery = "SELECT forename, surname FROM Staff " +
                              "WHERE staffUsername = ? AND password = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, username);
            sqlStatement.setString(2, password);

            result = sqlStatement.executeQuery();
            result.next();
            String forename = result.getString("forename");
            String surname = result.getString("surname");

            if (forename.equals("") || surname.equals("")) {
                throw new SQLException();
            }
            else {
                staff.setForename(forename);
                staff.setSurname(surname);
                return staff;
            }
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }
}
