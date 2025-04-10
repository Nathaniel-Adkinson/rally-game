import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Point;

public class PongGame extends JFrame {

    private GamePanel gamePanel;
    private static final int WIDTH = 900;
    private static final int HEIGHT = 700;

    public PongGame() {
        setTitle("Rally Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window

        gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new PongGame();
    }

    // Inner class for the game panel
    static class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {

        private static final int WIDTH = 900;
        private static final int HEIGHT = 700;
        private Paddle playerPaddle;
        private Paddle computerPaddle;
        private Ball ball;
        private static final int PADDLE_WIDTH = 10;
        private static final int PADDLE_HEIGHT = 60;
        private static final int PADDLE_OFFSET = 30;
        private static final int BALL_RADIUS = 10;
        private static final int GAME_SPEED = 15; // Milliseconds between each frame
        private static final int COMPUTER_PADDLE_SPEED = 7; // Increased base computer paddle speed
        private static final int DEAD_ZONE = 10; // Added dead zone
        private int gameScore = 0; // Single game score
        private Font scoreFont = new Font("Arial", Font.PLAIN, 36); // Font for the score
        private Font gameOverFont = new Font("Arial", Font.BOLD, 60); // Font for Game Over message
        private boolean gameOver = false; // Flag to indicate if the game is over
        private int lastSpeedIncreaseScore = 0; // Track when speed was last increased (for paddles)

        private Timer gameTimer;
        private boolean isUpKeyPressed = false;
        private boolean isDownKeyPressed = false;

        private List<PowerUp> powerUps = new ArrayList<>();
        private Random random = new Random();
        private int powerUpSpawnInterval = 300; // In game frames (adjust as needed)
        private int frameCount = 0;

        public GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this); // Add mouse listener
            initializePaddles();
            initializeBall();

            gameTimer = new Timer(GAME_SPEED, this);
            gameTimer.start();
        }

        private void initializePaddles() {
            // Create the player's paddle on the left
            playerPaddle = new Paddle(
                    PADDLE_OFFSET,
                    HEIGHT / 2 - PADDLE_HEIGHT / 2,
                    PADDLE_WIDTH,
                    PADDLE_HEIGHT,
                    Color.WHITE
            );

            // Create the computer's paddle on the right
            computerPaddle = new Paddle(
                    WIDTH - PADDLE_OFFSET - PADDLE_WIDTH,
                    0, // Set the Y-position to the top
                    PADDLE_WIDTH,
                    HEIGHT - 50, // Make it almost the full height
                    Color.WHITE
            );
        }

        private void initializeBall() {
            // Create the ball in the center of the screen
            ball = new Ball(
                    WIDTH / 2,
                    HEIGHT / 2,
                    BALL_RADIUS,
                    Color.WHITE
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!gameOver) {
                playerPaddle.draw(g);
                computerPaddle.draw(g);
                ball.draw(g);

                // Draw power-ups
                for (PowerUp powerUp : powerUps) {
                    powerUp.draw(g);
                }

                // Draw the score
                g.setColor(Color.WHITE);
                g.setFont(scoreFont);
                String scoreText = String.valueOf(gameScore);
                int textWidth = g.getFontMetrics().stringWidth(scoreText);
                g.drawString(scoreText, WIDTH / 2 - textWidth / 2, 50);
            } else {
                // Draw Game Over message
                g.setColor(Color.RED);
                g.setFont(gameOverFont);
                String gameOverText = "Game Over";
                int gameOverTextWidth = g.getFontMetrics().stringWidth(gameOverText);
                g.drawString(gameOverText, WIDTH / 2 - gameOverTextWidth / 2, HEIGHT / 2);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameOver) {
                ball.move();
                checkWallCollision();
                checkPaddleCollision();
                updatePaddlePositions();
                updateComputerPaddle();

                // Power-up generation
                frameCount++;
                if (frameCount % powerUpSpawnInterval == 0) {
                    spawnPowerUp();
                }

                // Check if the ball went off the left side (player missed)
                if (ball.getX() < -ball.getRadius()) {
                    gameScore -= 5; // Decrease score by 5
                    initializeBall(); // Reset the ball to the center
                    resetSpeeds(); // Reset speeds on miss for a fresh start
                    playerPaddle.resetHeight(); // Reset player's paddle height
                }
                // Check if the ball went off the right side
                else if (ball.getX() > WIDTH + ball.getRadius()) {
                    initializeBall(); // Reset the ball to the center (no score change for computer miss)
                    resetSpeeds(); // Reset speeds on miss for a fresh start
                }

                // Check for Game Over
                if (gameScore < 0) {
                    gameOver = true;
                    gameTimer.stop(); // Stop the game timer
                }

                repaint();
            }
        }

        private void spawnPowerUp() {
            int powerUpX = random.nextInt(WIDTH - 50) + 25;
            int powerUpY = random.nextInt(HEIGHT - 100) + 75;
            PowerUp.PowerUpType type = PowerUp.PowerUpType.INCREASE_PADDLE_SIZE; // For now, only one type
            powerUps.add(new PowerUp(powerUpX, powerUpY, type));
        }

        private void activatePowerUp(int mouseX, int mouseY) {
            for (int i = 0; i < powerUps.size(); i++) {
                PowerUp powerUp = powerUps.get(i);
                if (powerUp.getBounds().contains(mouseX, mouseY)) {
                    PowerUp.PowerUpType type = powerUp.getType();
                    if (type == PowerUp.PowerUpType.INCREASE_PADDLE_SIZE) {
                        playerPaddle.increaseHeight(20); // Increase height by 20 pixels
                        // We might want to set a timer to revert this later
                    }
                    powerUps.remove(i); // Remove the activated power-up
                    break; // Only activate one power-up per click
                }
            }
        }

        private void checkWallCollision() {
            // Check for collision with the top wall
            if (ball.getY() - ball.getRadius() < 0) {
                ball.setY(ball.getRadius());
                ball.reverseYVelocity();
            }

            // Check for collision with the bottom wall
            if (ball.getY() + ball.getRadius() > HEIGHT) {
                ball.setY(HEIGHT - ball.getRadius());
                ball.reverseYVelocity();
            }
        }

        private void checkPaddleCollision() {
            // Collision with the player's paddle
            if (ball.getX() - ball.getRadius() < playerPaddle.getX() + playerPaddle.getWidth() &&
                ball.getX() + ball.getRadius() > playerPaddle.getX() &&
                ball.getY() - ball.getRadius() < playerPaddle.getY() + playerPaddle.getHeight() &&
                ball.getY() + ball.getRadius() > playerPaddle.getY()) {
                ball.reverseXVelocity();
                double relativeIntersectY = (playerPaddle.getY() + (double)playerPaddle.getHeight() / 2) - ball.getY();
                double normalizedRelativeIntersectionY = (relativeIntersectY / (playerPaddle.getHeight() / 2));
                ball.setSpeedY((int) (normalizedRelativeIntersectionY * Ball.INITIAL_SPEED));
                gameScore++; // Increment the score only for the player's paddle
                ball.increaseSpeed(); // Increase ball speed on every point
                if (gameScore > 0 && gameScore % 5 == 0 && gameScore > lastSpeedIncreaseScore) {
                    playerPaddle.increaseSpeed();
                    computerPaddle.increaseSpeed();
                    lastSpeedIncreaseScore = gameScore;
                }
            }
            // Collision with the computer's paddle
            else if (ball.getX() + ball.getRadius() > computerPaddle.getX() &&
                     ball.getX() - ball.getRadius() < computerPaddle.getX() + computerPaddle.getWidth() &&
                     ball.getY() - ball.getRadius() < computerPaddle.getY() + computerPaddle.getHeight() &&
                     ball.getY() + ball.getRadius() > computerPaddle.getY()) {
                ball.reverseXVelocity();
                double relativeIntersectY = (computerPaddle.getY() + (double)computerPaddle.getHeight() / 2) - ball.getY();
                double normalizedRelativeIntersectionY = (relativeIntersectY / (computerPaddle.getHeight() / 2));
                ball.setSpeedY((int) (normalizedRelativeIntersectionY * Ball.INITIAL_SPEED));
                // No score increment here for the computer's paddle
            }
        }

        private void updatePaddlePositions() {
            if (isUpKeyPressed) {
                playerPaddle.moveUp();
            }
            if (isDownKeyPressed) {
                playerPaddle.moveDown(HEIGHT);
            }
        }

        private void updateComputerPaddle() {
            int paddleCenterY = computerPaddle.getY() + computerPaddle.getHeight() / 2;

            if (ball.getY() < paddleCenterY - DEAD_ZONE) {
                computerPaddle.moveUp();
            } else if (ball.getY() > paddleCenterY + DEAD_ZONE) {
                computerPaddle.moveDown(HEIGHT);
            }
        }

        private void resetSpeeds() {
            ball.resetSpeed();
            playerPaddle.resetSpeed();
            computerPaddle.resetSpeed();
            lastSpeedIncreaseScore = 0;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            activatePowerUp(e.getX(), e.getY()); // Pass the mouse click coordinates
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // Not used
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // Not used
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // Not used
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // Not used
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Not used
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_UP) {
                isUpKeyPressed = true;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                isDownKeyPressed = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_UP) {
                isUpKeyPressed = false;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                isDownKeyPressed = false;
            }
        }
    }
}