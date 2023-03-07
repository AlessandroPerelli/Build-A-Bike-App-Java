/**
 * Main.java
 *
 * Main runner of the complete system - the bike builder app.
 */

import gui.MainDashboard;

public class Main {
    public static void main(String[] args) {
        // Run GUI from here.
        MainDashboard m = new MainDashboard();
        m.setVisible(true);
        System.out.println("Compiled with success!");
    }
}
