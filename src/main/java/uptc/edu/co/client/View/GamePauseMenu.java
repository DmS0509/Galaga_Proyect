package uptc.edu.co.client.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GamePauseMenu extends JFrame {

    // Constantes para colores
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color PANEL_COLOR = new Color(0, 128, 128);
    private static final Color BORDER_COLOR = new Color(0, 180, 180);
    private static final Color TEXT_COLOR = Color.WHITE;

    // Constantes para fuentes
    private static final Font MENU_OPTION_FONT = new Font("Arial", Font.BOLD, 20);

    // Constantes para el panel de menú
    private static final int MENU_PANEL_WIDTH = 280;
    private static final int MENU_PANEL_HEIGHT = 180;
    private static final int MENU_PANEL_CORNER_RADIUS = 30;

    public GamePauseMenu() {
        initializeFrame();
        initComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Pausa");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(null) {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/Images/fondo.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(mainPanel);

        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel);

        addMenuOptions(menuPanel);

    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), MENU_PANEL_CORNER_RADIUS, MENU_PANEL_CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, MENU_PANEL_CORNER_RADIUS, MENU_PANEL_CORNER_RADIUS);
                g2.dispose();
            }
        };

        int x = (getWidth() - MENU_PANEL_WIDTH) / 2;
        int y = (getHeight() - MENU_PANEL_HEIGHT) / 2 - 50;
        panel.setBounds(x, y, MENU_PANEL_WIDTH, MENU_PANEL_HEIGHT);
        panel.setOpaque(false);
        return panel;
    }

    private void addMenuOptions(JPanel panel) {
        String[] options = {"Continuar", "Reiniciar", "Volver al menu"};
        int optionHeight = 35;
        int startY = 25;
        int spacing = 15;

        for (int i = 0; i < options.length; i++) {
            JButton optionButton = createMenuButton(options[i]);
            int x = (panel.getWidth() - 180) / 2;
            int y = startY + i * (optionHeight + spacing);
            optionButton.setBounds(x, y, 180, optionHeight);

            if (options[i].equals("Volver al menu")) {
                optionButton.addActionListener(e -> {
                    dispose();
                    SwingUtilities.invokeLater(MainMenu::new);
                });
            }

            panel.add(optionButton);
        }
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MENU_OPTION_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(PANEL_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(TEXT_COLOR);
            }
        });

        if (text.equals("Continuar")) {
            button.addActionListener(e -> {
                dispose();
            });
        } else if (text.equals("Reiniciar")) {
            button.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Reiniciando juego...");
                dispose();
            });
        }

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GamePauseMenu());
    }
}
