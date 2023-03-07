package gui.staff.stock;

import database.controllers.component.PairOfWheelsController;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import gui.AbstractFrame;
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

public class UpdatePairOfWheels extends AbstractFrame {
    private PairOfWheels pairOfWheels;
    private final Staff staff;
    private JPanel mainPanel;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnLogout;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JTextField tfName;
    private JTextField tfDiameter;
    private JTextField tfCost;
    private JTextField tfStock;
    private JComboBox comboBrake;
    private JComboBox comboTyre;
    private JLabel lblSerialNumber;
    private JLabel lblBrand;
    private JButton btnBack;
    private JLabel lblName;
    private JLabel lblBrake;
    private JLabel lblDiameter;
    private JLabel lblStock;
    private JLabel lblTyre;
    private JLabel lblCost;
    private PairOfWheelsController pairOfWheelsController;

    public UpdatePairOfWheels(PairOfWheels pairOfWheels, Staff staff) {
        this.pairOfWheels = pairOfWheels;
        this.staff = staff;

        add(mainPanel);
        setTitle("Update");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        String serialNumber = pairOfWheels.getSerialNumber();
        String componentName = pairOfWheels.getComponentName();
        String brandName = pairOfWheels.getBrandName();
        BigDecimal cost = pairOfWheels.getCost();
        int stock = pairOfWheels.getStock();
        BigDecimal diameter = pairOfWheels.getDiameter();
        TyreType tyreType = pairOfWheels.getTyreType();
        BrakeType brakeType = pairOfWheels.getBrakeType();

        lblSerialNumber.setText("Serial No. " + serialNumber);
        lblBrand.setText("Brand: " + brandName);
        tfName .setText(componentName);
        tfCost.setText(cost.toString());
        tfStock.setText(String.valueOf(stock));
        tfDiameter.setText(String.valueOf(diameter));
        for (TyreType tyre : TyreType.values()) {
            comboTyre.addItem(tyre.toString());
        }
        comboTyre.setSelectedItem(tyreType);

        for (BrakeType brake : BrakeType.values()) {
            comboBrake.addItem(brake.toString());
        }
        comboBrake.setSelectedItem(brakeType);

        pairOfWheelsController = new PairOfWheelsController();

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure?",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    //Tries to update the wheel with the new given information
                    try {
                        pairOfWheelsController.updateComponent(new PairOfWheels(
                                serialNumber,
                                tfName.getText(),
                                brandName,
                                new BigDecimal(tfCost.getText()),
                                Integer.parseInt(String.valueOf(tfStock.getText())),
                                new BigDecimal(String.valueOf(tfDiameter.getText())),
                                TyreType.valueOf(comboTyre.getSelectedItem().toString()),
                                BrakeType.valueOf(comboBrake.getSelectedItem().toString()))
                        );

                        StaffStock staffStock = new StaffStock(staff);
                        staffStock.setVisible(true);
                        dispose();
                    } catch (ComponentNotFoundException ex) {
                        showPopup(ex.getMessage());
                    } catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    } catch (Exception ex) {
                        showPopup("An external error occurred. Please try again!");
                    }
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? This cannot be recovered",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        pairOfWheelsController.deleteComponent(pairOfWheels, "PairsOfWheels");
                    } catch (ComponentNotFoundException ex) {
                        showPopup(ex.getMessage());
                    } catch (Exception ex) {
                        showPopup("An external error occurred. Please try again!");
                    }

                    StaffStock staffStock = new StaffStock(staff);
                    staffStock.setVisible(true);
                    dispose();
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
    }

    public void setFonts() {
        btnBack.setFont(f3);
        btnUpdate.setFont(f3);
        btnLogout.setFont(f3);
        btnDelete.setFont(f3);
        lblBrake.setFont(f3);
        lblCost.setFont(f3);
        lblName.setFont(f3);
        lblDiameter.setFont(f3);
        lblTyre.setFont(f3);
        lblBrand.setFont(f1);
        lblSerialNumber.setFont(f1);
        lblStock.setFont(f3);
        tfCost.setFont(f3);
        tfDiameter.setFont(f3);
        tfName.setFont(f3);
        tfStock.setFont(f3);
        comboBrake.setFont(f3);
        comboTyre.setFont(f3);
    }
}
