package co.edu.uptc.host.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics; // Import added
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import co.edu.uptc.host.controller.AudioController;

public class InGamePanel extends JFrame implements KeyListener {

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color HIGH_SCORE_COLOR = Color.RED;
    private static final Color LIVES_COLOR = Color.GREEN;

    private static Font SCORE_FONT;
    private static Font INFO_FONT;

    private GameAreaPanel gameArea;
    private InfoPanel infoPanel;
    
    private static AudioController audioManager;
    private static boolean audioInitialized = false;

    public InGamePanel() {
        loadCustomFonts();

        if (audioManager == null) {
            audioManager = new AudioController();
        }
        
        if (!audioInitialized) {
            audioManager.loadSound("game", "/music/sound-game.wav");
            audioManager.setVolume("game", 0.01f);
            audioManager.loopSound("game");
            audioInitialized = true;
        } else {
            if (!audioManager.isPlaying("game")) {
                audioManager.resumeSound("game");
            }
        }

        initializeFrame();
        initComponents();
        setVisible(true);
    }

    private void loadCustomFonts() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Jersey10-Regular.ttf");
            if (is != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseFont);

                SCORE_FONT = baseFont.deriveFont(Font.BOLD, 24f);
                INFO_FONT = baseFont.deriveFont(Font.BOLD, 18f);
            } else {
                System.err.println("No se encontró la fuente en /fonts/Jersey10-Regular.ttf. Usando fuentes por defecto.");
                SCORE_FONT = new Font("Monospaced", Font.BOLD, 24);
                INFO_FONT = new Font("Monospaced", Font.BOLD, 18);
            }
        } catch (Exception e) {
            System.err.println("Error cargando la fuente: " + e.getMessage());
            SCORE_FONT = new Font("Monospaced", Font.BOLD, 24);
            INFO_FONT = new Font("Monospaced", Font.BOLD, 18);
        }
    }

    // Método público para acceder al InfoPanel y actualizar el score
    public void updateScore(int newScore) {
        //infoPanel.updateScore(newScore);
    }

    // Método público para obtener el score actual
    public int getCurrentScore() {
        return 0;
        // return infoPanel.getCurrentScore();
    }

    private void initializeFrame() {
        setTitle("Galaga");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        addKeyListener(this);
        setFocusable(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        gameArea = new GameAreaPanel();
        add(gameArea, BorderLayout.CENTER);

        infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.EAST);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
            this.setVisible(false);
            new GamePauseMenu(this);
        }

        // Método para actualizar el score desde el back
        //public void updateScore(int newScore) {
            // this.currentScore = newScore;
            //repaint(); // Redibuja el panel para mostrar el nuevo score
        //}

        // Getter para obtener el score actual
        //public int getCurrentScore() {
            //return currentScore;
        //}
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Panel del área de juego
    class GameAreaPanel extends JPanel {
        private Image backgroundImage;
        private Image menuIcon;

        public GameAreaPanel() {
            setPreferredSize(new Dimension(600, 600));
            setBackground(BACKGROUND_COLOR);
            loadImages();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getX() <= 50 && e.getY() <= 40) {
                        InGamePanel.this.setVisible(false);
                        new GamePauseMenu(InGamePanel.this);
                    }
                }
            });
        }

        private void loadImages() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/Images/fondo.jpg")).getImage();
                menuIcon = new ImageIcon(getClass().getResource("/Images/menu.png")).getImage();
            } catch (Exception e) {
                System.err.println("Error cargando imágenes: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                drawStars(g2d);
            }

            if (menuIcon != null) {
                g2d.drawImage(menuIcon, 10, 10, 40, 30, this);
            }

            g2d.dispose();
        }

        private void drawStars(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 50; i++) {
                int x = (int) (Math.random() * getWidth());
                int y = (int) (Math.random() * getHeight());
                int size = (int) (Math.random() * 3) + 1;
                g2d.fillOval(x, y, size, size);
            }
        }


    }

    // Panel de información lateral
    class InfoPanel extends JPanel {
        private Image shipImage;
        private int currentScore = 0;

        public InfoPanel() {
            setPreferredSize(new Dimension(200, 600));
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, LIVES_COLOR));
            loadImages();
        }

        private void loadImages() {
            try {
                shipImage = new ImageIcon(getClass().getResource("/Images/nave2.png")).getImage();
            } catch (Exception e) {
                System.err.println("Error cargando imagen de nave: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawScoreInfo(g2d);

            drawShip(g2d);

            g2d.dispose();
        }

        private void drawScoreInfo(Graphics2D g2d) {
            int yPos = 50;
            int leftMargin = 10;


            g2d.setColor(HIGH_SCORE_COLOR);
            g2d.setFont(SCORE_FONT);

            String highScoreText = "High";
            g2d.drawString(highScoreText, leftMargin, yPos);

            yPos += 30;
            String scoreText = "Score";
            g2d.drawString(scoreText, leftMargin, yPos);

            yPos += 50;
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(INFO_FONT);

            String currentScoreText = String.valueOf(currentScore);
            g2d.drawString(currentScoreText, leftMargin, yPos);

            yPos += 80;
            g2d.setColor(HIGH_SCORE_COLOR);
            String livesText = "1 UP";
            g2d.drawString(livesText, leftMargin, yPos);
        }

        private void drawShip(Graphics2D g2d) {
            int shipY = getHeight() - 150;
            int shipSize = 60;
            int shipX = (getWidth() - shipSize) / 2;

            if (shipImage != null) {
                g2d.drawImage(shipImage, shipX, shipY, shipSize, shipSize, this);
            }
        }


    }

}
