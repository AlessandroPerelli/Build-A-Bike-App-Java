package gui.bikes;

import database.controllers.bicycle.BicycleController;
import database.controllers.order.OrderController;
import database.controllers.user.customer.AddressController;
import database.controllers.user.customer.CustomerController;
import exceptions.InputTooLongException;
import exceptions.InvalidAddressException;
import exceptions.UserNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.bicycle.Bicycle;
import models.order.Order;
import models.user.customer.Address;
import models.user.customer.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BikeOrder extends AbstractFrame {
    private final Cursor LOADING_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    private JPanel mainPanel;
    private JPanel topMenuPanel;
    private JButton btnCancel;
    private JButton btnOrderExistCustomer;
    private JButton btnOrderNewCustomer;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JTextField tfForename;
    private JTextField tfSurname;
    private JLabel lblMessage;
    private final AddressController aController = new AddressController();
    private Address address;
    private final BicycleController bController = new BicycleController();
    private final CustomerController cController = new CustomerController();
    private Customer customer;
    private final OrderController oController = new OrderController();
    private Order order;
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
    private JButton btnLogin;

    public BikeOrder(Bicycle bicycle) {
        add(mainPanel);
        setTitle("Fill Your Details and Place an Order");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure? All current progress of your order will be discarded.",
                        "Warning!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (dialogResult == 0) {
                        BikeBuilder bikeBuilder = new BikeBuilder();
                        bikeBuilder.setVisible(true);
                        dispose();
                }
            }
        });

        // Handle placing an order by an existing customer.
        btnOrderExistCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String forename = String.valueOf(tfForename.getText());
                String surname = String.valueOf(tfSurname.getText());
                String city = String.valueOf(tfCity.getText());
                String road = String.valueOf(tfRoad.getText());
                String postcode = String.valueOf(tfPostcode.getText());
                String houseNumberStr = String.valueOf(tfHouseNumber.getText());

                // Check the form details for their completeness.
                if (checkDetailsCompleteness(forename, surname, city, road, postcode, houseNumberStr)) {
                    int houseNumber = Integer.parseInt(houseNumberStr);
                    address = new Address(
                            houseNumber, road, postcode, city
                    );

                    try {
                        // Check if the full address is correct. If so, authenticate the user.
                        if (aController.matchAddress(address)) {
                            customer = new Customer(
                                    null,
                                    forename,
                                    surname,
                                    address
                            );

                            // If authenticated, then create a bicycle and place an order.
                            if (cController.authenticateCustomer(customer) != null) {
                                setCursor(LOADING_CURSOR);
                                order = new Order();
                                order.prepareOrder(
                                        customer.getCustomerId(),
                                        bicycle
                                );
                                boolean bicycleCreated = bController.createBicycle(bicycle);
                                boolean orderCreated = oController.createOrder(order);

                                if (bicycleCreated && orderCreated) {
                                    // Show order summary and redirect to menu.
                                    JOptionPane.showMessageDialog(
                                            null,
                                            prepareOrderSummary(order, bicycle, customer)
                                    );
                                    MainDashboard mainDashboard = new MainDashboard();
                                    mainDashboard.setVisible(true);
                                    dispose();
                                }
                            }
                            else {
                                showPopup("The account could not be authenticated. Please try again!");
                            }
                        }
                        else {
                            throw new InvalidAddressException();
                        }
                    }
                    catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    }
                    catch (UserNotFoundException ex) {
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
                    showPopup("The details are incomplete, or have incorrect format. " +
                            "\nPlease complete the missing fields and try again!");
                }
            }
        });

        // Handle order created by a new customer.
        btnOrderNewCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String forename = String.valueOf(tfForename.getText());
                String surname = String.valueOf(tfSurname.getText());
                String city = String.valueOf(tfCity.getText());
                String road = String.valueOf(tfRoad.getText());
                String postcode = String.valueOf(tfPostcode.getText());
                String houseNumberStr = String.valueOf(tfHouseNumber.getText());

                // Check the form details for their completeness.
                if (checkDetailsCompleteness(forename, surname, city, road, postcode, houseNumberStr)) {
                    int houseNumber = Integer.parseInt(houseNumberStr);
                    address = new Address(
                            houseNumber, road, postcode, city
                    );

                    try {
                        // If the address already exists, reuse it. Otherwise, create a new record.
                        if (!aController.matchAddress(address)) {
                            if (!aController.createAddress(address)) {
                                throw new InvalidAddressException();
                            }
                        }
                        else {
                            address = aController.findAddressById(address);
                        }

                        customer = new Customer(
                                null,
                                forename,
                                surname,
                                address
                        );

                        if (cController.authenticateCustomer(customer) == null) {
                            setCursor(LOADING_CURSOR);

                            // If customer successfully created, then create a bicycle an place an order.
                            cController.createCustomer(customer);
                            setCursor(LOADING_CURSOR);
                            order = new Order();
                            order.prepareOrder(
                                    customer.getCustomerId(),
                                    bicycle
                            );
                            boolean bicycleCreated = bController.createBicycle(bicycle);
                            boolean orderCreated = oController.createOrder(order);

                            if (bicycleCreated && orderCreated) {
                                // Show order summary and redirect to menu.r summary.
                                JOptionPane.showMessageDialog(
                                        null,
                                        prepareOrderSummary(order, bicycle, customer)
                                );
                                MainDashboard mainDashboard = new MainDashboard();
                                mainDashboard.setVisible(true);
                                dispose();
                            }
                        }
                        else {
                            showPopup("Your details are incorrect, or you selected the " +
                                    "wrong option. Please try again!");
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
                } else {
                    showPopup("The details are incomplete, or have incorrect format. " +
                            "Please complete the missing fields and try again!");
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

    /**
     * Prepare an order summary shown after successful order creation.
     *
     * @param order - placed ordered.
     * @param bicycle - ordered bicycle.
     * @param customer - customer who ordered.
     * @return String summarizing the order.
     */
    private String prepareOrderSummary(Order order, Bicycle bicycle, Customer customer) {
        return "ORDER NUMBER: " + order.getOrderNumber() + "\n" +
                "CUSTOMER NAME: " + customer.getForename() + " " + customer.getSurname() + "\n" +
                "ADDRESS: " + customer.getAddress().getHouseNumber() + " " + customer.getAddress().getRoad() +
                ", " + customer.getAddress().getCity() + " (" + customer.getAddress().getPostcode() + ")\n" +
                "DATE: " + order.getDate() + "\n\n" +
                "ORDER SUMMARY - " + bicycle.getCustomName() + " " + bicycle.getBrandName() + " (" +
                bicycle.getSerialNumber() + "):\n" +
                "\t• Frame-set: " + bicycle.getFrameSet().getComponentName() + " -- Cost: " +
                bicycle.getFrameSet().getCost() + " GBP\n" +
                "\t• Handlebar: " + bicycle.getHandlebar().getComponentName() + " -- Cost: " +
                bicycle.getHandlebar().getCost() + " GBP\n" +
                "\t• Pair of wheels: " + bicycle.getPairOfWheels().getComponentName() + " -- Cost: " +
                bicycle.getPairOfWheels().getCost() + " GBP\n" +
                "\t• Bicycle assembly -- Cost: " + Bicycle.getAssemblyCost() + " GBP\n\n" +
                "TOTAL COST: " + order.getTotalCost() + " GBP" + "\n\n" +
                "PLEASE REMEMBER TO SAVE YOUR ORDER NUMBER THE LATER REFERENCE IN CHECKOUT AREA!";
    }

    public void setFonts() {
        btnCancel.setFont(f3);
        btnOrderNewCustomer.setFont(f3);
        btnOrderExistCustomer.setFont(f3);
        lblCity.setFont(f3);
        lblHouseNumber.setFont(f3);
        lblForename.setFont(f3);
        lblPostcode.setFont(f3);
        lblRoad.setFont(f3);
        lblSurname.setFont(f3);
        tfForename.setFont(f3);
        tfSurname.setFont(f3);
        tfCity.setFont(f3);
        tfRoad.setFont(f3);
        tfPostcode.setFont(f3);
        tfHouseNumber.setFont(f3);
        lblMessage.setFont(f3);
    }
}
