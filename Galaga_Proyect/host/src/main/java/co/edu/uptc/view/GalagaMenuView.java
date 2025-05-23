package co.edu.uptc.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GalagaMenuView extends JFrame{

        public GalagaMenuView() {
        setTitle("Galaga Menu");
        setSize(480, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        MenuPanel panel = new MenuPanel();
        add(panel);
        addKeyListener(panel);
        setVisible(true);
    }

}

class MenuPanel extends JPanel implements java.awt.event.KeyListener {

    private final Font pixelFont = new Font("Monospaced", Font.BOLD, 20);
    private int selectedOption = 0;
    private Image galagaLogo;

    public MenuPanel() {
        try {
            galagaLogo = new ImageIcon(getClass().getResource("/Images/galagaLogo.png")).getImage();

        } catch (Exception e) {
            galagaLogo = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);

        g.setColor(Color.WHITE);
        g.setFont(pixelFont);

        if (galagaLogo != null) {
            int logoWidth = galagaLogo.getWidth(this);
            int logoHeight = galagaLogo.getHeight(this);

            int scaledWidth = 240;
            int scaledHeight = (logoHeight * scaledWidth) / logoWidth;

            int xCenter = (getWidth() - scaledWidth) / 2;
            int yPosition = 35;

            g.drawImage(galagaLogo, xCenter, yPosition, scaledWidth, scaledHeight, this);
        }

        g.drawString((selectedOption == 0 ? "▶ " : "  ") + "1 PLAYER", 170, 180);
        g.drawString((selectedOption == 1 ? "▶ " : "  ") + "2 PLAYERS", 170, 210);
        g.drawString((selectedOption == 2 ? "▶ " : "  ") + "VOLVER", 170, 240);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedOption = (selectedOption - 1 + 3) % 3;
                break;
            case KeyEvent.VK_DOWN:
                selectedOption = (selectedOption + 1) % 3;
                break;
            case KeyEvent.VK_ENTER:
                if (selectedOption == 0) {
                    SwingUtilities.getWindowAncestor(this).dispose();
                    new InGamePanel(); 
                } else if (selectedOption == 1) {
                    SwingUtilities.getWindowAncestor(this).dispose();
                    new InGamePanel(); 
                } else if (selectedOption == 2) {
                    SwingUtilities.getWindowAncestor(this).dispose();
                    new MainMenu();
                }   break;
            default:
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}