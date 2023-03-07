package gui.staff;
import database.controllers.user.staff.StaffController;
import exceptions.InvalidCredentialsException;
import exceptions.UserNotFoundException;
import gui.AbstractFrame;
import models.user.staff.Staff;
import gui.MainDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffLogin extends AbstractFrame {
    private JButton btnLogin;
    private JPanel loginPanel;
    private JPanel contentPanel;
    private JPanel topMenuPanel;
    private JButton btnBack;
    private JButton btnHome;
    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JPanel bottomPanel;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblTitle;
    private static final StaffController staffController = new StaffController();

    public StaffLogin() {
        add(loginPanel);
        setTitle("Staff Login");
        contentPanel.setBorder(new EmptyBorder(10, 80, 10, 80));

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Staff staff = new Staff();
                String username = tfUsername.getText();
                char[] password = tfPassword.getPassword();

                // Try setting up the password and username.
                try {
                    staff.setUsername(username);
                    staff.setPassword(password);

                    // Try to authenticate the staff user.
                    try {
                        Staff authStaff = staffController.attemptLogIn(staff);

                        if (authStaff != null) {
                            showPopup("Successfully logged in!");

                            // Redirect to the staff menu.
                            StaffMenu staffMenu = new StaffMenu(staff);
                            staffMenu.setVisible(true);
                            dispose();
                        }
                    }
                    catch (UserNotFoundException ex) {
                        showPopup(ex.getMessage());
                    }
                }
                catch (InvalidCredentialsException ex) {
                    showPopup(ex.getMessage());
                }
                catch (Exception ex) {
                    showPopup("An external error occurred. Please try again!");
                }
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

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true);
                dispose();
            }
        });
    }

    public void setFonts() {
        btnHome.setFont(f3);
        btnLogin.setFont(f3);
        lblTitle.setFont(f1);
        lblUsername.setFont(f3);
        lblPassword.setFont(f3);
        tfPassword.setFont(f3);
        tfUsername.setFont(f3);
    }
}
