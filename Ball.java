import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

class Ball {
    private int x;
    private int y;
    private int radius;
    private Color color;
    private int speedX;
    private int speedY;
    public static final int INITIAL_SPEED = 8; // Increased base initial speed again
    private double speedMultiplier = 1.0; // Multiplier for speed

    public Ball(int x, int y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        initializeVelocity();
    }

    public void initializeVelocity() {
        Random random = new Random();
        int angle = random.nextInt(120) + 30; // Ensure initial angle is between 30 and 150 degrees
        if (random.nextBoolean()) {
            angle = -angle; // Randomize direction
        }
        speedX = (int) (Math.cos(Math.toRadians(angle)) * INITIAL_SPEED);
        speedY = (int) (Math.sin(Math.toRadians(angle)) * INITIAL_SPEED);
    }

    public void move() {
        x += speedX * speedMultiplier;
        y += speedY * speedMultiplier;
    }

    public void reverseYVelocity() {
        speedY = -speedY;
    }

    public void reverseXVelocity() {
        speedX = -speedX;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public void increaseSpeed() {
        speedMultiplier += 0.2; // Increased speed multiplier increment
        if (speedMultiplier < 0.5) speedMultiplier = 0.5; // Ensure speed doesn't go too low
        if (speedMultiplier > 3.0) speedMultiplier = 3.0; // Increased max speed limit
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void resetSpeed() {
        speedMultiplier = 1.0;
    }
}