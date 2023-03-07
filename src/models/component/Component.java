/**
 * Component.java
 *
 * Abstract Component class extended by each specific component type.
 */

package models.component;

import java.math.BigDecimal;

public abstract class Component {
    protected String serialNumber;
    protected String componentName;
    protected String brandName;
    protected BigDecimal cost;
    protected int stock;

    /**
     * Constructor with parameters.
     *
     * @param serialNumber
     * @param componentName
     * @param brandName
     * @param cost
     * @param stock
     */
    public Component(String serialNumber, String componentName, String brandName, BigDecimal cost, int stock) {
        this.serialNumber = serialNumber;
        this.componentName = componentName;
        this.brandName = brandName;
        this.cost = cost;
        this.stock = stock;
    }

    /** Accessor for serial number. */
    public String getSerialNumber() {
        return this.serialNumber;
    }

    /** Mutator for serial number. */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /** Accessor for component name. */
    public String getComponentName() {
        return this.componentName;
    }

    /** Mutator for component name. */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /** Accessor for brand name. */
    public String getBrandName() {
        return this.brandName;
    }

    /** Mutator for brand name. */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /** Accessor for cost. */
    public BigDecimal getCost() {
        return this.cost;
    }

    /** Mutator for cost. */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    /** Accessor for stock. */
    public int getStock() {
        return this.stock;
    }

    /** Mutator for stock. */
    public void setStock(int stock) {
        this.stock = stock;
    }
}
