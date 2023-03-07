package gui.staff.customers;

import database.controllers.user.customer.AddressController;
import database.controllers.user.customer.CustomerController;
import gui.AbstractFrame;
import gui.MainDashboard;
import gui.staff.StaffMenu;
import models.user.customer.Address;
import models.user.customer.Customer;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class StaffCustomers extends AbstractFrame {
    private final Staff staff;
    private JPanel usersPanel;
    private JButton btnLogout;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JTable tableUsers;
    private JPanel contentPanel;
    private JButton btnBack;
    private CustomerController cController = new CustomerController();
    private AddressController aController = new AddressController();

    public StaffCustomers(Staff staff) {
        this.staff = staff;
        
        add(usersPanel);
        setTitle("Staff Users");
        setFonts();

        ArrayList<Customer> customers = new ArrayList<>();

        try {
            generateCustomersTable(cController.readAllCustomers());
        } catch (Exception e) {
            showPopup("An external error occurred. Please try again!");
        }

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffMenu staffMenu = new StaffMenu(staff);
                staffMenu.setVisible(true);
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

    private void generateCustomersTable(ArrayList<Customer> customers) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id");
        model.addColumn("Forename");
        model.addColumn("Surname");
        model.addColumn("House No.");
        model.addColumn("Postcode");

        Object[] rowData = new Object[5];
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            rowData[0] = customer.getCustomerId();
            rowData[1] = customer.getForename();
            rowData[2] = customer.getSurname();
            rowData[3] = customer.getAddress().getHouseNumber();
            rowData[4] = customer.getAddress().getPostcode();

            model.addRow(rowData);
        }

        tableUsers.setModel(model);
    }

    public void setFonts() {
        btnBack.setFont(f3);
        btnLogout.setFont(f3);
        tableUsers.setFont(f3);
    }
}
