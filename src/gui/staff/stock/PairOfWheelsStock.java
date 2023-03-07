package gui.staff.stock;

import database.controllers.component.PairOfWheelsController;
import exceptions.ComponentAlreadyExistsException;
import exceptions.InputTooLongException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.component.pairofwheels.BrakeType;
import models.component.pairofwheels.TyreType;
import models.component.pairofwheels.PairOfWheels;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

public class PairOfWheelsStock extends AbstractFrame {
    private final Staff staff;
    private JPanel mainPanel;
    private JButton btnBack;
    private JButton btnAdd;
    private JTextField tfSerialNumber;
    private JTextField tfName;
    private JTextField tfBrand;
    private JComboBox comboBrake;
    private JComboBox comboTyre;
    private JTextField tfCost;
    private JTextField tfDiameter;
    private JTextField tfStock;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnLogout;
    private JLabel lblSerialNumber;
    private JLabel lblName;
    private JLabel lblBrand;
    private JLabel lblBrake;
    private JLabel lblTyre;
    private JLabel lblDiameter;
    private JLabel lblStock;
    private JLabel lblCost;
    private PairOfWheelsController powController = new PairOfWheelsController();

    public PairOfWheelsStock(Staff staff) {
        this.staff = staff;
        
        add(mainPanel);
        setTitle("Add Stock");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        generateCombos();

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new wheel record.
                if (checkDetailsCompleteness(
                        tfSerialNumber.getText(), tfName.getText(), tfBrand.getText(),
                        tfCost.getText(), tfStock.getText(), tfDiameter.getText())
                ) {
                    try {
                        powController.createComponent(new PairOfWheels(
                                tfSerialNumber.getText(),
                                tfName.getText(),
                                tfBrand.getText(),
                                new BigDecimal(tfCost.getText()),
                                Integer.parseInt(tfStock.getText()),
                                new BigDecimal(tfDiameter.getText()),
                                TyreType.valueOf(comboTyre.getSelectedItem().toString()),
                                BrakeType.valueOf(comboBrake.getSelectedItem().toString())
                        ));

                    } catch (ComponentAlreadyExistsException ex) {
                        showPopup(ex.getMessage());
                    } catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    } catch (Exception ex) {
                        showPopup("An external error occurred. Please try again!");
                    }

                    StaffStock staffStock = new StaffStock(staff);
                    staffStock.setVisible(true);
                    dispose();
                } else {
                    showPopup("The details are incomplete, or have incorrect format. " +
                            "\nPlease complete the missing fields and try again!");
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffStock staffStock = new StaffStock(staff);
                staffStock.setVisible(true);
                dispose();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true);
                dispose();
            }
        });
    }

    private void generateCombos() {
        for (BrakeType type : BrakeType.values()) {
            comboBrake.addItem(type.toString());
        }

        for (TyreType type : TyreType.values()) {
            comboTyre.addItem(type.toString());
        }
    }

    /**
     * Checks that are fields are completed
     *
     * @param serialNumber
     * @param name
     * @param brand
     * @param cost
     * @param stock
     * @param diameter
     * @return
     */
    private boolean checkDetailsCompleteness(
            String serialNumber, String name, String brand, String cost,
            String stock, String diameter
    ) {
        if (
                serialNumber.equals("") ||
                        name.equals("") ||
                        brand.equals("") ||
                        cost.equals("") ||
                        stock.equals("") ||
                        diameter.equals("")
        ) {
            return false;
        } else {
            try {
                Integer.parseInt(stock);
                return true;
            } catch (NumberFormatException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }
    }

    public void setFonts() {
        btnAdd.setFont(f3);
        btnBack.setFont(f3);
        btnLogout.setFont(f3);
        lblBrake.setFont(f3);
        lblCost.setFont(f3);
        lblName.setFont(f3);
        lblStock.setFont(f3);
        lblDiameter.setFont(f3);
        lblBrand.setFont(f3);
        lblSerialNumber.setFont(f3);
        lblTyre.setFont(f3);
        lblBrake.setFont(f3);
        tfBrand.setFont(f3);
        tfCost.setFont(f3);
        tfStock.setFont(f3);
        tfDiameter.setFont(f3);
        tfName.setFont(f3);
        tfSerialNumber.setFont(f3);
        comboBrake.setFont(f3);
        contentPanel.setFont(f3);
    }
}
