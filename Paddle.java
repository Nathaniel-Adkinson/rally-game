import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle; // Import the Rectangle class

class Paddle {
    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;
    private int speed = 7; // Increased base paddle speed
    private int originalHeight; // Store the original height

    public Paddle(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.originalHeight = height; // Initialize originalHeight
    }

    public void moveUp() {
        y -= speed;
        if (y < 0) {
            y = 0;
        }
    }

    public void moveDown(int gameHeight) {
        y += speed;
        if (y > gameHeight - height) {
            y = gameHeight - height;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public void increaseSpeed() {
        speed += 2; // Increased paddle speed increment
        if (speed > 15) speed = 15; // Increased max paddle speed
    }

    public void resetSpeed() {
        speed = 7;
    }

    public void increaseHeight(int amount) {
        this.height += amount;
        // You might want to add logic to limit the maximum height
    }

    // Method to reset the paddle height to its original value
    public void resetHeight() {
        this.height = originalHeight;
    }

    // New method to get the paddle's bounding rectangle
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}