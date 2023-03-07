package gui.staff.orders;

import database.controllers.order.OrderController;
import exceptions.BicycleNotFoundException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import exceptions.OrderNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.order.Order;
import models.order.OrderStatus;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class StaffUpdateOrder extends AbstractFrame {
    private final Staff staff;
    private JPanel mainPanel;
    private JButton btnLogout;
    private JButton btnUpdate;
    private JLabel lblOrderNumber;
    private JComboBox comboStatus;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnBack;
    private JLabel lblStatus;
    private JButton btnCancel;
    private OrderController oController = new OrderController();

    public StaffUpdateOrder(Staff staff, Order order) {
        this.staff = staff;

        add(mainPanel);
        setTitle("Update");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));
        lblOrderNumber.setText(order.getOrderNumber());
        setFonts();

        // Adds the order statuses to the combo list
        OrderStatus[] orderStatuses = OrderStatus.values();
        for (OrderStatus orderStatus : orderStatuses) {
            comboStatus.addItem(orderStatus);
        }

        btnBack.addActionListener(new ActionListener() {
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

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = String.valueOf(comboStatus.getSelectedItem());
                order.setStatus(OrderStatus.valueOf(status));

                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure?",
                        "Warning!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        oController.updateOrderStatus(order);

                        StaffOrders staffOrders = new StaffOrders(staff);
                        staffOrders.setVisible(true);
                        dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showPopup("An external error occurred. Please try again!");
                    }
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    oController.deleteOrder(order);

                    StaffOrders staffOrders = new StaffOrders(staff);
                    staffOrders.setVisible(true);
                    dispose();
                } catch (OrderNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (InputTooLongException ex) {
                    showPopup(ex.getMessage());
                } catch (ComponentNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (BicycleNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (SQLException ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });
     }

    public void setFonts() {
        btnBack.setFont(f3);
        btnLogout.setFont(f3);
        btnUpdate.setFont(f3);
        btnCancel.setFont(f3);
        lblStatus.setFont(f3);
        lblOrderNumber.setFont(f1);
        comboStatus.setFont(f3);
    }
}
