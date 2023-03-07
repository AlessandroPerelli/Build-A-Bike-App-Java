package gui.staff.stock;

import database.controllers.component.HandlebarController;
import exceptions.ComponentAlreadyExistsException;
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

public class HandlebarStock extends AbstractFrame {

    private final Staff staff;
    private JPanel mainPanel;
    private JPanel topMenuPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    private JButton btnAdd;
    private JButton btnBack;
    private JTextField tfSerialNumber;
    private JTextField tfName;
    private JTextField tfBrand;
    private JTextField tfCost;
    private JTextField tfStock;
    private JComboBox comboType;
    private JButton btnLogout;
    private JLabel lblSerialNumber;
    private JLabel lblName;
    private JLabel lblBrand;
    private JLabel lblType;
    private JLabel lblCost;
    private JLabel lblStock;

    private HandlebarController hController= new HandlebarController();

    public HandlebarStock(Staff staff) {
        this.staff = staff;

        add(mainPanel);
        setTitle("Add Stock");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        generateCombos();

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new handlebar record.
                if (checkDetailsCompleteness(
                        tfSerialNumber.getText(), tfName.getText(), tfBrand.getText(),
                        tfCost.getText(), tfStock.getText()
                    )) {
                    try {
                        hController.createComponent(new Handlebar(
                                tfSerialNumber.getText(),
                                tfName.getText(),
                                tfBrand.getText(),
                                new BigDecimal(tfCost.getText()),
                                Integer.parseInt(tfStock.getText()),
                                HandlebarType.valueOf(comboType.getSelectedItem().toString())
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
        for (HandlebarType type : HandlebarType.values()) {
            comboType.addItem(type.toString());
        }
    }

    /**
     * Checks that all fields are completed
     *
     * @param serialNumber
     * @param name
     * @param brand
     * @param cost
     * @param stock
     * @return
     */
    private boolean checkDetailsCompleteness(
            String serialNumber, String name, String brand, String cost,
            String stock
    ) {

        if (
                serialNumber.equals("") ||
                        name.equals("") ||
                        brand.equals("") ||
                        cost.equals("") ||
                        stock.equals("")
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
        btnLogout.setFont(f3);
        btnBack.setFont(f3);
        btnAdd.setFont(f3);
        lblCost.setFont(f3);
        lblName.setFont(f3);
        lblStock.setFont(f3);
        lblType.setFont(f3);
        lblBrand.setFont(f3);
        lblSerialNumber.setFont(f3);
        tfBrand.setFont(f3);
        tfCost.setFont(f3);
        tfStock.setFont(f3);
        tfName.setFont(f3);
        tfSerialNumber.setFont(f3);
        comboType.setFont(f3);
    }
}
