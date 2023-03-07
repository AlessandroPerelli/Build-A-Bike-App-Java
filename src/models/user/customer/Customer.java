/**
 * Customer.java
 *
 * Customer model class.
 */

package models.user.customer;

import database.controllers.user.customer.CustomerController;
import models.user.User;
import services.IdGenerator;

import java.sql.SQLException;

public class Customer extends User {
    private String customerId;
    private Address address;

    public Customer(String customerId, String forename, String surname, Address address) {
        super(forename, surname);
        this.address = address;

        if (customerId == null) {
            generateCustomerId();
        }
        else {
            this.customerId = customerId;
        }
    }

    /** Generate a unique ID for the customer using the service class. */
    public void generateCustomerId() {
        String id = IdGenerator.generateId(11);

        // Make sure that the ID was never used before.
        try {
            CustomerController cc = new CustomerController();
            boolean idExists = cc.customerExists(id);

            if (!idExists) {
                this.customerId = id;
            }
            else {
                // Recursive call to find a new unique serial number.
                // The probability of entering this part of the program
                // ever is extremely low but still has to be considered.
                generateCustomerId();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Mutator for the customer ID (used only when customer is read back from database). */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /** Accessor for the generated customer ID. */
    public String getCustomerId() {
        return this.customerId;
    }

    /** Accessor for the address assigned to the customer. */
    public Address getAddress() {
        return this.address;
    }

    /** Mutator for the address assigned to the customer. */
    public void setAddress(Address address) {
        this.address = address;
    }
}
