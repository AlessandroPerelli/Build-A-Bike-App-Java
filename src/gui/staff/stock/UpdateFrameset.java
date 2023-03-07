package gui.staff.stock;

import database.controllers.component.FrameSetController;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import gui.AbstractFrame;
import gui.MainDashboard;
import jdk.jfr.internal.tool.Main;
import models.component.frameset.FrameSet;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

public class UpdateFrameset extends AbstractFrame {
    private final Staff staff;
    private FrameSet frameSet;
    private JPanel mainPanel;
    private JButton btnLogout;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JTextField tfName;
    private JTextField tfForks;
    private JTextField tfGear;
    private JTextField tfSize;
    private JCheckBox checkShocks;
    private JTextField tfCost;
    private JTextField tfStock;
    private JLabel lblSerialNumber;
    private JLabel lblBrand;
    private JButton btnBack;
    private JLabel lblName;
    private JLabel lblForks;
    private JLabel lblSize;
    private JLabel lblCost;
    private JLabel lblGear;
    private JLabel lblShocks;
    private JLabel lblStock;
    private FrameSetController fController;

    public UpdateFrameset(FrameSet frameSet, Staff staff) {
        this.frameSet = frameSet;
        this.staff = staff;

        add(mainPanel);
        setTitle("Update");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        String serialNumber = frameSet.getSerialNumber();
        String framesetName = frameSet.getComponentName();
        String brandName = frameSet.getBrandName();
        String forksName = frameSet.getForkSetName();
        String gearsName = frameSet.getGearSetName();
        BigDecimal size = frameSet.getSize();
        Boolean hasShocks = frameSet.isHasShocks();
        BigDecimal cost = frameSet.getCost();
        int stock = frameSet.getStock();

        lblSerialNumber.setText("Serial No. " + serialNumber);
        lblBrand.setText("Brand: " + brandName);
        tfName.setText(framesetName);
        tfForks.setText(forksName);
        tfGear.setText(gearsName);
        tfSize.setText(size.toString());
        checkShocks.setText(hasShocks.toString());
        tfCost.setText(cost.toString());
        tfStock.setText(String.valueOf(stock));

        fController = new FrameSetController();

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkDetailsCompleteness(
                        tfName.getText(), tfCost.getText(),
                        tfStock.getText(), tfGear.getText(), tfSize.getText())
                ) {
                    int dialogResult = JOptionPane.showConfirmDialog(null,
                            "Are you sure?",
                            "Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (dialogResult == 0) {
                        //Tries to update the frameset with the new given information
                        try {
                            fController.updateComponent(new FrameSet(
                                    serialNumber,
                                    tfName.getText(),
                                    brandName,
                                    new BigDecimal(tfCost.getText().trim()),
                                    Integer.parseInt(tfStock.getText()),
                                    tfForks.getText(),
                                    tfGear.getText(),
                                    new BigDecimal(tfSize.getText().trim()),
                                    hasShocks.booleanValue())
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
                        fController.deleteComponent(frameSet, "FrameSets");
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

    /**
     * Checks that all fields are completed
     *
     * @param name
     * @param cost
     * @param stock
     * @param gear
     * @param size
     * @return
     */
    private boolean checkDetailsCompleteness(
            String name, String cost,
            String stock, String gear, String size
    ) {
        if (
                name.equals("") ||
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
        btnDelete.setFont(f3);
        btnLogout.setFont(f3);
        btnUpdate.setFont(f3);
        btnBack.setFont(f3);
        lblBrand.setFont(f1);
        lblCost.setFont(f3);
        lblForks.setFont(f3);
        lblGear.setFont(f3);
        lblName.setFont(f3);
        lblSerialNumber.setFont(f1);
        lblSize.setFont(f3);
        lblShocks.setFont(f3);
        lblStock.setFont(f3);
        tfCost.setFont(f3);
        tfForks.setFont(f3);
        tfGear.setFont(f3);
        tfSize.setFont(f3);
        tfName.setFont(f3);
        tfStock.setFont(f3);
        checkShocks.setFont(f3);
    }
}
