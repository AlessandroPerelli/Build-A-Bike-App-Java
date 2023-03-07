package gui;

import gui.bikes.BikeBuilder;
import gui.customer.CustomerMenu;
import gui.staff.StaffLogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends AbstractFrame {
    private final Cursor LOADING_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    private JPanel rootPanel;
    private JPanel menuPanel;
    private JButton btnBuild;
    private JButton btnCustomer;
    private JButton btnStaff;
    private JLabel lblTitle;

    public MainDashboard () {
        add(rootPanel);
        setTitle("Build-a-Bike Ltd.");

        btnStaff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffLogin staffLogin = new StaffLogin();
                staffLogin.setVisible(true);
                dispose();
            }
        });

        btnBuild.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setCursor(LOADING_CURSOR);
                    BikeBuilder bikeBuilder = new BikeBuilder();
                    bikeBuilder.setVisible(true);
                    dispose();
                }
                catch (Exception ex) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    JOptionPane.showMessageDialog(
                            null,
                            "Some of the components are not available at the moment" +
                                    " and bikes cannot be assembled. We are sorry!"
                    );
                }
            }
        });

        btnCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerMenu customerMenu = new CustomerMenu();
                customerMenu.setVisible(true);
                dispose();
            }
        });
    }

    public void setFonts() {
        lblTitle.setFont(f1);
        btnCustomer.setFont(f2);
        btnBuild.setFont(f2);
        btnStaff.setFont(f2);
    }
}
