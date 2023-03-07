package gui.bikes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import database.controllers.component.FrameSetController;
import database.controllers.component.HandlebarController;
import database.controllers.component.PairOfWheelsController;
import exceptions.IncompleteBicycleException;
import exceptions.NoComponentForFilterException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.bicycle.Bicycle;
import models.component.Component;
import models.component.frameset.FrameSet;
import models.component.handlebar.Handlebar;
import models.component.handlebar.HandlebarType;
import models.component.pairofwheels.BrakeType;
import models.component.pairofwheels.TyreType;
import models.component.pairofwheels.PairOfWheels;

public class BikeBuilder extends AbstractFrame {
    private JPanel mainPanel;
    private JButton btnHome;
    private JPanel topMenuPanel;
    private JPanel contentPanel;
    private JButton btnOrder;
    private JLabel lblTotal;
    private JPanel bottomPanel;
    private JComboBox comboFrameSetName;
    private JComboBox comboFrameSetSize;
    private JComboBox comboFrameSetShocks;
    private JComboBox comboHandlebarName;
    private JComboBox comboHandlebarType;
    private JComboBox comboPairOfWheelsName;
    private JComboBox comboWheelBrakeType;
    private JComboBox comboWheelTyreType;
    private JComboBox comboWheelDiameter;
    private JButton btnFrameSetDetails;
    private JButton btnHandlebarDetails;
    private JButton btnPairOfWheelsDetails;
    private ArrayList<FrameSet> frameSets;
    private ArrayList<Handlebar> handlebars;
    private ArrayList<PairOfWheels> pairOfWheels;
    private final FrameSetController fsController = new FrameSetController();
    private final HandlebarController hController = new HandlebarController();
    private final PairOfWheelsController powController = new PairOfWheelsController();
    private FrameSet currSelectedFrameSet;
    private Handlebar currSelectedHandlebar;
    private PairOfWheels currSelectedPairOfWheels;
    private BigDecimal currCost;
    private JTextField tfBikeName;
    private JLabel lblBikeName;
    private JLabel lblFrameset;
    private JLabel lblHandlebar;
    private JLabel lblWheels;

    public BikeBuilder(){
        add(mainPanel);
        setTitle("Build Your Bike");
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Generate the ComboBoxes for all component types.
        try {
            frameSets = fsController.readAllComponentsOfType(false);
            handlebars = hController.readAllComponentsOfType(false);
            pairOfWheels = powController.readAllComponentsOfType(false);
            generateDefaultCombos();
        }
        catch (Exception e) {
            showPopup("An error occurred. Please try to enter the form again.");
        }

        repaint();

        btnOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bicycleName = String.valueOf(tfBikeName.getText());
                try {
                    Bicycle bicycle = buildBicycle(bicycleName);
                    BikeOrder bikeOrder = new BikeOrder(bicycle);
                    bikeOrder.setVisible(true);
                    dispose();
                }
                catch (IncompleteBicycleException ex) {
                    showPopup(ex.getMessage());
                }
                catch (Exception ex) {
                    showPopup("An unexpected error occurred. Please try again!");
                }
            }
        });

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? All current progress of your bike will be discarded.",
                        "Warning!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    MainDashboard mainDashboard = new MainDashboard();
                    mainDashboard.setVisible(true);
                    dispose();
                }
            }
        });

        // Set up the details buttons for the components.
        btnFrameSetDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currSelectedFrameSet != null) {
                    JOptionPane.showMessageDialog(
                            null,
                            currSelectedFrameSet.toString()
                    );
                }
            }
        });
        btnHandlebarDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currSelectedHandlebar != null) {
                    JOptionPane.showMessageDialog(
                            null,
                            currSelectedHandlebar.toString()
                    );
                }
            }
        });
        btnPairOfWheelsDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currSelectedPairOfWheels != null) {
                    JOptionPane.showMessageDialog(
                            null,
                            currSelectedPairOfWheels.toString()
                    );
                }
            }
        });

        // Update frame-set results according to size and shocks inclusion filters.
        comboFrameSetName.addItemListener((new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (comboFrameSetName.getSelectedIndex() != -1) {
                    currSelectedFrameSet = frameSets.get(comboFrameSetName.getSelectedIndex());
                    calculateCurrCost();
                }
            }
        }));
        comboFrameSetSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFrameSetCombo(
                        String.valueOf(comboFrameSetSize.getSelectedItem()),
                        String.valueOf(comboFrameSetShocks.getSelectedItem())
                );
                calculateCurrCost();
            }
        });
        comboFrameSetShocks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFrameSetCombo(
                        String.valueOf(comboFrameSetSize.getSelectedItem()),
                        String.valueOf(comboFrameSetShocks.getSelectedItem())
                );
                calculateCurrCost();
            }
        });

        // Update handlebar results according to the type filter.
        comboHandlebarName.addItemListener((new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (comboHandlebarName.getSelectedIndex() != -1) {
                    currSelectedHandlebar = handlebars.get(comboHandlebarName.getSelectedIndex());
                    calculateCurrCost();
                }
            }
        }));
        comboHandlebarType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateHandlebarCombo(
                        String.valueOf(comboHandlebarType.getSelectedItem())
                );
                calculateCurrCost();
            }
        });

        // Update pair of wheels results according to the brake type, tyre type and diameter filters.
        comboPairOfWheelsName.addItemListener((new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (comboPairOfWheelsName.getSelectedIndex() != -1) {
                    currSelectedPairOfWheels = pairOfWheels.get(comboPairOfWheelsName.getSelectedIndex());
                    calculateCurrCost();
                }
            }
        }));
        comboWheelDiameter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWheelCombo(
                        String.valueOf(comboWheelDiameter.getSelectedItem()),
                        String.valueOf(comboWheelTyreType.getSelectedItem()),
                        String.valueOf(comboWheelBrakeType.getSelectedItem())
                );
            }
        });
        comboWheelTyreType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWheelCombo(
                        String.valueOf(comboWheelDiameter.getSelectedItem()),
                        String.valueOf(comboWheelTyreType.getSelectedItem()),
                        String.valueOf(comboWheelBrakeType.getSelectedItem())
                );
                calculateCurrCost();
            }
        });
        comboWheelBrakeType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWheelCombo(
                        String.valueOf(comboWheelDiameter.getSelectedItem()),
                        String.valueOf(comboWheelTyreType.getSelectedItem()),
                        String.valueOf(comboWheelBrakeType.getSelectedItem())
                );
                calculateCurrCost();
            }
        });
    }

    /**
     * Generate default combos for all selections.
     *
     * @throws SQLException
     */
    public void generateDefaultCombos() throws SQLException {
        // Initialize result and filter combos for each type of the component.
        initFrameSetCombos();
        initHandlebarCombos();
        initWheelCombos();
        calculateCurrCost();
    }

    /**
     * Initialize the frame-set combos and their filters.
     *
     * @throws SQLException
     */
    private void initFrameSetCombos() throws SQLException {
        // Set up the filter for size.
        ArrayList<BigDecimal> sizes = fsController.getUniqueSizeSet();
        comboFrameSetSize.addItem("SIZE (CM): ANY");
        for (BigDecimal s : sizes) {
            comboFrameSetSize.addItem(String.valueOf(s));
        }

        // Set up the filter for having shocks.
        comboFrameSetShocks.addItem("SHOCKS: ANY");
        comboFrameSetShocks.addItem("SHOCKS: YES");
        comboFrameSetShocks.addItem("SHOCKS: NO");

        // Set up the currently selected frame-set object.
        currSelectedFrameSet = frameSets.get(0);

        // Set up frame-set results.
        for (FrameSet f : frameSets) {
            comboFrameSetName.addItem(
                    prettifyComponentDescription(f)
            );
        }
    }

    /**
     * Initialize handlebar combos and their filters.
     *
     * @throws SQLException
     */
    private void initHandlebarCombos() throws SQLException {
        // Set up the handlebar filters for type.
        HandlebarType[] handlebarTypes = HandlebarType.values();
        comboHandlebarType.addItem("HANDLEBAR TYPE: ANY");
        for (HandlebarType ht : handlebarTypes) {
            comboHandlebarType.addItem(String.valueOf(ht));
        }

        // Set up the currently selected handlebar object.
        currSelectedHandlebar = handlebars.get(0);

        // Set up handlebar results.
        for (Handlebar h : handlebars) {
            comboHandlebarName.addItem(
                    prettifyComponentDescription(h)
            );
        }
    }

    /**
     * Initialize pair of wheels combos and their filters.
     *
     * @throws SQLException
     */
    private void initWheelCombos() throws SQLException {
        // Set up brake type filters.
        BrakeType[] brakeTypes = BrakeType.values();
        comboWheelBrakeType.addItem("BRAKE TYPE: ANY");
        for (BrakeType bt : brakeTypes) {
            comboWheelBrakeType.addItem(String.valueOf(bt));
        }

        // Set up tyre type filters.
        TyreType[] tyreTypes = TyreType.values();
        comboWheelTyreType.addItem("TYRE TYPE: ANY");
        for (TyreType tt : tyreTypes) {
            comboWheelTyreType.addItem(String.valueOf(tt));
        }

        // Set up diameter filters.
        ArrayList<BigDecimal> diameters = powController.getUniqueDiameterSet();
        comboWheelDiameter.addItem("DIAMETER (CM): ANY");
        for (BigDecimal d : diameters) {
            comboWheelDiameter.addItem(String.valueOf(d));
        }

        // Set up the currently selected pair of wheels object.
        currSelectedPairOfWheels = pairOfWheels.get(0);

        // Set up pair of wheels results.
        for (PairOfWheels pow : pairOfWheels) {
            comboPairOfWheelsName.addItem(
                    prettifyComponentDescription(pow)
            );
        }
    }

    /**
     * Dynamically update frame-set results according to applied filters.
     *
     * @param fSizeStr - size filter (as String).
     * @param fHasShocksStr - has-shocks filter (as String).
     */
    public void updateFrameSetCombo(String fSizeStr, String fHasShocksStr) {
        comboFrameSetName.removeAllItems();

        // Convert filters to suitable types.
        BigDecimal fSize = (fSizeStr.equals("SIZE (CM): ANY") ? null : new BigDecimal(fSizeStr));
        Boolean fHasShocks;
        if (fHasShocksStr.equals("SHOCKS: YES")) {
            fHasShocks = true;
        } else if ((fHasShocksStr.equals("SHOCKS: NO"))) {
            fHasShocks = false;
        } else {
            fHasShocks = null;
        }

        try {
            // If no filters, read all instances following the user's access.
            if (fHasShocks == null && fSize == null) {
                frameSets = fsController.readAllComponentsOfType(false);
            }
            // Otherwise, call the specific method for applying the filters.
            else {
                frameSets = fsController.filterFrameSets(fSize, fHasShocks);
            }

            // Set up the currently selected frame-set.
            currSelectedFrameSet = frameSets.get(0);

            for (FrameSet f : frameSets) {
                comboFrameSetName.addItem(
                        prettifyComponentDescription(f)
                );
            }
        }
        catch (NoComponentForFilterException e) {
            currSelectedFrameSet = null;
            calculateCurrCost();
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage()
            );
            showPopup(e.getMessage());
        }
        catch (Exception e) {
            showPopup("An unexpected error occurred. Please try again!");
        }
    }

    /**
     * Dynamically update handlebar dropdown results according to applied filters.
     *
     * @param hType - filter selected by the user.
     */
    public void updateHandlebarCombo(String hType) {
        comboHandlebarName.removeAllItems();

        try {
            if (hType.equals("HANDLEBAR TYPE: ANY")) {
                 handlebars = hController.readAllComponentsOfType(false);
            }
            else {
                handlebars = hController.filterHandlebars(HandlebarType.valueOf(hType));
            }

            // Set up the currently selected handlebar.
            currSelectedHandlebar = handlebars.get(0);

            for (Handlebar h : handlebars) {
                comboHandlebarName.addItem(
                        prettifyComponentDescription(h)
                );
            }
        }
        catch (NoComponentForFilterException e) {
            currSelectedHandlebar = null;
            calculateCurrCost();
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage()
            );
        }
        catch (Exception e) {
            showPopup("An unexpected error occurred. Please try again!");
        }
    }

    /**
     * Dynamically update pair of wheels results according to applied filters.
     *
     * @param fDiameterStr - diameter filter (as String).
     * @param fTyreTypeStr - tyre type filter (as String).
     * @param fBrakeTypeStr - brake type filter (as String).
     */
    public void updateWheelCombo(String fDiameterStr, String fTyreTypeStr, String fBrakeTypeStr) {
        comboPairOfWheelsName.removeAllItems();

        // Convert filters to suitable types.
        BigDecimal fDiameter = (
                fDiameterStr.equals("DIAMETER (CM): ANY") ? null : new BigDecimal(fDiameterStr)
        );
        TyreType fTyreType = (
                fTyreTypeStr.equals("TYRE TYPE: ANY") ? null : TyreType.valueOf(fTyreTypeStr)
        );
        BrakeType fBrakeType = (
                fBrakeTypeStr.equals("BRAKE TYPE: ANY") ? null : BrakeType.valueOf(fBrakeTypeStr)
        );

        try {
            // If no filters, read all instances following the user's access.
            if (fDiameter == null && fTyreType == null && fBrakeType == null) {
                pairOfWheels = powController.readAllComponentsOfType(false);
            }
            // Otherwise, call the specific method for applying the filters.
            else {
                pairOfWheels = powController.filterWheels(fTyreType, fBrakeType, fDiameter);
            }

            // Set up the currently selected wheel.
            currSelectedPairOfWheels = pairOfWheels.get(0);

            for (PairOfWheels pow : pairOfWheels) {
                comboPairOfWheelsName.addItem(
                        prettifyComponentDescription(pow)
                );
            }
        }
        catch (NoComponentForFilterException e) {
            currSelectedPairOfWheels = null;
            calculateCurrCost();
            showPopup(e.getMessage());
        }
        catch (Exception e) {
            showPopup("An unexpected error occurred. Please try again!");
        }
    }

    /**
     * Prettify a component's description that appears in the combo using the format:
     * "componentName - brandName (cost GBP)"
     *
     * @param component - instance to be added into the combo box.
     * @return
     */
    private String prettifyComponentDescription(Component component) {
        String prettifiedDescriptionTemplate = "%s - %s (%s GBP)";
        String prettifiedResult = String.format(
                prettifiedDescriptionTemplate,
                component.getComponentName(),
                component.getBrandName(),
                component.getCost()
        );
        return prettifiedResult;
    }

    /**
     * Dynamically calculate the bike cost, which is determined by the
     * currently selected items. Note: wheel cost has to be multiplied by 2.
     */
    private void calculateCurrCost() {
        String totalCostStr = "Total: %s GBP";

        if (currSelectedHandlebar == null || currSelectedPairOfWheels == null || currSelectedFrameSet == null) {
            currCost = BigDecimal.valueOf(0);
            lblTotal.setText(String.format(totalCostStr, currCost));
        }
        else {
            BigDecimal totalCost = new BigDecimal(0);
            BigDecimal[] componentCosts = {
                    currSelectedFrameSet.getCost(),
                    currSelectedHandlebar.getCost(),
                    currSelectedPairOfWheels.getCost()
            };

            for (BigDecimal cc : componentCosts) {
                totalCost = totalCost.add(cc);
            }

            currCost = totalCost;
            lblTotal.setText(String.format(totalCostStr, currCost));
        }
    }

    /**
     * Creates a new instance of the bicycle that will create database record and
     * will be later passed to create Order.
     *
     * @param customBicycleName - name of the created bicycle.
     * @return bicycle object based on component selection etc.
     * @throws IncompleteBicycleException
     */
    private Bicycle buildBicycle(String customBicycleName) throws IncompleteBicycleException {
        if (
                currSelectedFrameSet == null ||
                        currSelectedPairOfWheels == null ||
                        currSelectedHandlebar == null ||
                        customBicycleName.equals("")
        ) {
            throw new IncompleteBicycleException();
        } else {
            Bicycle bicycle = new Bicycle(
                    customBicycleName,
                    currSelectedHandlebar,
                    currSelectedFrameSet,
                    currSelectedPairOfWheels,
                    null
            );
            return bicycle;
        }
    }

    public void setFonts() {
        btnOrder.setFont(f3);
        btnHome.setFont(f3);
        btnHandlebarDetails.setFont(f3);
        btnPairOfWheelsDetails.setFont(f3);
        btnFrameSetDetails.setFont(f3);
        lblTotal.setFont(f3);
        lblWheels.setFont(f3);
        lblBikeName.setFont(f3);
        lblHandlebar.setFont(f3);
        lblFrameset.setFont(f3);
        comboFrameSetName.setFont(f3);
        comboFrameSetShocks.setFont(f3);
        comboHandlebarType.setFont(f3);
        comboHandlebarName.setFont(f3);
        comboFrameSetSize.setFont(f3);
        comboPairOfWheelsName.setFont(f3);
        comboWheelBrakeType.setFont(f3);
        comboWheelDiameter.setFont(f3);
        comboWheelTyreType.setFont(f3);
        tfBikeName.setFont(f3);
    }
}
