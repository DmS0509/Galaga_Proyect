package uptc.edu.co.View;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class EnemyPanel extends JFrame {

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton menuButton;
    private JButton backButton;

    private ImageIcon spiImage;
    private ImageIcon beeImage;
    private ImageIcon bossImage;

    private static final String LEFT_BUTTON_IMAGE = "Galaga-Interfaz-master\\src\\main\\resources\\Images\\boton-izquierda.png";

    public EnemyPanel() {
        setTitle("Enemigos");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        initComponents();
        setVisible(true);
    }

    public void initComponents() {
        getContentPane().setLayout(null); 

        // Crear y agregar el fondo
        JLabel backgroundLabel = new JLabel(
            resizeIcon(new ImageIcon(getClass().getResource("/Images/fondo.jpg")), getWidth(), getHeight())
        );
        
        backgroundLabel.setBounds(0, 0, getWidth(), getHeight());
        getContentPane().add(backgroundLabel);

        // Crear el botón de retroceso
        backButton = new JButton(new ImageIcon(LEFT_BUTTON_IMAGE));
        backButton.setBounds(10, 10, 50, 50);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false); 
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose(); 
            new ControlsPanel(); 
        });
        getContentPane().add(backButton);

        menuButton = createStyledButton("Menu");
        menuButton.setBounds(550, 10, 80, 30);
        menuButton.addActionListener(e -> {
            dispose();
            new MainMenu();
        });
        getContentPane().add(menuButton);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBackground(new Color(0, 179, 179));
        mainPanel.setBounds(70, 70, 500, 350);
        mainPanel.setLayout(null);
        getContentPane().add(mainPanel);

        titleLabel = new JLabel("Enemigos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(20, 10, 200, 40);
        mainPanel.add(titleLabel);
        loadEnemyImages();

        addEnemy(mainPanel, bossImage, "Boss",
                "Este está siempre está protegido por Bee y Spi, hace exactamente todo lo que hacen las demás, cuando le disparas 2 veces, muere.",
                20, 60);
        addEnemy(mainPanel, beeImage, "Bee",
                "Su comportamiento no es muy difícil de adivinar, ella después de salir de la fila, va hacia ti y dispara, sigue de largo y aparece otra vez arriba pero en ocasiones se devuelve.",
                20, 150);
        addEnemy(mainPanel, spiImage, "Spi",
                "Esta es parecida a la anterior, pero a diferencia de la otra esta no se devuelve.",
                20, 240);
        getContentPane().setComponentZOrder(backgroundLabel, getContentPane().getComponentCount() - 1);
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
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        return button;
    }

    public void loadEnemyImages() {
        bossImage = loadImageResource("/Images/boss.png", 50, 50);
        beeImage = loadImageResource("/Images/bee.png", 50, 50);
        spiImage = loadImageResource("/Images/spi.png", 50, 50);
    }

    private ImageIcon loadImageResource(String path, int width, int height) {
        try {
            InputStream imgStream = getClass().getResourceAsStream(path);
            if (imgStream == null) {
                System.err.println("No se encontró la imagen: " + path);
                return createPlaceholderIcon(width, height);
            }
            byte[] bytes = imgStream.readAllBytes();
            ImageIcon icon = new ImageIcon(bytes);
            return resizeIcon(icon, width, height);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + path);
            e.printStackTrace();
            return createPlaceholderIcon(width, height);
        }
    }

    private ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.RED);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon == null || icon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
            System.err.println("Error: No se pudo cargar la imagen");
            return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        }
        Image img = icon.getImage();
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();
        return new ImageIcon(resizedImg);
    }

    private void addEnemy(JPanel panel, ImageIcon icon, String name, String description, int x, int y) {
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(x, y, 50, 50);
        panel.add(iconLabel);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setBounds(x + 60, y, 100, 20);
        panel.add(nameLabel);

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setBounds(x + 60, y + 20, 400, 60);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(descriptionArea);
    }

    public static void main(String[] args) {
        new EnemyPanel();
    }
}
