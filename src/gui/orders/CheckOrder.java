package gui.orders;

import database.controllers.order.OrderController;
import exceptions.InvalidOrderException;
import exceptions.OrderNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import gui.customer.CustomerMenu;
import models.order.Order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CheckOrder extends AbstractFrame {
    private JPanel mainPanel;
    private JButton btnHome;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JPanel orderNumberPanel;
    private JTextField tfOrderNumber;
    private JButton btnForgot;
    private JButton btnSubmit;
    private JLabel lblTitle;
    private JLabel lblOrderNumber;
    private JButton btnBack;
    private OrderController oController = new OrderController();

    public CheckOrder() {
        add(mainPanel);
        setTitle("Check Order");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));
        setFonts();

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

        btnForgot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ForgotOrder forgotOrder = new ForgotOrder();
                forgotOrder.setVisible(true);
                dispose();
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Order order = oController.findOrderById(tfOrderNumber.getText());

                    if (order.getBicycleSerialNumber() != null) {
                        SingleOrder singleOrder = new SingleOrder(order);
                        singleOrder.setVisible(true);
                        dispose();
                    } else {
                        throw new InvalidOrderException(order.getOrderNumber());
                    }
                } catch (InvalidOrderException ex) {
                    showPopup(ex.getMessage());
                } catch (OrderNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (Exception ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });
    }

    public void setFonts() {
        btnForgot.setFont(f3);
        btnHome.setFont(f3);
        btnSubmit.setFont(f3);
        btnBack.setFont(f3);
        lblTitle.setFont(f1);
        lblOrderNumber.setFont(f3);
        tfOrderNumber.setFont(f3);
    }
}
