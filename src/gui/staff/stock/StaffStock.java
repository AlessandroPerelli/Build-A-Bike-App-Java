package gui.staff.stock;
import database.controllers.component.FrameSetController;
import database.controllers.component.HandlebarController;
import database.controllers.component.PairOfWheelsController;
import exceptions.ComponentNotFoundException;
import exceptions.InputTooLongException;
import gui.AbstractFrame;
import gui.MainDashboard;
import gui.staff.StaffMenu;
import models.component.frameset.FrameSet;
import models.component.handlebar.Handlebar;
import models.component.pairofwheels.PairOfWheels;
import models.user.staff.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class StaffStock extends AbstractFrame {
    private final Staff staff;
    private JPanel mainPanel;
    private JButton btnLogout;
    private JPanel topMenuPanel;
    private JPanel contentPanel;
    private JButton btnBack;
    private JTabbedPane tabbedComponentPane;
    private JPanel handlebarsPanel;
    private JPanel framsetsPanel;
    private JPanel pairsOfWheelsPanel;
    private JScrollPane handlebarsScroll;
    private JScrollPane pairsOfWheelsScroll;
    private JButton btnAddHandlebars;
    private JButton btnAddFramesets;
    private JButton btnAddPairOfWheels;
    private JScrollPane framesetScroll;
    private JTable tableFrameset;
    private JTable tableHandlebar;
    private JTable tablePairsOfWheels;
    private JButton btnUpdateFS;
    private JButton btnUpdateHB;
    private JButton btnUpdatePW;
    private JButton btnDeleteFS;
    private JButton btnDeleteHB;
    private JButton btnDeletePW;
    private FrameSetController fController;
    private HandlebarController hController;
    private PairOfWheelsController powController;

    public StaffStock(Staff staff) {
        this.staff = staff;

        add(mainPanel);
        setTitle("Staff Stock");
        setFonts();

        fController = new FrameSetController();
        hController = new HandlebarController();
        powController = new PairOfWheelsController();

        try {
            generateFramesetTable(fController.readAllComponentsOfType(true));
            generateHandlebarTable(hController.readAllComponentsOfType(true));
            generatePairOfWheelsTable(powController.readAllComponentsOfType(true));
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

        btnAddFramesets.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FramesetStock framesetStock = new FramesetStock(staff);
                framesetStock.setVisible(true);
                dispose();
            }
        });

        btnUpdateFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableFrameset.getSelectedRow();
                String serialNumber = tableFrameset.getModel().getValueAt(row, 0).toString();
                String brandName = tableFrameset.getModel().getValueAt(row, 2).toString();

                try {
                    FrameSet frameSet = fController.findComponentById(serialNumber, brandName);

                    // Makes sure that the staff input something in the popup
                    if (frameSet != null) {
                        UpdateFrameset updateFrameset = new UpdateFrameset(frameSet, staff);
                        updateFrameset.setVisible(true);
                        dispose();
                    }
                } catch (ComponentNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (InputTooLongException ex) {
                    showPopup(ex.getMessage());
                } catch (Exception ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });

        btnDeleteFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableFrameset.getSelectedRow();
                String serialNumber = tableFrameset.getModel().getValueAt(row, 0).toString();
                String brandName = tableFrameset.getModel().getValueAt(row, 2).toString();

                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? This cannot be recovered",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        FrameSet frameSet = fController.findComponentById(serialNumber, brandName);

                        // Makes sure that the staff input something in the popup
                        if (frameSet != null) {
                            fController.deleteComponent(frameSet, "FrameSets");
                            generateFramesetTable(fController.readAllComponentsOfType(true));
                            tableFrameset.repaint();
                        }
                    } catch (ComponentNotFoundException ex) {
                        showPopup(ex.getMessage());
                    } catch (InputTooLongException ex) {
                        showPopup(ex.getMessage());
                    } catch (Exception ex) {
                        showPopup("An external error occurred. Please try again!");
                    }

                    StaffStock staffStock = new StaffStock(staff);
                    staffStock.setVisible(true);
                    dispose();
                }
            }
        });

        btnAddHandlebars.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HandlebarStock handlebarStock = new HandlebarStock(staff);
                handlebarStock.setVisible(true);
                dispose();
            }
        });

        btnUpdateHB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableHandlebar.getSelectedRow();
                String serialNumber = tableHandlebar.getModel().getValueAt(row, 0).toString();
                String brandName = tableHandlebar.getModel().getValueAt(row, 2).toString();

                try {
                    Handlebar handlebar = hController.findComponentById(serialNumber, brandName);

                    // Makes sure that the staff input something in the popup
                    if (handlebar != null) {
                        UpdateHandlebar updateHandlebar = new UpdateHandlebar(handlebar, staff);
                        updateHandlebar.setVisible(true);
                        dispose();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);

                } catch (ComponentNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (InputTooLongException ex) {
                    showPopup(ex.getMessage());
                }
            }
        });

        btnDeleteHB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableHandlebar.getSelectedRow();
                String serialNumber = tableHandlebar.getModel().getValueAt(row, 0).toString();
                String brandName = tableHandlebar.getModel().getValueAt(row, 2).toString();

                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? This cannot be recovered",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        Handlebar handlebar = hController.findComponentById(serialNumber, brandName);

                        if (handlebar != null) {
                            hController.deleteComponent(handlebar, "Handlebars");
                            generateHandlebarTable(hController.readAllComponentsOfType(true));
                            tableHandlebar.repaint();
                        }
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

        btnAddPairOfWheels.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PairOfWheelsStock pairOfWheelsStock = new PairOfWheelsStock(staff);
                pairOfWheelsStock.setVisible(true);
                dispose();
            }
        });

        btnUpdatePW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tablePairsOfWheels.getSelectedRow();
                String serialNumber = tablePairsOfWheels.getModel().getValueAt(row, 0).toString();
                String brandName = tablePairsOfWheels.getModel().getValueAt(row, 2).toString();

                try {
                    PairOfWheels wheel = powController.findComponentById(serialNumber, brandName);

                    if (wheel != null) {
                        UpdatePairOfWheels updatePairOfWheels = new UpdatePairOfWheels(wheel, staff);
                        updatePairOfWheels.setVisible(true);
                        dispose();
                    }
                } catch (ComponentNotFoundException ex) {
                    showPopup(ex.getMessage());
                } catch (InputTooLongException ex) {
                    showPopup(ex.getMessage());
                } catch (Exception ex) {
                    showPopup("An external error occurred. Please try again!");
                }
            }
        });

        btnDeletePW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tablePairsOfWheels.getSelectedRow();
                String serialNumber = tablePairsOfWheels.getModel().getValueAt(row, 0).toString();
                String brandName = tablePairsOfWheels.getModel().getValueAt(row, 2).toString();

                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure? This cannot be recovered",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == 0) {
                    try {
                        PairOfWheels wheels = powController.findComponentById(serialNumber, brandName);

                        if (wheels != null) {
                            powController.deleteComponent(wheels, "PairsOfWheels");
                            generatePairOfWheelsTable(powController.readAllComponentsOfType(true));
                            tablePairsOfWheels.repaint();
                        }
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
    }

    /**
     * Generates a table containing all framesets in the DB
     *
     * @param framesets
     */
    public void generateFramesetTable(ArrayList<FrameSet> framesets) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Serial No.:");
        model.addColumn("Name:");
        model.addColumn("Brand:");
        model.addColumn("Cost:");
        model.addColumn("Stock:");
        model.addColumn("Forks:");
        model.addColumn("Gears:");
        model.addColumn("Size:");
        model.addColumn("Shocks:");

        Object[] rowData = new Object[9];
        for (int i = 0; i < framesets.size(); i++) {
            FrameSet frameset = framesets.get(i);
            rowData[0] = frameset.getSerialNumber();
            rowData[1] = frameset.getComponentName();
            rowData[2] = frameset.getBrandName();
            rowData[3] = frameset.getCost();
            rowData[4] = frameset.getStock();
            rowData[5] = frameset.getForkSetName();
            rowData[6] = frameset.getGearSetName();
            rowData[7] = frameset.getSize();
            rowData[8] = frameset.isHasShocks();

            model.addRow(rowData);
        }

        tableFrameset.setModel(model);
    }

    /**
     * Generates a table containing all handlebars in the DB
     *
     * @param handlebars
     */
    public void generateHandlebarTable(ArrayList<Handlebar> handlebars) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Serial No.:");
        model.addColumn("Name:");
        model.addColumn("Brand");
        model.addColumn("Cost:");
        model.addColumn("Stock:");
        model.addColumn("Type:");

        Object[] rowData = new Object[6];
        for (int i = 0; i < handlebars.size(); i++) {
            Handlebar handlebar = handlebars.get(i);
            rowData[0] = handlebar.getSerialNumber();
            rowData[1] = handlebar.getComponentName();
            rowData[2] = handlebar.getBrandName();
            rowData[3] = handlebar.getCost();
            rowData[4] = handlebar.getStock();
            rowData[5] = handlebar.getType();

            model.addRow(rowData);
        }

        tableHandlebar.setModel(model);
    }

    /**
     * Generates a table containing all the pairs of wheels from the DB.
     *
     * @param pairsOfWheels
     */
    public void generatePairOfWheelsTable(ArrayList<PairOfWheels> pairsOfWheels) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Serial No.:");
        model.addColumn("Pair Name:");
        model.addColumn("Brand:");
        model.addColumn("Cost:");
        model.addColumn("Stock:");
        model.addColumn("Diameter:");
        model.addColumn("Tyres:");
        model.addColumn("Brakes:");

        Object[] rowData = new Object[8];
        for(int i = 0; i < pairsOfWheels.size(); i++) {
            PairOfWheels wheel = pairsOfWheels.get(i);
            rowData[0] = wheel.getSerialNumber();
            rowData[1] = wheel.getComponentName();
            rowData[2] = wheel.getBrandName();
            rowData[3] = wheel.getCost();
            rowData[4] = wheel.getStock();
            rowData[5] = wheel.getDiameter();
            rowData[6] = wheel.getTyreType();
            rowData[7] = wheel.getBrakeType();

            model.addRow(rowData);
        }

        tablePairsOfWheels.setModel(model);
    }

    public void setFonts() {
        btnAddFramesets.setFont(f3);
        btnLogout.setFont(f3);
        btnBack.setFont(f3);
        btnAddHandlebars.setFont(f3);
        btnUpdateFS.setFont(f3);
        btnAddPairOfWheels.setFont(f3);
        btnUpdateHB.setFont(f3);
        btnUpdatePW.setFont(f3);
        btnDeleteFS.setFont(f3);
        btnDeleteHB.setFont(f3);
        btnDeletePW.setFont(f3);
        tableFrameset.setFont(f3);
        tableHandlebar.setFont(f3);
        tablePairsOfWheels.setFont(f3);
    }
}