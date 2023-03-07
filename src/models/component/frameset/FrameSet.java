/**
 * FrameSet.java
 *
 * FrameSet class model.
 */

package models.component.frameset;

import models.component.Component;

import java.math.BigDecimal;

public class FrameSet extends Component {
    private BigDecimal size;
    private String forkSetName;
    private String gearSetName;
    private boolean hasShocks;

    /**
     * Constructor with parameters.
     *
     * @param serialNumber
     * @param componentName
     * @param brandName
     * @param cost
     * @param stock
     * @param forkSetName
     * @param gearSetName
     * @param size
     * @param hasShocks
     */
    public FrameSet(
            String serialNumber, String componentName, String brandName, BigDecimal cost, int stock,
            String forkSetName, String gearSetName, BigDecimal size, boolean hasShocks
    ) {
        super(serialNumber, componentName, brandName, cost, stock);
        this.forkSetName = forkSetName;
        this.gearSetName = gearSetName;
        this.size = size;
        this.hasShocks = hasShocks;
    }

    /** Accessor for size. */
    public BigDecimal getSize() {
        return this.size;
    }

    /** Mutator for size. */
    public void setSize(BigDecimal size) {
        this.size = size;
    }

    /** Accessor for fork set name. */
    public String getForkSetName() {
        return this.forkSetName;
    }

    /** Mutator for fork set name. */
    public void setForkSetName(String forkSetName) {
        this.forkSetName = forkSetName;
    }

    /** Accessor for gear set name. */
    public String getGearSetName() {
        return this.gearSetName;
    }

    /** Mutator for gear set name. */
    public void setGearSetName(String gearSetName) {
        this.gearSetName = gearSetName;
    }

    /** Accessor for having shocks. */
    public boolean isHasShocks() {
        return this.hasShocks;
    }

    /** Mutator for having shocks. */
    public void setHasShocks(boolean hasShocks) {
        this.hasShocks = hasShocks;
    }

    /** Frame-set product description. */
    @Override
    public String toString() {
        String shocksIncluded = (this.isHasShocks() ? "Yes" : "No");
        String outputStr = "Frame-set name: " + this.getComponentName() + "\n" +
                           "Brand name: " + this.getBrandName() + "\n" +
                           "Fork-set name: " + this.getForkSetName() + "\n" +
                           "Gear-set name: " + this.getGearSetName() + "\n" +
                           "Shocks included: " + shocksIncluded + "\n" +
                           "Cost: " + this.getCost() + " GBP";
        return outputStr;
    }
}
