package uptc.edu.co.client.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoPanel extends JFrame {
    private static final int FRAME_WIDTH = 700;
    private static final int FRAME_HEIGHT = 600;
    private static final int PANEL_X = 75;
    private static final int PANEL_Y = 30;
    private static final int PANEL_WIDTH = 550;
    private static final int PANEL_HEIGHT = 500;
    private static final int BUTTON_SIZE = 45;
    private static final int LEFT_BUTTON_X = 20;
    private static final int RIGHT_BUTTON_X = 635;
    private static final int BUTTON_Y = 20;
    private static final int STAR_COUNT = 60;

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color PANEL_COLOR = new Color(0, 100, 100);
    private static final Color BORDER_COLOR = new Color(0, 150, 150);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color STAR_COLOR = new Color(200, 200, 200);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 40);
    private static final Font SECTION_FONT = new Font("Arial", Font.BOLD, 10);
    private static final Font RULES_FONT = new Font("Arial", Font.PLAIN, 10);
    private static final String LEFT_BUTTON_IMAGE = "/Images/boton-izquierda.png";
    private static final String RIGHT_BUTTON_IMAGE = "/Images/boton-derecha.png";

    public InfoPanel() {
        initializeFrame();
        initComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Reglas");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);

        JButton backButton = createNavigationButton(LEFT_BUTTON_IMAGE, LEFT_BUTTON_X, BUTTON_Y);
        JButton nextButton = createNavigationButton(RIGHT_BUTTON_IMAGE, RIGHT_BUTTON_X, BUTTON_Y);

        mainPanel.add(backButton);
        mainPanel.add(nextButton);

        JPanel rulesPanel = createRulesPanel();
        mainPanel.add(rulesPanel);

        addTitleToPanel(rulesPanel);
        addRulesContentToPanel(rulesPanel);
        addStars(mainPanel);

        backButton.addActionListener(e -> dispose());
        nextButton.addActionListener(e -> navigateTo(new ControlsPanel()));
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    private JButton createNavigationButton(String imagePath, int x, int y) {
        URL imageUrl = InfoPanel.class.getResource(imagePath);
        if (imageUrl == null) {
            System.err.println("No se pudo cargar la imagen: " + imagePath);
            return new JButton("?");
        }
    
        ImageIcon icon = new ImageIcon(imageUrl);
        Image scaledImage = scaleImage(icon.getImage(), 48, 48);
    
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setBounds(x, y, BUTTON_SIZE, BUTTON_SIZE);
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

    private JPanel createRulesPanel() {
        JPanel panel = new JPanel() {
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
        panel.setOpaque(false);
        return panel;
    }

    private void addTitleToPanel(JPanel panel) {
        JLabel title = new JLabel("Reglas");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
    }

    private void addRulesContentToPanel(JPanel panel) {
        // Objetivo
        addSectionTitle(panel, "Objetivo:");
        addRuleText(panel, "El objetivo principal es destruir todas las oleadas de alienígenas");
        addRuleText(panel, "que aparecen en la pantalla.");
        panel.add(Box.createVerticalStrut(10));

        // Mecánica del juego
        addSectionTitle(panel, "Mecánica del juego:");
        addRuleText(panel, "• Control: El jugador controla una nave espacial que se mueve");
        addRuleText(panel, "  horizontalmente en la parte inferior de la pantalla.");
        addRuleText(panel, "• Disparo: La nave dispara misiles hacia arriba para destruir a los");
        addRuleText(panel, "  alienígenas.");
        addRuleText(panel, "• Oleadas de enemigos: Los alienígenas vuelan en formaciones y");
        addRuleText(panel, "  atacan en oleadas.");
        addRuleText(panel, "• Puntuación: Se obtienen puntos por destruir a los alienígenas. Los");
        addRuleText(panel, "  enemigos más difíciles otorgan más puntos.");
        addRuleText(panel, "• Niveles: El juego avanza a través de niveles cada vez más difíciles,");
        addRuleText(panel, "  con formaciones de alienígenas más complejas y agresivas.");
        addRuleText(panel, "• Vidas: El jugador comienza con un número limitado de vidas. Perder");
        addRuleText(panel, "  todas las vidas resulta en el fin del juego.");
    }

    private void addSectionTitle(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(SECTION_FONT);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
    }

    private void addRuleText(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(RULES_FONT);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
    }

    private void addStars(JPanel panel) {
        Random random = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            JLabel star = new JLabel("+");
            star.setForeground(STAR_COLOR);
            star.setBounds(random.nextInt(FRAME_WIDTH), random.nextInt(FRAME_HEIGHT), 10, 10);
            panel.add(star);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void navigateTo(JFrame frame) {
        this.dispose();
        frame.setVisible(true);
    }
}
