package gui.customer;

import gui.AbstractFrame;
import gui.MainDashboard;
import gui.orders.CheckOrder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerMenu extends AbstractFrame {

    private JPanel mainPanel;
    private JPanel topMenuPanel;
    private JPanel contentPanel;
    private JButton btnOrders;
    private JButton btnDetails;
    private JLabel lblTitle;
    private JButton btnHome;

    public CustomerMenu() {
        add(mainPanel);
        setTitle("Customer Menu");
        setFonts();

        btnOrders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CheckOrder checkOrder = new CheckOrder();
                checkOrder.setVisible(true);
                dispose();
            }
        });

        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerLogin customerLogin = new CustomerLogin();
                customerLogin.setVisible(true);
                dispose();
            }
        });

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true);
                dispose();
            }
        });
    }

    public void setFonts() {
        lblTitle.setFont(f1);
        btnDetails.setFont(f3);
        btnOrders.setFont(f3);
    }
}
