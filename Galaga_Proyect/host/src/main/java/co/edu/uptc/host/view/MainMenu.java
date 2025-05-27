package co.edu.uptc.host.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.edu.uptc.host.controller.AudioController;

public class MainMenu extends JFrame {

    private JButton playButton;
    private JButton infoButton;
    private JLabel logoLabel;
    private Font customFont;
    private static AudioController audioManager;
    private static boolean audioInitialized = false;

    public MainMenu() {
        setTitle("Galaga");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        if (audioManager == null) {
            audioManager = new AudioController();
        }
        
        if (!audioInitialized) {
            audioManager.loadSound("carga", "/music/sound-carga.wav");
            audioManager.setVolume("carga", 0.3f);
            audioManager.loopSound("carga");
            audioInitialized = true;
        } else {
            if (!audioManager.isPlaying("carga")) {
                audioManager.resumeSound("carga");
            }
        }

        loadCustomFont();
        initComponents();
        setVisible(true);
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Jersey10-Regular.ttf");
            if (is != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 16f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
            } else {
                System.err.println("No se encontró la fuente en /fonts/Jersey10-Regular.ttf");
            }
        } catch (Exception e) {
            System.err.println("Error cargando la fuente: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/Images/fondo.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Images/galagaLogo.png"));
        logoLabel = new JLabel(logoIcon);
        int logoWidth = logoIcon.getIconWidth();
        int logoHeight = logoIcon.getIconHeight();
        int xCenter = (getWidth() - logoWidth) / 2;
        logoLabel.setBounds(xCenter, 30, logoWidth, logoHeight);
        backgroundPanel.add(logoLabel);

        playButton = createStyledButton("Jugar");
        playButton.setBounds(190, 220, 120, 40);
        backgroundPanel.add(playButton);
        playButton.addActionListener(e -> {
            audioManager.pauseSound("carga");
            dispose(); 
            new GalagaMenuView(); 
        });

        infoButton = createStyledButton("Información");
        infoButton.setBounds(190, 280, 120, 40);
        backgroundPanel.add(infoButton);
        infoButton.addActionListener(e -> new InfoPanel());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int xCenter = (getWidth() - logoLabel.getWidth()) / 2;
                logoLabel.setLocation(xCenter, logoLabel.getY());
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground().darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 153, 153));
        button.setFont(customFont != null ? customFont : new Font("SansSerif", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}