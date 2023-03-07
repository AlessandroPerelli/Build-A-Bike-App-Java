package gui.staff.stock;

import database.controllers.component.FrameSetController;
import exceptions.ComponentAlreadyExistsException;
import exceptions.InputTooLongException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.component.frameset.FrameSet;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

public class FramesetStock extends AbstractFrame {
    private final Staff staff;
    private JPanel mainPanel;
    private JButton btnBack;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JTextField tfSerialNumber;
    private JTextField tfName;
    private JTextField tfBrand;
    private JTextField tfCost;
    private JTextField tfSize;
    private JTextField tfForks;
    private JTextField tfGear;
    private JButton btnAdd;
    private JTextField tfStock;
    private JCheckBox checkBoxShocks;
    private JButton btnLogout;
    private JLabel lblSerialNumber;
    private JLabel lblName;
    private JLabel lblBrand;
    private JLabel lblForks;
    private JLabel lblGear;
    private JLabel lblSize;
    private JLabel lblShocks;
    private JLabel lblCost;
    private JLabel lblStock;
    private JButton Add;

    private FrameSetController fController = new FrameSetController();

    public FramesetStock(Staff staff) {
        this.staff = staff;

        add(mainPanel);
        setTitle("Add Stock");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new frame-set record.
                if (checkDetailsCompleteness(
                        tfSerialNumber.getText(), tfName.getText(),
                        tfBrand.getText(), tfCost.getText(), tfStock.getText(),
                        tfGear.getText(), tfSize.getText())
                ) {
                    try {
                        fController.createComponent(new FrameSet(
                                tfSerialNumber.getText(),
                                tfName.getText(),
                                tfBrand.getText(),
                                new BigDecimal(tfCost.getText()),
                                Integer.parseInt(tfStock.getText()),
                                tfForks.getText(), tfGear.getText(),
                                new BigDecimal(tfSize.getText()),
                                checkBoxShocks.isSelected()
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

    /**
     * Checks that all fields are completed
     *
     * @param serialNumber
     * @param name
     * @param brand
     * @param cost
     * @param stock
     * @param gear
     * @param size
     * @return
     */
    private boolean checkDetailsCompleteness(
            String serialNumber, String name, String brand, String cost,
            String stock, String gear, String size
    ) {
        if (
                serialNumber.equals("") ||
                        name.equals("") ||
                        brand.equals("") ||
                        cost.equals("") ||
                        stock.equals("") ||
                        gear.equals("") ||
                        size.equals("")
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
        lblBrand.setFont(f3);
        lblForks.setFont(f3);
        lblGear.setFont(f3);
        lblName.setFont(f3);
        lblSerialNumber.setFont(f3);
        lblShocks.setFont(f3);
        lblSize.setFont(f3);
        lblStock.setFont(f3);
        tfBrand.setFont(f3);
        tfCost.setFont(f3);
        tfForks.setFont(f3);
        tfStock.setFont(f3);
        tfGear.setFont(f3);
        tfName.setFont(f3);
        tfSerialNumber.setFont(f3);
        tfSize.setFont(f3);
        checkBoxShocks.setFont(f3);
    }
}
