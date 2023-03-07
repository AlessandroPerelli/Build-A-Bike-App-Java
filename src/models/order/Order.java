/**
 * Order.java
 *
 * Order model class.
 */

package models.order;

import database.controllers.order.OrderController;
import models.bicycle.Bicycle;
import models.order.items.Item;
import models.order.items.ItemType;
import services.IdGenerator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Order {
    private String orderNumber;
    private Date date;
    private BigDecimal totalCost;
    private OrderStatus status;
    private String customerId;
    private String staffUsername;
    private String bicycleSerialNumber;
    private ArrayList<Item> orderItems;

    /** Default constructor with no parameters. */
    public Order() {}

    /** Prepare a brand new order instance, when placed by a customer. */
    public void prepareOrder(String customerId, Bicycle bicycle) {
        generateOrderNumber();
        setDate(new Date());
        setStatus(OrderStatus.PENDING);
        setupOrderItems(bicycle, orderNumber);
        calculateTotalCost(bicycle);
        setBicycleSerialNumber(bicycle.getSerialNumber());
        setCustomerId(customerId);
    }

    /**
     * Set up order items, based on the assembled bicycle instance.
     * '
     * @param bicycle - bicycle associated with the order instance.
     * @return array list of prepared order items.
     */
    private void setupOrderItems(Bicycle bicycle, String orderNumber) {
        ArrayList<Item> orderItems = new ArrayList<>();
        orderItems.add(
                new Item(
                        bicycle.getSerialNumber(),
                        bicycle.getBrandName(),
                        ItemType.BICYCLE,
                        1,
                        Bicycle.getAssemblyCost(),
                        orderNumber
                )
        );
        orderItems.add(
                new Item(
                        bicycle.getHandlebar().getSerialNumber(),
                        bicycle.getHandlebar().getBrandName(),
                        ItemType.HANDLEBAR,
                        1,
                        bicycle.getHandlebar().getCost(),
                        orderNumber
                )
        );
        orderItems.add(
                new Item(
                        bicycle.getFrameSet().getSerialNumber(),
                        bicycle.getFrameSet().getBrandName(),
                        ItemType.FRAMESET,
                        1,
                        bicycle.getFrameSet().getCost(),
                        orderNumber
                )
        );
        orderItems.add(
                new Item(
                        bicycle.getPairOfWheels().getSerialNumber(),
                        bicycle.getPairOfWheels().getBrandName(),
                        ItemType.PAIROFWHEELS,
                        1,
                        bicycle.getPairOfWheels().getCost(),
                        orderNumber
                )
        );
        this.orderItems = orderItems;
    }

    /**
     * Calculate the initial total order cost based on the bicycle and its components.
     *
     * @param orderedBicycle - bicycle associated with the order.
     */
    public void calculateTotalCost(Bicycle orderedBicycle) {
        BigDecimal totalCost = new BigDecimal(0);

        for (Item orderItem : this.orderItems) {
            totalCost = totalCost.add(orderItem.getCost());
        }

        this.totalCost = totalCost;
    }

    /** Generate a unique order number for the new order using the service class. */
    public void generateOrderNumber() {
        String id = IdGenerator.generateId(11);

        // Make sure that the unique ID is not repeated.
        try {
            OrderController oc = new OrderController();
            boolean idExists = oc.orderExists(id);

            if (!idExists) {
                this.orderNumber = id;
            }
            else {
                // Recursive call to find a new unique order number.
                // The probability of entering this part of the program
                // ever is very low but still has to be considered.
                generateOrderNumber();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Accessor for the order number. */
    public String getOrderNumber() {
        return this.orderNumber;
    }

    /** Mutator for the order number (used only when creating orders retrieved from DB records.). */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /** Accessor for the order date. */
    public Date getDate() {
        return this.date;
    }

    /** Mutator for the order date. */
    public void setDate(Date date) {
        this.date = date;
    }

    /** Accessor for the order items */
    public ArrayList<Item> getOrderItems() {
        return this.orderItems;
    }

    /** Mutator for the order items. */
    public void setOrderItems(ArrayList<Item> orderItems) {
        this.orderItems = orderItems;
    }

    /** Accessor for the order status. */
    public OrderStatus getStatus() {
        return this.status;
    }

    /** Mutator for the order status. */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /** Accessor for the total cost */
    public BigDecimal getTotalCost() {
        return this.totalCost;
    }

    /** Mutator for the total cost (used only when retrieving records from the database. */
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /** Accessor for the bicycle serial number. */
    public String getBicycleSerialNumber() {
        return this.bicycleSerialNumber;
    }

    /** Mutator for the bicycle serial number. */
    public void setBicycleSerialNumber(String bicycleSerialNumber) {
        this.bicycleSerialNumber = bicycleSerialNumber;
    }

    /** Accessor for the customer ID. */
    public String getCustomerId() {
        return this.customerId;
    }

    /** Mutator for the customer ID. */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /** Accessor for the staff username. */
    public String getStaffUsername() {
        return this.staffUsername;
    }

    /** Mutator for the staff username. */
    public void setStaffUsername(String staffUsername) {
        this.staffUsername = staffUsername;
    }
}
