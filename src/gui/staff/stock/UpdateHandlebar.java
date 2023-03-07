package gui.staff.stock;

import database.controllers.component.HandlebarController;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.component.handlebar.Handlebar;
import models.component.handlebar.HandlebarType;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

public class UpdateHandlebar extends AbstractFrame {

    private final Staff staff;
    private Handlebar handlebar;
    private JPanel mainPanel;
    private JButton btnLogout;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JTextField tfName;
    private JTextField tfStock;
    private JTextField tfCost;
    private JComboBox comboType;
    private JLabel lblSerialNumber;
    private JLabel lblBrand;
    private JButton btnBack;
    private JLabel lblName;
    private JLabel lblType;
    private JLabel lblCost;
    private JLabel lblStock;
    private HandlebarController hController;

    public UpdateHandlebar(Handlebar handlebar, Staff staff) {
        this.handlebar = handlebar;
        this.staff = staff;
        
        add(mainPanel);
        setTitle("Update");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        String serialNumber = handlebar.getSerialNumber();
        String handlebarName = handlebar.getComponentName();
        String brandName = handlebar.getBrandName();
        HandlebarType type = handlebar.getType();
        BigDecimal cost = handlebar.getCost();
        int stock = handlebar.getStock();

        lblSerialNumber.setText("Serial No. " + serialNumber);
        lblBrand.setText("Brand: " + brandName);
        tfName.setText(handlebarName);
        for (HandlebarType hType : HandlebarType.values()) {
            comboType.addItem(type.toString());
        }
        comboType.setSelectedItem(type);
        tfCost.setText(cost.toString());
        tfStock.setText(String.valueOf(stock));

        hController = new HandlebarController();

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure?",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    //Tries to update the handlebar with the new given information
                    try {
                        hController.updateComponent(new Handlebar(
                                serialNumber,
                                tfName.getText(),
                                brandName,
                                new BigDecimal(tfCost.getText()),
                                Integer.parseInt(String.valueOf(tfStock.getText())),
                                HandlebarType.valueOf(comboType.getSelectedItem().toString())
                                )
                        );

                        StaffStock staffStock = new StaffStock(staff);
                        staffStock.setVisible(true);
                        dispose();
                    } catch (ComponentNotFoundException ex) {
                        showPopup(ex.getMessage());
                    } catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    } catch (SQLException ex) {
                        showPopup("An external error occurred. Please try again!");
                    }
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Delete DB record HERE

                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? This cannot be recovered",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        hController.deleteComponent(handlebar, "Handlebars");
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

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true);
                dispose();
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
        btnLogout.setFont(f3);
        btnUpdate.setFont(f3);
        btnDelete.setFont(f3);
        lblBrand.setFont(f1);
        lblCost.setFont(f3);
        lblName.setFont(f3);
        lblType.setFont(f3);
        lblStock.setFont(f3);
        lblSerialNumber.setFont(f1);
        tfStock.setFont(f3);
        tfCost.setFont(f3);
        tfName.setFont(f3);
        comboType.setFont(f3);
    }
}
