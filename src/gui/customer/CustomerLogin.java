package gui.customer;

import database.controllers.user.customer.AddressController;
import exceptions.InputTooLongException;
import exceptions.UserNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.user.customer.Address;
import models.user.customer.Customer;
import database.controllers.user.customer.CustomerController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerLogin extends AbstractFrame {
    private JPanel mainPanel;
    private JButton btnHome;
    private JButton btnLogin;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    private JPanel topMenuPanel;
    private JTextField tfForename;
    private JTextField tfSurname;
    private JTextField tfHouseNumber;
    private JTextField tfPostcode;
    private JLabel lblForename;
    private JLabel lblSurname;
    private JLabel lblHouseNumber;
    private JLabel lblPostcode;
    private JButton btnBack;
    private JLabel lblLogin;
    private JLabel lblMessage;
    private final CustomerController cController = new CustomerController();
    private final AddressController aController = new AddressController();
    private Address address;
    private Customer customer;

    public CustomerLogin() {
        add(mainPanel);
        setTitle("Login");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));
        setFonts();

        //Tries to log in using the input
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String forename = String.valueOf(tfForename.getText());
                String surname = String.valueOf(tfSurname.getText());
                String postcode = String.valueOf(tfPostcode.getText());
                String houseNumberStr = String.valueOf(tfHouseNumber.getText());

                // Check the form details for their completeness.
                if (checkDetailsCompleteness(forename, surname, postcode, houseNumberStr)) {
                    int houseNumber = Integer.parseInt(houseNumberStr);
                    customer = new Customer(
                            null,
                            forename,
                            surname,
                            new Address(houseNumber, null, postcode, null)
                    );
                    try {
                        // Attempt to authenticate.
                        if (cController.authenticateCustomer(customer) != null) {
                            customer.setAddress(
                                    aController.findAddressById(customer.getAddress())
                            );
                            showPopup("You have been successfully authenticated");

                            UpdateCustomer updateCustomer = new UpdateCustomer(customer);
                            updateCustomer.setVisible(true);
                            dispose();
                        }
                        else {
                            showPopup("Failed to authenticated. Please try again!");
                        }
                    }
                    catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    }
                    catch (UserNotFoundException ex) {
                        showPopup(ex.getMessage());
                    }
                    catch (Exception ex) {
                        showPopup("Could not authenticate. Please tyr again!");
                    }
                }
                else {
                    showPopup("Incorrect details. Please try again!");
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerMenu customerMenu = new CustomerMenu();
                customerMenu.setVisible(true);
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

    /**
     * Checks that all fields where completed
     *
     * @param forename
     * @param surname
     * @param postcode
     * @param houseNumber
     * @return
     */
    private boolean checkDetailsCompleteness(
            String forename, String surname, String postcode, String houseNumber
    ) {
        if (forename.equals("") || surname.equals("") || postcode.equals("")) {
            return false;
        } else {
            try {
                Integer.parseInt(houseNumber);
                return true;
            } catch (NumberFormatException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }
    }

    public void setFonts() {
        btnHome.setFont(f3);
        btnLogin.setFont(f3);
        btnBack.setFont(f3);
        lblForename.setFont(f3);
        lblPostcode.setFont(f3);
        lblHouseNumber.setFont(f3);
        lblSurname.setFont(f3);
        tfForename.setFont(f3);
        tfSurname.setFont(f3);
        tfPostcode.setFont(f3);
        tfHouseNumber.setFont(f3);
        lblLogin.setFont(f1);
    }
}
