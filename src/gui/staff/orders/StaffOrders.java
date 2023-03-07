

package gui.staff.orders;

import database.controllers.order.OrderController;
import exceptions.*;
import gui.AbstractFrame;
import gui.MainDashboard;
import gui.staff.StaffMenu;
import models.order.Order;
import models.order.OrderStatus;
import models.order.items.Item;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class StaffOrders extends AbstractFrame {
    private final Staff staff;
    private JPanel mainPanel;
    private JTable tableMyOrders;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private JButton btnUpdate;
    private JButton btnDetails;
    private JPanel topMenuPanel;
    private JButton btnBack;
    private JButton btnLogout;
    private JTabbedPane tabbedOrdersPane;
    private JTable tableUnassigned;
    private JPanel myOrdersPanel;
    private JPanel unassignedPanel;
    private JScrollPane myOrdersScroll;
    private JScrollPane unassignedScroll;
    private JButton btnAssign;
    private JButton btnUnassign;
    private JButton btnDelete;
    OrderController oController = new OrderController();

    public StaffOrders (Staff staff) {
        this.staff = staff;

        add(mainPanel);
        setTitle("Staff Orders");
        setFonts();

        try {
            generateUnassignedTable(oController.readAllPendingOrders());
            generateMyOrdersTable(oController.findStaffOrders(staff));
        } catch (InvalidOrderException ex) {
            showPopup(ex.getMessage());
        } catch (NoOrderForUserException ex) {
            showPopup(ex.getMessage());
        } catch (Exception ex) {
            showPopup("An external error occurred. Please try again!");
        }

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
                StaffMenu staffMenu = new StaffMenu(staff);
                staffMenu.setVisible(true);
                dispose();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableMyOrders.getSelectedRow();
                String orderNumber = tableMyOrders.getModel().getValueAt(row, 0).toString();

                Order order = null;

                try {
                    order = oController.findOrderById(orderNumber);

                    StaffUpdateOrder staffUpdateOrder = new StaffUpdateOrder(staff, order);
                    staffUpdateOrder.setVisible(true);
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

        btnAssign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableUnassigned.getSelectedRow();
                String orderNumber = tableUnassigned.getModel().getValueAt(row, 0).toString();

                try {
                    oController.assignStaffOrder(orderNumber, staff.getUsername());

                    generateUnassignedTable(oController.readAllPendingOrders());
                    generateMyOrdersTable(oController.findStaffOrders(staff));
                    tableUnassigned.repaint();
                    tableMyOrders.repaint();
                } catch (Exception ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });

        btnUnassign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableMyOrders.getSelectedRow();
                String orderNumber = tableMyOrders.getModel().getValueAt(row, 0).toString();

                try {
                    Order order = oController.findOrderById(orderNumber);
                    if (order.getStatus() == OrderStatus.PENDING) {
                        oController.cancelStaffOrder(orderNumber);

                        generateUnassignedTable(oController.readAllPendingOrders());
                        generateMyOrdersTable(oController.findStaffOrders(staff));
                        tableUnassigned.repaint();
                        tableMyOrders.repaint();
                    } else {
                        showPopup("You cannot unassigned an order that's not PENDING");
                    }
                } catch (OrderNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (InvalidOrderException ex) {
                    showPopup(ex.getMessage());
                } catch (NoOrderForUserException ex) {
                    showPopup(ex.getMessage());
                } catch (Exception ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableUnassigned.getSelectedRow();
                String orderNumber = tableUnassigned.getModel().getValueAt(row, 0).toString();

                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? This cannot be recovered",
                        "Warning!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        Order order = oController.findOrderById(orderNumber);
                        oController.deleteOrder(order);

                        generateUnassignedTable(oController.readAllPendingOrders());
                        tableUnassigned.repaint();
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
                    } catch (Exception ex) {
                        showPopup("An external error occurred. Please try again!");
                    }
                }
            }
        });

        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableMyOrders.getSelectedRow();
                String orderNumber = tableMyOrders.getModel().getValueAt(row, 0).toString();

                try {
                    Order order = oController.findOrderById(orderNumber);
                    ArrayList<Item> items = order.getOrderItems();

                    for (Item item : items) {
                        itemDialogPane(itemDetails(item));
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

    /**
     * Generates a table with all orders assigned to the staff member
     *
     * @param orders
     */
    public void generateMyOrdersTable(ArrayList<Order> orders) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Order No.");
        model.addColumn("Date");
        model.addColumn("Cost");
        model.addColumn("Status");
        model.addColumn("Serial No.");
        model.addColumn("Customer ID");

        Object[] rowData = new Object[6];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            rowData[0] = order.getOrderNumber();
            rowData[1] = order.getDate();
            rowData[2] = order.getTotalCost();
            rowData[3] = order.getStatus();
            rowData[4] = order.getBicycleSerialNumber();
            rowData[5] = order.getCustomerId();

            model.addRow(rowData);
        }

        tableMyOrders.setModel(model);
    }

    /**
     * Generates the table with all unassigned orders
     *
     * @param orders
     */
    public void generateUnassignedTable(ArrayList<Order> orders) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Order No.");
        model.addColumn("Date");
        model.addColumn("Cost");
        model.addColumn("Status");
        model.addColumn("Serial No.");
        model.addColumn("Customer ID");

        Object[] rowData = new Object[6];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            rowData[0] = order.getOrderNumber();
            rowData[1] = order.getDate();
            rowData[2] = order.getTotalCost();
            rowData[3] = order.getStatus();
            rowData[4] = order.getBicycleSerialNumber();
            rowData[5] = order.getCustomerId();

            model.addRow(rowData);
        }

        tableUnassigned.setModel(model);
    }

    /**
     * Returns the item details in a nice string format
     *
     * @param item
     * @return
     */
    private String itemDetails(Item item) {
        String outputStr = "Item ID: " + item.getItemId() + "\n" +
                "Type: " + item.getType() + "\n\n" +
                "Brand name: " + item.getBrandName() + "\n" +
                "Quantity: " + item.getQuantity() + "\n" +
                "Cost: " + item.getCost() + "\n";
        return outputStr;
    }

    public void itemDialogPane(String message) {
        JLabel lblMessage = new JLabel();
        lblMessage.setText(message);
        Object[] options = { "Next" };

        JPanel itemPanel = new JPanel();
        itemPanel.add(lblMessage);

        int result = JOptionPane.showOptionDialog(null, itemPanel, "Item Details",
                JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

    }

    public void setFonts() {
        btnLogout.setFont(f3);
        btnBack.setFont(f3);
        btnDetails.setFont(f3);
        btnUpdate.setFont(f3);
        btnDelete.setFont(f3);
        btnUnassign.setFont(f3);
        btnAssign.setFont(f3);
        tableMyOrders.setFont(f3);
        tableUnassigned.setFont(f3);
    }
}