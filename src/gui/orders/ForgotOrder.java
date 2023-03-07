package gui.orders;

import database.controllers.order.OrderController;
import database.controllers.user.customer.AddressController;
import database.controllers.user.customer.CustomerController;
import exceptions.InputTooLongException;
import exceptions.InvalidAddressException;
import exceptions.UserNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.user.customer.Address;
import models.user.customer.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ForgotOrder extends AbstractFrame {

    private JPanel mainPanel;
    private JButton btnHome;
    private JButton btnBack;
    private JPanel topMenuPanel;
    private JButton btnSubmit;
    private JTextField tfForename;
    private JTextField tfSurname;
    private JTextField tfHouseNumber;
    private JTextField tfPostcode;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JLabel lblForename;
    private JLabel lblSurname;
    private JLabel lblHouseNumber;
    private JLabel lblPostcode;
    AddressController aController = new AddressController();
    OrderController oController = new OrderController();
    CustomerController cController = new CustomerController();

    public ForgotOrder(){
        add(mainPanel);
        setTitle("Check Order");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));
        setFonts();

        btnHome.addActionListener(new ActionListener() {
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
                CheckOrder checkOrder = new CheckOrder();
                checkOrder.setVisible(true);
                dispose();
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String forename = String.valueOf(tfForename.getText());
                String surname = String.valueOf(tfSurname.getText());
                String postcode = String.valueOf(tfPostcode.getText());
                String houseNumberStr = String.valueOf(tfHouseNumber.getText());

                if (checkDetailsCompleteness(forename, surname, postcode, houseNumberStr)) {
                    try {
                        Customer customer  = new Customer(
                                null,
                                forename,
                                surname,
                                new Address(
                                        Integer.parseInt(houseNumberStr),
                                        null,
                                        postcode,
                                        null
                                )
                        );
                        customer = cController.authenticateCustomer(customer);

                        if (customer.getCustomerId() != null) {
                            CustomerOrders addressOrders = new CustomerOrders(customer);
                            addressOrders.setVisible(true);
                            dispose();
                        } else {
                            throw new UserNotFoundException();
                        }
                    } catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    } catch (UserNotFoundException ex) {
                        showPopup(ex.getMessage());
                    } catch (Exception ex) {
                        showPopup("Failed to authenticate the user. Please try again!");
                    }
                }
                else {
                    showPopup("Your details are incorrect, or you selected the " +
                            "wrong option. Please try again");
                }
            }
        });
    }

    /**
     * Check if the provided user details are complete.
     *
     * @param forename
     * @param surname
     * @param postcode
     * @param houseNumber
     * @return true - complete details. false - incomplete details.
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
        btnBack.setFont(f3);
        btnHome.setFont(f3);
        btnSubmit.setFont(f3);
        lblForename.setFont(f3);
        lblPostcode.setFont(f3);
        lblSurname.setFont(f3);
        lblHouseNumber.setFont(f3);
        tfForename.setFont(f3);
        tfPostcode.setFont(f3);
        tfSurname.setFont(f3);
        tfHouseNumber.setFont(f3);
    }
}
