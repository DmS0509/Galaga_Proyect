package co.edu.uptc.host.view;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import co.edu.uptc.server.network.ClientAction;

public class InGamePanel extends JFrame implements KeyListener {

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color HIGH_SCORE_COLOR = Color.RED;
    private static final Color LIVES_COLOR = Color.GREEN;
    private GameScreen gameScreen;
    private static Font SCORE_FONT;
    private static Font INFO_FONT;
    private GamePauseMenu pauseMenu;
    private GameAreaPanel gameArea; // Declaración de GameAreaPanel
    private InfoPanel infoPanel; // Declaración de InfoPanel (si lo usas para la puntuación, etc.)

    private int currentScore = 0;
    private int highScore = 10000;
    private int lives = 3;
    private Image playerLifeSprite; // Para mostrar las vidas

    public InGamePanel(GameScreen gameScreen) { // Recibe GameScreen como parámetro
        System.out.println("InGamePanel: Constructor iniciado.");
        this.gameScreen = gameScreen;

        loadCustomFonts();
        loadSprites(); // Nuevo método para cargar sprites (ej. vida del jugador)

        setTitle("Galaga");
        setSize(800, 600); // Tamaño inicial
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // Centrar la ventana
        setLayout(new BorderLayout()); // Usa BorderLayout para organizar paneles

        System.out.println("InGamePanel: Inicializando GameAreaPanel...");
        gameArea = new GameAreaPanel();
        gameArea.setLayout(new BorderLayout()); // GameAreaPanel también usa BorderLayout para GameScreen

        if (this.gameScreen != null) {
            gameArea.add(this.gameScreen, BorderLayout.CENTER); // GameScreen ocupa el centro de GameAreaPanel
            System.out.println("InGamePanel: GameScreen añadido a GameAreaPanel.");
        } else {
            System.err.println("InGamePanel: GameScreen es nulo. No se puede añadir.");
        }

        add(gameArea, BorderLayout.CENTER); // GameAreaPanel ocupa el centro de InGamePanel

        setFocusable(true);
        requestFocusInWindow(); // Solicita el foco inicial al JFrame

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Clic derecho para pausa
                    if (gameScreen.getGameClient() != null) {
                        gameScreen.getGameClient().sendAction(ClientAction.PAUSE_GAME);
                        System.out.println("InGamePanel: Enviando acción de PAUSE_GAME al servidor.");
                    }

                    // Abrir el menú de pausa
                    if (pauseMenu == null) {
                        pauseMenu = new GamePauseMenu(InGamePanel.this, gameScreen.getGameClient());
                        System.out.println("InGamePanel: GamePauseMenu instanciado.");
                    }
                    pauseMenu.setVisible(true);
                    System.out.println("InGamePanel: GamePauseMenu visible.");
                    gameScreen.getGameClient().sendAction(co.edu.uptc.server.network.ClientAction.PAUSE_GAME);
                }
            }
        });

        System.out.println("InGamePanel: Constructor completado. Listo para ser visible.");
    }

    private void loadCustomFonts() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Jersey10-Regular.ttf");
            if (is == null) {
                System.err.println("InGamePanel: No se encontró el recurso de la fuente: /fonts/PressStart2P-Regular.ttf. Usando fuentes por defecto.");
                SCORE_FONT = new Font("Monospaced", Font.BOLD, 24); // Fuente de fallback
                INFO_FONT = new Font("Monospaced", Font.PLAIN, 18); // Fuente de fallback
            } else {
                SCORE_FONT = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
                // Es buena práctica reiniciar el stream si se va a leer de nuevo para otra fuente o tamaño
                is = getClass().getResourceAsStream("/fonts/PressStart2P-Regular.2ttf"); // Asegúrate de que esta ruta sea correcta o usa deriveFont
                if (is != null) {
                    INFO_FONT = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(18f);
                } else {
                    INFO_FONT = SCORE_FONT.deriveFont(18f);
                }
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(SCORE_FONT);
                ge.registerFont(INFO_FONT);
                System.out.println("InGamePanel: Fuentes personalizadas cargadas correctamente.");
            }
        } catch (Exception e) {
            System.err.println("InGamePanel: Error al cargar las fuentes personalizadas: " + e.getMessage());
            e.printStackTrace();
            SCORE_FONT = new Font("Monospaced", Font.BOLD, 24); // Fuente de fallback
            INFO_FONT = new Font("Monospaced", Font.PLAIN, 18); // Fuente de fallback
        }
    }

    private void loadSprites() {
        try {
            playerLifeSprite = new ImageIcon(getClass().getResource("/Images/naveGalaga.png")).getImage();
            if (playerLifeSprite == null) {
                System.err.println("InGamePanel: No se pudo cargar el sprite de vida del jugador. Verifique la ruta /Images/naveGalaga.png");
            } else {
                System.out.println("InGamePanel: Sprite de vida del jugador cargado correctamente.");
            }
        } catch (Exception e) {
            System.err.println("InGamePanel: Error al cargar el sprite de vida del jugador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateScore(int score) {
        this.currentScore = score;
    }

    public void updateLives(int lives) {
        this.lives = lives;
    }

    public class GameAreaPanel extends JPanel {
        public GameAreaPanel() {
            setBackground(BACKGROUND_COLOR); // Fondo negro para el área de juego
            setLayout(new BorderLayout()); // Para que GameScreen se expanda
            setFocusable(true); // Necesario para la captura de eventos de teclado (si InGamePanel no lo maneja directamente)
            System.out.println("GameAreaPanel: Inicializado.");
            addStars(this); // Añade estrellas al GameAreaPanel
        }

        private void addStars(JPanel panel) {
    java.util.Random random = new java.util.Random();
    int panelWidth = Math.max(1, panel.getWidth());  // Asegura mínimo 1
    int panelHeight = Math.max(1, panel.getHeight()); // Asegura mínimo 1
    
    for (int i = 0; i < 100; i++) {
        JLabel star = new JLabel("•"); // Usamos un punto más visible
        star.setForeground(Color.WHITE);
        star.setFont(new Font("Arial", Font.PLAIN, 8)); // Tamaño más controlado
        star.setBounds(random.nextInt(panelWidth), random.nextInt(panelHeight), 5, 5);
        panel.add(star);
    }
    System.out.println("GameAreaPanel: Añadidas " + 100 + " estrellas.");
}
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawScoreInfo(g2d);
            drawLives(g2d);
            // No dibujes el jugador ni los enemigos aquí, eso lo hace GameScreen.

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

            String currentScoreText = String.valueOf(currentScore); // Usa la variable de instancia
            g2d.drawString(currentScoreText, leftMargin, yPos);

            yPos += 80;
            g2d.setColor(HIGH_SCORE_COLOR);
            String livesLabel = "1 UP";
            g2d.drawString(livesLabel, leftMargin, yPos);
        }

        private void drawLives(Graphics2D g2d) {
            int xPos = 10;
            int yPos = 250; // Posición para dibujar las vidas
            int spriteSize = 24; // Tamaño del sprite de vida (ajusta según tu imagen)
            int spacing = 5;

            g2d.setColor(LIVES_COLOR);
            g2d.setFont(INFO_FONT);
            g2d.drawString("LIVES", xPos, yPos);
            yPos += 30; // Mueve hacia abajo para los sprites

            for (int i = 0; i < lives; i++) { // Usa la variable de instancia
                if (playerLifeSprite != null) {
                    g2d.drawImage(playerLifeSprite, xPos + (i * (spriteSize + spacing)), yPos, spriteSize, spriteSize, null);
                } else {
                    g2d.fillRect(xPos + (i * (spriteSize + spacing)), yPos, spriteSize, spriteSize); // Fallback
                }
            }
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (pauseMenu == null) {
                pauseMenu = new GamePauseMenu(this, gameScreen.getGameClient());
            }
            pauseMenu.setVisible(true);
            System.out.println("InGamePanel: Escape presionado, mostrando GamePauseMenu.");
            // Pausar el juego en el servidor
            if (gameScreen.getGameClient() != null) {
                gameScreen.getGameClient().sendAction(co.edu.uptc.server.network.ClientAction.PAUSE_GAME);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Normalmente no se envían acciones de "release" a menos que necesites diferenciar
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa comúnmente para juegos de acción
    }
}