/**
 * AbstractDatabaseController.java
 *
 * Abstract controller for handling CRUD/login for different entities in DB.
 *
 * NB: All the subclasses of the abstract controller rely on the usage of
 * PreparedStatement, which helps to avoid SQL injection by user input.
 */

package database.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DatabaseController {
    private static final String DB_URL = "jdbc:mysql://stusql.dcs.shef.ac.uk/";
    private static final String DB_USER = "team020";
    private static final String DB_NAME = "team020";
    private static final String DB_PASSWORD = "c602ccf2";
    protected Connection connection;
    protected PreparedStatement sqlStatement;

    /**
     * Open a connection with the database using the pre-defined credentials.
     *
     * @throws SQLException
     */
    protected void openConnection() throws SQLException {
        this.connection = DriverManager.getConnection(
                DB_URL + DB_NAME, DB_USER, DB_PASSWORD
        );
    }

    /**
     * Close a connection with the database.
     *
     * @throws SQLException
     */
    protected void closeConnection() throws SQLException {
        if (sqlStatement != null) {
            sqlStatement.close();
        }

        if (connection != null) {
            connection.close();
        }
    }
}
