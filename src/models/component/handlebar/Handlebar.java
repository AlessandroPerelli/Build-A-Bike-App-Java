/**
 * Handlebar.java
 *
 * Handlebar class model.
 */

package models.component.handlebar;

import models.component.Component;

import java.math.BigDecimal;

public class Handlebar extends Component {
    private HandlebarType type;

    /**
     * Constructor with parameters.
     *
     * @param serialNumber
     * @param componentName
     * @param brandName
     * @param cost
     * @param stock
     * @param type
     */
    public Handlebar(
            String serialNumber, String componentName, String brandName,
            BigDecimal cost, int stock, HandlebarType type
    ) {
        super(serialNumber, componentName, brandName, cost, stock);
        this.type = type;
    }

    /** Accessor for handlebar type. */
    public HandlebarType getType() {
        return this.type;
    }

    /** Mutator for handlebar type. */
    public void setType(HandlebarType type) {
        this.type = type;
    }

    /** Handlebar product description. */
    @Override
    public String toString() {
        String outputStr = "Handlebar name: " + this.getComponentName() + "\n" +
                           "Brand name: " + this.getBrandName() + "\n" +
                           "Handlebar type: " + String.valueOf(this.getType()).toLowerCase() + "\n" +
                           "Cost: " + this.getCost() + " GBP";
        return outputStr;
    }
}
