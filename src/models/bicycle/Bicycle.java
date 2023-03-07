/**
 * Bicycle.java
 *
 * Bicycle model class.
 */

package models.bicycle;

import database.controllers.bicycle.BicycleController;
import models.component.frameset.FrameSet;
import models.component.handlebar.Handlebar;
import models.component.pairofwheels.PairOfWheels;
import services.IdGenerator;

import java.math.BigDecimal;
import java.sql.SQLException;

public class Bicycle {
    private final static BigDecimal ASSEMBLY_COST = new BigDecimal("10.0");
    private String serialNumber;
    private String customName;
    private String brandName;
    private Handlebar handlebar;
    private FrameSet frameSet;
    private PairOfWheels pairOfWheels;

    public Bicycle(
            String customName, Handlebar handlebar, FrameSet frameSet,
            PairOfWheels pairOfWheels, String serialNumber
    ) {
        generateBrandName(frameSet, pairOfWheels);
        this.customName = customName;
        this.handlebar = handlebar;
        this.frameSet = frameSet;
        this.pairOfWheels = pairOfWheels;

        if (serialNumber == null) {
            generateSerialNumber();
        }
        else {
            this.serialNumber = serialNumber;
        }
    }

    /** Generate a unique serial number for the bicycle using the service class. */
    public void generateSerialNumber() {
        String id = IdGenerator.generateId(12);

        // Make sure that the unique ID is not repeated.
        try {
            BicycleController bc = new BicycleController();
            boolean idExists  = bc.bicycleExists(id);

            if (!idExists) {
                this.serialNumber = id;
            }
            else {
                // Recursive call to find a new unique serial number.
                // The probability of entering this part of the program
                // ever is very low but still has to be considered.
                generateSerialNumber();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Accessor for the serial number. */
    public String getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * Generate brand name of the bicycle.
     *
     * @param f - frame-set of the bicycle instance.
     * @param w - pair of wheel of the bicycle instance.
     */
    public void generateBrandName(FrameSet f, PairOfWheels w) {
        this.brandName = f.getBrandName() + " " + String.valueOf(w.getTyreType()).toLowerCase();
    }

    /** Accessor for the brand name. */
    public String getBrandName() {
        return this.brandName;
    }

    /** Accessor for the assembly cost. */
    public static BigDecimal getAssemblyCost() {
        return ASSEMBLY_COST;
    }

    /** Accessor for the bicycle name */
    public String getCustomName() {
        return this.customName;
    }

    /** Mutator for the bicycle name. */
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    /** Accessor for the frame-set. */
    public FrameSet getFrameSet() {
        return this.frameSet;
    }

    /** Mutator for the frame-set */
    public void setFrameSet(FrameSet frameSet) {
        this.frameSet = frameSet;
    }

    /** Accessor for the handlebar. */
    public Handlebar getHandlebar() {
        return this.handlebar;
    }

    /** Mutator for the handlebar. */
    public void setHandlebar(Handlebar handlebar) {
        this.handlebar = handlebar;
    }

    /** Accessor for the wheel. */
    public PairOfWheels getPairOfWheels() {
        return this.pairOfWheels;
    }

    /** Mutator for the wheel. */
    public void setWheels(PairOfWheels pairOfWheels) {
        this.pairOfWheels = pairOfWheels;
    }
}
