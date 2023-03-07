/**
 * PairOfWheels.java
 *
 * PairOfWheels class model.
 */

package models.component.pairofwheels;

import models.component.Component;

import java.math.BigDecimal;

public class PairOfWheels extends Component {
    private BigDecimal diameter;
    private TyreType tyreType;
    private BrakeType brakeType;

    /**
     * Constructor with parameters. The 'stock' field with value 1
     * refers to 1 pair of wheels, so technically 2 wheels.
     *
     * @param serialNumber
     * @param pairName
     * @param brandName
     * @param cost
     * @param stock
     * @param diameter
     * @param tyreType
     * @param brakeType
     */
    public PairOfWheels(
            String serialNumber, String pairName, String brandName, BigDecimal cost,
            int stock, BigDecimal diameter, TyreType tyreType, BrakeType brakeType
    ) {
        super(serialNumber, pairName, brandName, cost, stock);
        this.diameter = diameter;
        this.tyreType = tyreType;
        this.brakeType = brakeType;
    }

    /** Accessor for diameter. */
    public BigDecimal getDiameter() {
        return this.diameter;
    }

    /** Mutator for diameter. */
    public void setDiameter(BigDecimal diameter) {
        this.diameter = diameter;
    }

    /** Accessor for tyre type. */
    public TyreType getTyreType() {
        return this.tyreType;
    }

    /** Mutator for tyre type. */
    public void setTyreType(TyreType tyreType) {
        this.tyreType = tyreType;
    }

    /** Accessor for brake type. */
    public BrakeType getBrakeType() {
        return this.brakeType;
    }

    /** Mutator for brake type. */
    public void setBrakeType(BrakeType brakeType) {
        this.brakeType = brakeType;
    }

    /** Pair of wheels product description. */
    @Override
    public String toString() {
        String outputStr = "Pair of wheels name: " + this.getComponentName() + "\n" +
                           "Brand name: " + this.getBrandName() + "\n" +
                           "Tyre type: " + String.valueOf(this.getTyreType()).toLowerCase() + "\n" +
                           "Brake type: " + String.valueOf(this.getBrakeType()).toLowerCase() + "\n" +
                           "Cost: " + this.getCost() + " GBP";
        return outputStr;
    }
}
