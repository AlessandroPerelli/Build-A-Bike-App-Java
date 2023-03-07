package gui.orders;

import database.controllers.order.OrderController;
import exceptions.*;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.order.Order;
import models.order.OrderStatus;
import models.user.customer.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerOrders extends AbstractFrame {

    private JPanel mainPanel;
    private JButton btnHome;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnDetails;
    private JTable tableOrders;
    private JButton btnBack;
    private JButton btnCancel;
    private OrderController oController = new OrderController();

    public CustomerOrders(Customer customer) {
        add(mainPanel);
        setTitle("Orders");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));
        setFonts();

        try {
            generateOrdersTable(oController.findCustomerOrders(customer));
        } catch (NoOrderForUserException e) {
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage()
            );
        } catch (InvalidOrderException e) {
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "An external error occurred. Please try again!"
            );
        }

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CheckOrder checkOrder = new CheckOrder();
                checkOrder.setVisible(true);
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

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableOrders.getSelectedRow();
                String orderNumber = tableOrders.getModel().getValueAt(row, 0).toString();

                Order order = null;
                try {
                    order = oController.findOrderById(orderNumber);

                    generateOrdersTable(oController.findCustomerOrders(customer));
                    tableOrders.repaint();
                    if (order.getStatus() == OrderStatus.PENDING) {
                        oController.deleteOrder(order);
                    }
                } catch (InvalidOrderException ex) {
                    showPopup(ex.getMessage());
                } catch (OrderNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (BicycleNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (ComponentNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (InputTooLongException ex) {
                    showPopup(ex.getMessage());
                } catch (NoOrderForUserException ex) {
                    showPopup(ex.getMessage());
                } catch (SQLException ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });

        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableOrders.getSelectedRow();
                String orderNumber = tableOrders.getModel().getValueAt(row, 0).toString();

                SingleOrder singleOrder = null;
                try {
                    singleOrder = new SingleOrder(oController.findOrderById(orderNumber));
                    singleOrder.setVisible(true);
                    dispose();
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


    private void generateOrdersTable(ArrayList<Order> orders) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Order No.");
        model.addColumn("Date");
        model.addColumn("Cost");
        model.addColumn("Status");
        model.addColumn("Serial No.");

        Object[] rowData = new Object[5];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            rowData[0] = order.getOrderNumber();
            rowData[1] = order.getDate();
            rowData[2] = order.getTotalCost();
            rowData[3] = order.getStatus();
            rowData[4] = order.getBicycleSerialNumber();

            model.addRow(rowData);
        }

        tableOrders.setModel(model);
    }

    public void setFonts() {
        btnDetails.setFont(f3);
        btnHome.setFont(f3);
        btnCancel.setFont(f3);
        btnBack.setFont(f3);
        tableOrders.setFont(f3);
    }
}
