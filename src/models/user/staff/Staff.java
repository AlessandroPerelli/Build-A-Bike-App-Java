/**
 * Staff.java
 *
 * Staff model.
 */

package models.user.staff;

import exceptions.InvalidCredentialsException;
import models.order.Order;
import models.user.User;
import services.EncryptionHandler;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

public class Staff extends User {
    private String username;
    private String password;
    private Collection<Order> orders;

    /** Default constructor with no parameters. */
    public Staff() {}

    /**
     * Constructor with parameters.
     *
     * @param username
     * @param forename
     * @param surname
     * @param password
     */
    public Staff(String username, String forename, String surname, String password) {
        super(forename, surname);
        this.username = username;
        this.password = password;
    }

    /** Accessor for username. */
    public String getUsername() {
        return this.username;
    }

    /** Mutator for username. */
    public void setUsername(String username) throws NoSuchAlgorithmException, InvalidCredentialsException {
        if (username.length() < 1) {
            throw new InvalidCredentialsException("The username field cannot be empty!");
        }
        else {
            this.username = EncryptionHandler.encryptData(username);
        }
    }

    /** Accessor for encrypted password. */
    public String getPassword() {
        return this.password;
    }

    /** Password mutator performing encryption.*/
    public void setPassword(char[] password) throws NoSuchAlgorithmException, InvalidCredentialsException {
        if (password.length < 1) {
            throw new InvalidCredentialsException("The password field cannot be empty!");
        }
        else {
            String passwordStr = String.valueOf(password);
            this.password = EncryptionHandler.encryptData(passwordStr);
        }
    }

    /** Accessor for staff orders. */
    public Collection<Order> getOrders() {
        return this.orders;
    }

    // TODO: Add an order for a staff member.
    //    public void addOrder(Order) {
    //
    //    }
}
