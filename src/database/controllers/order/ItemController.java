/**
 * ItemController.java
 *
 * Item controller responsible for managing order items.
 */

package database.controllers.order;

import database.controllers.DatabaseController;
import models.order.items.Item;
import models.order.items.ItemType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemController extends DatabaseController {
    /**
     * Create order items records for the associated order.
     *
     * @param orderItems - order items associated with an order.
     * @return true if performed with success, false otherwise.
     * @throws SQLException
     */
    public boolean createOrderItems(ArrayList<Item> orderItems) throws SQLException {
        try {
            openConnection();
            connection.setAutoCommit(false);

            // Create a new DB record of an order item.
            String sqlQuery = "INSERT INTO Items " +
                              "(serialNumber, quantity, cost, type, orderNumber, brandName) VALUES " +
                              "(?, ?, ?, ?, ?, ?)";
            sqlStatement = connection.prepareStatement(sqlQuery);

            // Prepare all the variables in the query.
            for (Item item : orderItems) {
                sqlStatement.setString(1, item.getItemId());
                sqlStatement.setInt(2, item.getQuantity());
                sqlStatement.setBigDecimal(3, item.getCost());
                sqlStatement.setString(4, String.valueOf(item.getType()));
                sqlStatement.setString(5, item.getOrderNumber());
                sqlStatement.setString(6, item.getBrandName());
                sqlStatement.addBatch();
            }

            sqlStatement.executeBatch();
            connection.commit();
            return true;
        }
        catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw e;
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Find order items that match the order by its number.
     *
     * @param orderNumber - order number.
     * @return list of order items matching the serial number.
     * @throws SQLException
     */
    public ArrayList<Item> findOrderItems(String orderNumber) throws SQLException {
        ArrayList<Item> orderItems = new ArrayList<>();
        ResultSet result = null;

        try {
            openConnection();
            String sqlQuery = "SELECT * FROM Items WHERE orderNumber = ?";
            sqlStatement = connection.prepareStatement(sqlQuery);
            sqlStatement.setString(1, orderNumber);
            result = sqlStatement.executeQuery();

            while (result.next()) {
                String itemId = result.getString("serialNumber");
                String itemBrandName = result.getString("brandName");
                ItemType itemType = ItemType.valueOf(result.getString("type"));
                int quantity = result.getInt("quantity");
                BigDecimal cost = result.getBigDecimal("cost");

                Item currItem = new Item(
                        itemId,
                        itemBrandName,
                        itemType,
                        quantity,
                        cost,
                        orderNumber
                );
                orderItems.add(currItem);
            }

            return orderItems;
        }
        finally {
            if (result != null) {
                result.close();
            }

            closeConnection();
        }
    }
}
