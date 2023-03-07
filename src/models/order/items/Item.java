/**
 * Item.java
 *
 * Item model class (associated order's item).
 */

package models.order.items;

import java.math.BigDecimal;

public class Item {
    private String itemId;
    private String brandName;
    private ItemType type;
    private int quantity;
    private BigDecimal cost;
    private String orderNumber;

    /**
     * Constructor with parameters, based on Order instance.
     *
     * @param itemId
     * @param brandName
     * @param type
     * @param quantity
     * @param cost
     * @param orderNumber
     */
    public Item(
            String itemId, String brandName, ItemType type,
            int quantity, BigDecimal cost, String orderNumber
    ) {
        this.itemId = itemId;
        this.brandName = brandName;
        this.type = type;
        this.quantity = quantity;
        this.cost = cost;
        this.orderNumber = orderNumber;
    }

    /** Accessor for the item ID. */
    public String getItemId() {
        return this.itemId;
    }

    /** Mutator for the item ID. */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /** Accessor for the order item type. */
    public ItemType getType() {
        return this.type;
    }

    /** Mutator for the order item type. */
    public void setType(ItemType type) {
        this.type = type;
    }

    /** Accessor for quantity. */
    public int getQuantity() {
        return this.quantity;
    }

    /** Mutator for quantity. */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /** Accessor for cost. */
    public BigDecimal getCost() {
        return this.cost;
    }

    /** Mutator for cost. */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    /** Accessor for the order number. */
    public String getOrderNumber() {
        return this.orderNumber;
    }

    /** Mutator for order number. */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /** Accessor for the brand name. */
    public String getBrandName() {
        return brandName;
    }

    /** Mutator for the brand name. */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
