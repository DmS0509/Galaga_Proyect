package co.edu.uptc.host.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import co.edu.uptc.host.controller.AudioController;

public class ControlsPanel extends JFrame {

   // Constantes para dimensiones de imágenes
    private static final int SCALE_INCREASE = 20;
    private static final int EXTRA_INCREASE = 20;
    private static final int SHIFT_WIDTH = 60 + SCALE_INCREASE;
    private static final int SHIFT_HEIGHT = 45 + SCALE_INCREASE;
    private static final int WASD_ARROWS_WIDTH = SHIFT_WIDTH + EXTRA_INCREASE;
    private static final int WASD_ARROWS_HEIGHT = SHIFT_HEIGHT + EXTRA_INCREASE;
    private static final int SPACE_WIDTH = 100;
    private static final int SPACE_HEIGHT = SHIFT_HEIGHT;

    // Constantes para colores
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color PANEL_COLOR = new Color(0, 100, 100);
    private static final Color BORDER_COLOR = new Color(0, 150, 150);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color STAR_COLOR = new Color(200, 200, 200);

    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 40);
    private static final Font PLAYER_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font CONTROL_FONT = new Font("Arial", Font.PLAIN, 14);

    private static final String LEFT_BUTTON_IMAGE = "/Images/boton-izquierda.png";
    private static final String RIGHT_BUTTON_IMAGE = "/Images/boton-derecha.png";
    private static final String WASD_IMAGE = "src/main/resources/Images/wasd.png";
    private static final String SPACE_IMAGE = "src/main/resources/Images/espace.png";
    private static final String ARROWS_IMAGE = "src/main/resources/Images/flechas.png";
    private static final String SHIFT_IMAGE = "src/main/resources/Images/shift derecho.png";

    private AudioController audioManager; // Instancia de AudioManager

    public ControlsPanel() {
        // Inicializa AudioManager y carga la música
        audioManager = new AudioController();
        audioManager.loadSound("theme", "/resources/Galaga Theme.mp3"); // Carga el tema
        audioManager.loopSound("theme"); // Comienza la reproducción en bucle

        initializeFrame();
        initComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Controles");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);

        JButton backButton = createNavigationButton(LEFT_BUTTON_IMAGE, 20, 20);
        JButton nextButton = createNavigationButton(RIGHT_BUTTON_IMAGE, 620, 20);

        mainPanel.add(backButton);
        mainPanel.add(nextButton);

        JPanel controlsPanel = createControlsPanel();
        mainPanel.add(controlsPanel);

        addTitleToPanel(controlsPanel);
        addPlayerControls(controlsPanel, "1 Player", 80, WASD_IMAGE, SPACE_IMAGE);
        addPlayerControls(controlsPanel, "2 Player", 220, ARROWS_IMAGE, SHIFT_IMAGE);

        addStars(mainPanel);

        backButton.addActionListener(e -> {
            audioManager.stopSound("theme"); // Detiene la música al navegar
            navigateTo(new InfoPanel());
        });
        nextButton.addActionListener(e -> {
            audioManager.stopSound("theme"); // Detiene la música al navegar
            navigateTo(new EnemyPanel());
        });
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    private JButton createNavigationButton(String imagePath, int x, int y) {
        URL imageUrl = ControlsPanel.class.getResource(imagePath);
        if (imageUrl == null) {
            System.err.println("No se pudo cargar la imagen: " + imagePath);
            return new JButton("?");
        }

        ImageIcon icon = new ImageIcon(imageUrl);
        Image scaledImage = scaleImage(icon.getImage(), 48, 48);

        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setBounds(x, y, 45, 45);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setVisible(true);
        return button;
    }

    private Image scaleImage(Image image, int width, int height) {
        try {
            return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error al escalar imagen: " + e.getMessage());
            return image;
        }
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        panel.setBounds(75, 70, 550, 370);
        panel.setOpaque(false);
        return panel;
    }

    private void addTitleToPanel(JPanel panel) {
        JLabel titleLabel = new JLabel("Controles");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBounds(200, 20, 200, 40);
        panel.add(titleLabel);
    }

    private void addPlayerControls(JPanel panel, String playerName, int yOffset, String movementImagePath, String actionImagePath) {
        // etiqueta del jugador
        JLabel playerLabel = new JLabel(playerName);
        playerLabel.setFont(PLAYER_FONT);
        playerLabel.setForeground(TEXT_COLOR);
        playerLabel.setBounds(50, yOffset, 150, 30);
        panel.add(playerLabel);

        // etiquetas de controles
        addControlLabel(panel, "MOVIMIENTO A LA IZQ.", 50, yOffset + 40);
        addControlLabel(panel, "MOVIMIENTO A LA DER.", 350, yOffset + 40);
        addControlLabel(panel, "DISPARAR", 350, yOffset + 80);

        int verticalOffset = 25;

        addControlImage(panel, movementImagePath, WASD_ARROWS_WIDTH, WASD_ARROWS_HEIGHT, 210, yOffset - 23 + verticalOffset);

        int actionImageWidth = playerName.equals("1 Player") ? SPACE_WIDTH : SHIFT_WIDTH;
        int actionImageHeight = playerName.equals("1 Player") ? SPACE_HEIGHT : SHIFT_HEIGHT;
        addControlImage(panel, actionImagePath, actionImageWidth, actionImageHeight, 212, yOffset + 20 + verticalOffset + 14);
    }

    private void addControlLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(CONTROL_FONT);
        label.setForeground(TEXT_COLOR);
        label.setBounds(x, y, 150, 20);
        panel.add(label);
    }

    private void addControlImage(JPanel panel, String imagePath, int width, int height, int x, int y) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = scaleImage(icon.getImage(), width, height);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBounds(x, y, width, height);
        panel.add(imageLabel);
    }

    private void addStars(JPanel panel) {
        Random random = new Random();
        for (int i = 0; i < 40; i++) {
            JLabel star = new JLabel("+");
            star.setForeground(STAR_COLOR);
            star.setBounds(random.nextInt(650), random.nextInt(500), 10, 10);
            panel.add(star);
        }
    }

    private void navigateTo(JFrame frame) {
        this.dispose();
        frame.setVisible(true);
    }
}