package gui.customer;

import database.controllers.user.customer.AddressController;
import database.controllers.user.customer.CustomerController;
import exceptions.InputTooLongException;
import exceptions.InvalidAddressException;
import exceptions.InvalidCredentialsException;
import exceptions.UserNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.user.customer.Address;
import models.user.customer.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateCustomer extends AbstractFrame {
    private Customer customer;
    private JPanel mainPanel;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnHome;
    private JButton btnUpdate;
    private JTextField tfForename;
    private JTextField tfSurname;
    private JTextField tfHouseNumber;
    private JTextField tfCity;
    private JTextField tfRoad;
    private JTextField tfPostcode;
    private JLabel lblForename;
    private JLabel lblSurname;
    private JLabel lblHouseNumber;
    private JLabel lblCity;
    private JLabel lblRoad;
    private JLabel lblPostcode;
    private JButton btnBack;
    private JLabel lblTitle;
    private final static CustomerController cController = new CustomerController();
    private final static AddressController aController = new AddressController();

    public UpdateCustomer(Customer customer) {
        this.customer = customer;
        Address address = customer.getAddress();

        add(mainPanel);
        setTitle("Update Details");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));
        setFonts();

        //Sets the text fields to contain the current user's details
        String customerID = customer.getCustomerId();
        tfForename.setText(customer.getForename());
        tfSurname.setText(customer.getSurname());
        tfHouseNumber.setText(String.valueOf(address.getHouseNumber()));
        tfRoad.setText(address.getRoad());
        tfPostcode.setText(address.getPostcode());
        tfCity.setText(address.getCity());

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

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure?",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    String forename = String.valueOf(tfForename.getText());
                    String surname = String.valueOf(tfSurname.getText());
                    String city = String.valueOf(tfCity.getText());
                    String road = String.valueOf(tfRoad.getText());
                    String postcode = String.valueOf(tfPostcode.getText());
                    String houseNumberStr = String.valueOf(tfHouseNumber.getText());

                    if (checkDetailsCompleteness(forename, surname, city, road, postcode, houseNumberStr)) {
                        int houseNumber = Integer.parseInt(houseNumberStr);
                        Address address = new Address(
                                houseNumber, road, postcode, city
                        );
                        customer.setForename(forename);
                        customer.setSurname(surname);
                        customer.setAddress(address);

                        try {
                            // If the address already exists, reuse it. Otherwise, create a new record.
                            if (cController.updateDetails(customer)) {
                                showPopup("You successfully update your details");

                                MainDashboard mainDashboard = new MainDashboard();
                                mainDashboard.setVisible(true);
                                dispose();
                            }
                            else {
                                throw new InvalidCredentialsException("Could not update the customer!");
                            }
                        }
                        catch (InputTooLongException ex) {
                            showPopup(ex.getMessage());
                        }
                        catch (InvalidAddressException ex) {
                            showPopup(ex.getMessage());
                        }
                        catch (Exception ex) {
                            showPopup("An external error occurred. Please try again!");
                        }
                    }
                    else {
                        showPopup("Your details are incorrect.");
                    }
                }
            }
        });
    }

    /**
     * Check if the provided user details are complete.
     *
     * @param forename
     * @param surname
     * @param city
     * @param road
     * @param postcode
     * @param houseNumber
     * @return true - complete details. false - incomplete details.
     */
    private boolean checkDetailsCompleteness(
            String forename, String surname, String city, String road, String postcode, String houseNumber
    ) {
        if (
                forename.equals("") ||
                        surname.equals("") ||
                        city.equals("") ||
                        road.equals("") ||
                        postcode.equals("")
        ) {
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
        btnUpdate.setFont(f3);
        btnHome.setFont(f3);
        lblCity.setFont(f3);
        lblForename.setFont(f3);
        lblRoad.setFont(f3);
        lblPostcode.setFont(f3);
        lblSurname.setFont(f3);
        lblHouseNumber.setFont(f3);
        tfCity.setFont(f3);
        tfPostcode.setFont(f3);
        tfRoad.setFont(f3);
        tfSurname.setFont(f3);
        tfForename.setFont(f3);
        tfHouseNumber.setFont(f3);
        lblTitle.setFont(f1);
    }
}
