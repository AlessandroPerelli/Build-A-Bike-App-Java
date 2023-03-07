/**
 * OrderController.java
 *
 * Order controller used for creation, deletion, viewing all
 * pending orders, staff orders, customer orders, etc.
 */

package database.controllers.order;

import database.controllers.DatabaseController;
import database.controllers.bicycle.BicycleController;
import exceptions.*;
import models.order.Order;
import models.order.OrderStatus;
import models.order.items.Item;
import models.user.customer.Customer;
import models.user.staff.Staff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderController extends DatabaseController {
    private final static ItemController iController = new ItemController();
    private final static BicycleController bController = new BicycleController();

    /**
     * Check if order with the provided number exists in the records.
     *
     * @param orderNumber - number identifying the order to be found.
     * @return true if exists, false otherwise.
     * @throws SQLException
     */
    public boolean orderExists(String orderNumber) throws SQLException {
        ResultSet result = null;

        try {
            openConnection();

            // Attempt to find the staff by the username.
            String sqlQuery = "SELECT EXISTS(\n" +
                              "\tSELECT * FROM Orders WHERE \n" +
                              "\t\torderNumber = ?\n\t)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, orderNumber);
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
     * Find order by ID, i.e. by its order number.
     *
     * @param orderNumber - order number.
     * @return instance of the order (if found).
     * @throws SQLException
     * @throws OrderNotFoundException
     */
    public Order findOrderById(String orderNumber)
            throws SQLException, InvalidOrderException, OrderNotFoundException {
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM Orders WHERE orderNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, orderNumber);
            result = sqlStatement.executeQuery();

            if (result.next()) {
                Order order = new Order();
                order.setOrderNumber(orderNumber);
                order.setDate(
                        result.getDate("date")
                );
                order.setTotalCost(
                        result.getBigDecimal("totalCost")
                );
                order.setStatus(
                        OrderStatus.valueOf(result.getString("status"))
                );
                order.setBicycleSerialNumber(
                        result.getString("serialNumber")
                );

                // Get order items of the current order.
                ArrayList<Item> currItems = iController.findOrderItems(orderNumber);
                if (currItems.size() != 4) {
                    throw new InvalidOrderException(orderNumber);
                }

                order.setOrderItems(currItems);
                return order;
            }

            throw new OrderNotFoundException(orderNumber);
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }

    /**
     * Create a new order record, together with order items.
     *
     * @param order - full order including order items, bicycle, customer.
     * @return true if successfully created, false otherwise.
     * @throws SQLException
     */
    public boolean createOrder(Order order) throws SQLException, InvalidOrderException {
        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of an order.
            String sqlQuery = "INSERT INTO Orders " +
                    "(orderNumber, date, totalCost, status, " +
                    "staffUsername, serialNumber, customerId) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, order.getOrderNumber());
            sqlStatement.setDate(2, new java.sql.Date(order.getDate().getTime()));
            sqlStatement.setBigDecimal(3, order.getTotalCost());
            sqlStatement.setString(4, String.valueOf(order.getStatus()));
            sqlStatement.setString(5, order.getStaffUsername());
            sqlStatement.setString(6, order.getBicycleSerialNumber());
            sqlStatement.setString(7, order.getCustomerId());
            int result = sqlStatement.executeUpdate();

            if (result == 1) {
                connection.commit();
                // If successful, create associated order items.
                iController.createOrderItems(order.getOrderItems());
                return true;
            }
            else {
                connection.rollback();
                throw new InvalidOrderException(order.getOrderNumber());
            }
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Delete an existing order from records. Include deletion of order items
     * and the bicycle instance.
     *
     * @param order - order instance to be deleted.
     * @return true if deleted with success, false otherwise.
     * @throws SQLException
     */
    public boolean deleteOrder(Order order) throws SQLException, OrderNotFoundException,
            InputTooLongException, ComponentNotFoundException, BicycleNotFoundException {
        String orderNumber = order.getOrderNumber();
        String bicycleSerialNumber = order.getBicycleSerialNumber();

        // Check if the order exists at all.
        if (!orderExists(orderNumber)) {
            throw new OrderNotFoundException(orderNumber);
        }

        try {
            if (bController.deleteBicycle(bicycleSerialNumber)) {
                openConnection();
                String sqlQuery = "DELETE Orders, Items  FROM Orders INNER JOIN Items " +
                                  "WHERE Orders.orderNumber = Items.orderNumber AND Orders.orderNumber = ?";
                sqlStatement = connection.prepareStatement(sqlQuery);
                sqlStatement.setString(1, orderNumber);
                sqlStatement.executeUpdate();
                return true;
            }
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Find all orders for a customer.
     *
     * @param customer - customer whose orders will be searched.
     * @return array list of customer's orders.
     * @throws SQLException
     * @throws NoOrderForUserException
     * @throws InvalidOrderException
     */
    public ArrayList<Order> findCustomerOrders(Customer customer)
            throws SQLException, NoOrderForUserException, InvalidOrderException {
        String customerId = customer.getCustomerId();
        String forename = customer.getForename();
        String surname = customer.getSurname();
        ArrayList<Order> customerOrders = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM Orders WHERE customerId = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, customerId);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                // Set up the order object.
                Order currOrder = new Order();
                String orderNumber = result.getString("orderNumber");
                currOrder.setOrderNumber(orderNumber);
                currOrder.setDate(
                        result.getDate("date")
                );
                currOrder.setTotalCost(
                        result.getBigDecimal("totalCost")
                );
                currOrder.setStatus(
                        OrderStatus.valueOf(result.getString("status"))
                );
                currOrder.setBicycleSerialNumber(
                        result.getString("serialNumber")
                );

                // Get order items of the current order.
                ArrayList<Item> currItems = iController.findOrderItems(orderNumber);
                if (currItems.size() != 4) {
                    throw new InvalidOrderException(orderNumber);
                }
                else {
                    currOrder.setOrderItems(currItems);
                    customerOrders.add(currOrder);
                }
            }

            if (customerOrders.size() > 0) {
                return customerOrders;
            }
            else {
                throw new NoOrderForUserException(forename, surname);
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

    /**
     * Assign a pending order to a staff member.
     *
     * @param orderNumber - order number to be assigned.
     * @param staffUsername - username of the staff who takes the order.
     * @return true if done with success, false otherwise.
     * @throws SQLException
     */
    public boolean assignStaffOrder(String orderNumber, String staffUsername) throws SQLException {
        try {
            openConnection();
            connection.setAutoCommit(false);

            String sqlQuery = "UPDATE Orders SET staffUsername = ? WHERE orderNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, staffUsername);
            sqlStatement.setString(2, orderNumber);
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
     * Cancel the order assignment to a staff member.
     *
     * @param orderNumber - order number to be dis-assigned.
     * @return true if done with success, false otherwise.
     * @throws SQLException
     */
    public boolean cancelStaffOrder(String orderNumber) throws SQLException {
        try {
            openConnection();
            connection.setAutoCommit(false);

            String sqlQuery = "UPDATE Orders SET staffUsername = NULL, status = 'PENDING' " +
                              "WHERE orderNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, orderNumber);
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
     * Update order status, e.g. after payment confirmation, or finishing assembling.
     * Or otherwise, when payment has been denied and status will be pending again.
     *
     * @param order - order instance to be updated.
     * @return true if successfully updated, false otherwise.
     * @throws SQLException
     */
    public boolean updateOrderStatus(Order order) throws SQLException {
        try {
            openConnection();
            connection.setAutoCommit(false);
            String sqlQuery = "UPDATE Orders SET status = ? WHERE orderNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, String.valueOf(order.getStatus()));
            sqlStatement.setString(2, order.getOrderNumber());
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
     * Find all orders assigned to a staff member.
     *
     * @param staff - staff whose orders will be searched.
     * @return array list orders assigned to the staff member.
     * @throws SQLException
     * @throws NoOrderForUserException
     * @throws InvalidOrderException
     */
    public ArrayList<Order> findStaffOrders(Staff staff)
            throws SQLException, NoOrderForUserException, InvalidOrderException {
        String staffUsername = staff.getUsername();
        String forename = staff.getForename();
        String surname = staff.getSurname();
        ArrayList<Order> staffOrders = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM Orders WHERE staffUsername = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, staffUsername);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                Order currOrder = new Order();
                String orderNumber = result.getString("orderNumber");
                currOrder.setOrderNumber(orderNumber);
                currOrder.setDate(
                        result.getDate("date")
                );
                currOrder.setTotalCost(
                        result.getBigDecimal("totalCost")
                );
                currOrder.setStatus(OrderStatus.valueOf(result.getString("status")));
                currOrder.setBicycleSerialNumber(
                        result.getString("serialNumber")
                );
                currOrder.setCustomerId(
                        result.getString("customerId")
                );

                // Get order items of the current order.
                ArrayList<Item> currItems = iController.findOrderItems(orderNumber);
                if (currItems.size() != 4) {
                    throw new InvalidOrderException(orderNumber);
                }
                else {
                    currOrder.setOrderItems(currItems);
                    staffOrders.add(currOrder);
                }
            }

            if (staffOrders.size() > 0) {
                return staffOrders;
            }
            else {
                throw new NoOrderForUserException(forename, surname);
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

    /**
     * Read all PENDING (i.e. unassigned) orders, only available to the staff.
     *
     * @return array list of all unassigned orders found in the system.
     * @throws SQLException
     */
    public ArrayList<Order> readAllPendingOrders() throws SQLException, InvalidOrderException {
        ArrayList<Order> pendingOrders = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();

            String sqlQuery = "SELECT * FROM Orders " +
                              "WHERE status = 'PENDING' AND staffUsername IS NULL";
            sqlStatement = connection.prepareStatement(sqlQuery);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                Order currOrder = new Order();
                String orderNumber = result.getString("orderNumber");
                currOrder.setOrderNumber(orderNumber);
                currOrder.setDate(
                        result.getDate("date")
                );
                currOrder.setTotalCost(
                        result.getBigDecimal("totalCost")
                );
                currOrder.setStatus(OrderStatus.PENDING);
                currOrder.setBicycleSerialNumber(
                        result.getString("serialNumber")
                );
                currOrder.setCustomerId(
                        result.getString("customerId"));

                // Get order items of the current order.
                ArrayList<Item> currItems = iController.findOrderItems(orderNumber);
                if (currItems.size() != 4) {
                    throw new InvalidOrderException(orderNumber);
                }
                else {
                    currOrder.setOrderItems(currItems);
                    pendingOrders.add(currOrder);
                }
            }

            return pendingOrders;
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
