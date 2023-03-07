package gui;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractFrame extends JFrame {
    Toolkit tk = Toolkit.getDefaultToolkit();
    int xSize = ((int) tk.getScreenSize().getWidth());
    int ySize = ((int) tk.getScreenSize().getWidth());
    int xWidth = (int) (Math.round(xSize * 0.55));
    int yHeight = (int) (Math.round(ySize * 0.45));

    //A set of fonts that are going to be used through all Frames
    protected Font f1 = new Font(Font.SANS_SERIF, Font.PLAIN, xWidth/30);
    protected Font f2 = new Font(Font.SANS_SERIF, Font.PLAIN, xWidth/40);
    protected Font f3 = new Font(Font.SANS_SERIF, Font.PLAIN, xWidth/50);
    protected Font f4 = new Font(Font.SANS_SERIF, Font.PLAIN, xWidth/60);

    public AbstractFrame(){
        setSize(xWidth, yHeight);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Makes a custom popup with suitable size for different screen sizes
     */
    public void showPopup(String message) {
        JPanel optionPanel = new JPanel();
        optionPanel.setSize(xWidth, yHeight);

        JLabel lblPopup = new JLabel();
        lblPopup.setFont(f4);
        lblPopup.setText(message);
        optionPanel.add(lblPopup);

        JOptionPane.showMessageDialog(
                null,
                optionPanel
        );
    }
}
