package gui.orders;

import database.controllers.bicycle.BicycleController;
import database.controllers.order.OrderController;
import exceptions.BicycleNotFoundException;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import exceptions.OrderNotFoundException;
import gui.AbstractFrame;
import gui.MainDashboard;
import models.bicycle.Bicycle;
import models.order.Order;
import models.order.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SingleOrder extends AbstractFrame {
    private final Order order;
    private JPanel mainPanel;
    private JButton btnHome;
    private JPanel topMenuPanel;
    private JPanel bottomPanel;
    private JLabel lblOrderNumber;
    private JButton btnEdit;
    private JPanel contentPanel;
    private JLabel lblDate;
    private JLabel lblFrameSet;
    private JLabel lblHandlebar;
    private JLabel lblWheels;
    private JLabel lblAssemblyCost;
    private JLabel lblTotalCost;
    private JLabel lblStatus;
    private JLabel lblSummary;
    private JButton btnCancel;
    private BicycleController bController = new BicycleController();
    private OrderController oController = new OrderController();

    public SingleOrder (Order order) {
        this.order = order;

        add(mainPanel);
        setTitle("Order");
        contentPanel.setBorder(new EmptyBorder(10, 80, 20, 80));

        lblOrderNumber.setText("Order No." + order.getOrderNumber());
        orderSummary(order);
        setFonts();

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
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure?",
                        "Warning!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    if (order.getStatus() == OrderStatus.PENDING) {
                        try {
                            oController.deleteOrder(order);

                            MainDashboard mainDashboard = new MainDashboard();
                            mainDashboard.setVisible(true);
                            dispose();
                        } catch (OrderNotFoundException ex) {
                            showPopup(ex.getMessage());
                        } catch (InputTooLongException ex) {
                            showPopup(ex.getMessage());
                        } catch (ComponentNotFoundException ex) {
                            showPopup(ex.getMessage());
                        } catch (BicycleNotFoundException ex) {
                            showPopup(ex.getMessage());
                        } catch (Exception ex) {
                            showPopup("An external error occurred. Please try again!");
                        }
                    }
                    else {
                        showPopup("You cannot cancel an order that's not PENDING!");
                    }
                }
            }
        });
    }

    private void orderSummary(Order order) {
        Bicycle bicycle = null;
        try {
            bicycle = bController.findBicycleById(order.getBicycleSerialNumber());
        } catch (InputTooLongException e) {
            showPopup(e.getMessage());
        } catch (BicycleNotFoundException e) {
            showPopup(e.getMessage());
        } catch (ComponentNotFoundException e) {
            showPopup(e.getMessage());
        } catch (Exception e) {
            showPopup("An external error occurred. Please try again!");
        }

        if (bicycle != null) {
         lblDate.setText("DATE: " + order.getDate());
         lblFrameSet.setText("Frame-set: " + bicycle.getFrameSet().getComponentName() + " -- Cost: " +
            bicycle.getFrameSet().getCost() + " GBP");
         lblSummary.setText("Order Summary:- " + bicycle.getCustomName() + " " + bicycle.getBrandName() + " (" +
                 bicycle.getSerialNumber() + "):");
         lblHandlebar.setText("Handlebar: " + bicycle.getHandlebar().getComponentName() + " -- Cost: " +
            bicycle.getHandlebar().getCost() + " GBP");
         lblWheels.setText("Pair of wheels: " + bicycle.getPairOfWheels().getComponentName() +  "Cost: " +
                 bicycle.getPairOfWheels().getCost() + " GBP");
         lblAssemblyCost.setText("Bicycle assembly -- Cost: " + Bicycle.getAssemblyCost() + " GBP");
         lblTotalCost.setText("Total cost: " + order.getTotalCost() + " GBP");
         lblStatus.setText("Status: " + order.getStatus());
    }
    }

    public void setFonts() {
        lblOrderNumber.setFont(f1);
        btnHome.setFont(f3);
        btnCancel.setFont(f3);
        lblDate.setFont(f3);
        lblFrameSet.setFont(f3);
        lblHandlebar.setFont(f3);
        lblWheels.setFont(f3);
        lblAssemblyCost.setFont(f3);
        lblTotalCost.setFont(f3);
        lblStatus.setFont(f3);
        lblSummary.setFont(f3);
    }
}
