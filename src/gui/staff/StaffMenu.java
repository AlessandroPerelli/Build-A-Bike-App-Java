package gui.staff;
import gui.AbstractFrame;
import gui.MainDashboard;
import gui.staff.orders.StaffOrders;
import gui.staff.stock.StaffStock;
import gui.staff.customers.StaffCustomers;
import models.user.staff.Staff;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffMenu extends AbstractFrame {
    private final Staff staff;
    private JPanel menuPanel;
    private JButton btnLogout;
    private JPanel topMenuPanel;
    private JPanel contentPanel;
    private JButton btnOrders;
    private JButton btnUsers;
    private JButton btnStock;
    private JButton btnBack;

    public StaffMenu(Staff staff) {
        this.staff = staff;
        add(menuPanel);
        setTitle("Staff Menu");

        btnStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffStock staffStock = new StaffStock(staff);
                staffStock.setVisible(true);
                dispose();
            }
        });

        btnUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffCustomers staffUsers = new StaffCustomers(staff);
                staffUsers.setVisible(true);
                dispose();
            }
        });

        btnOrders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffOrders staffOrders = new StaffOrders(staff);
                staffOrders.setVisible(true);
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

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffLogin staffLogin = new StaffLogin();
                staffLogin.setVisible(true);
                dispose();
            }
        });
    }

    public void setFonts() {
        btnLogout.setFont(f3);
        btnOrders.setFont(f3);
        btnStock.setFont(f3);
        btnUsers.setFont(f3);
    }
}
