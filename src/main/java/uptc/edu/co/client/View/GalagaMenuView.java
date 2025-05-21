package uptc.edu.co.client.View;

import uptc.edu.co.client.controller.AudioController;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GalagaMenuView extends JFrame {

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GalagaMenuView().setVisible(true));
    }
}

class MenuPanel extends JPanel implements java.awt.event.KeyListener {

    private final Font pixelFont = new Font("Monospaced", Font.BOLD, 20);
    private int selectedOption = 0;
    private Image galagaLogo;
    private AudioController audioManager; // Instancia de AudioManager

    public MenuPanel() {
        try {
            galagaLogo = new ImageIcon(getClass().getResource("/Images/galagaLogo.png")).getImage();
        } catch (Exception e) {
            galagaLogo = null;
        }

        audioManager = new AudioController();
        audioManager.loadSound("theme", "/music/theme.wav");
        audioManager.loopSound("theme"); // Comienza a reproducir la música en bucle
        setFocusable(true);
        requestFocusInWindow();
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
            int scaledWidth = 240; // Ancho deseado
            int scaledHeight = (logoHeight * scaledWidth) / logoWidth;
            int xCenter = (getWidth() - scaledWidth) / 2;
            int yPosition = 35; // Posición vertical fija
            g.drawImage(galagaLogo, xCenter, yPosition, scaledWidth, scaledHeight, this);
        }

        g.drawString((selectedOption == 0 ? "▶ " : "  ") + "1 PLAYER", 170, 180);
        g.drawString((selectedOption == 1 ? "▶ " : "  ") + "2 PLAYERS", 170, 210);
        g.drawString((selectedOption == 2 ? "▶ " : "  ") + "VOLVER", 170, 240); // Nueva opción
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedOption = (selectedOption - 1 + 3) % 3; // Navegar hacia arriba (circular entre 0, 1, 2)
                break;
            case KeyEvent.VK_DOWN:
                selectedOption = (selectedOption + 1) % 3; // Navegar hacia abajo (circular entre 0, 1, 2)
                break;
            case KeyEvent.VK_ENTER:
                if (selectedOption == 0) {
                    // Abrir GamePauseMenu al seleccionar "1 PLAYER"
                    audioManager.stopSound("theme"); // Detiene la música al cambiar de pantalla
                    new GamePauseMenu();
                } else if (selectedOption == 1) {
                    // Abrir GamePauseMenu al seleccionar "2 PLAYERS"
                    audioManager.stopSound("theme"); // Detiene la música al cambiar de pantalla
                    new GamePauseMenu();
                } else if (selectedOption == 2) {
                    // Volver al menú principal
                    audioManager.stopSound("theme"); // Detiene la música al cambiar de pantalla
                    SwingUtilities.getWindowAncestor(this).dispose(); // Cierra la ventana actual
                    new MainMenu(); // Abre la ventana principal
                }
                break;
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