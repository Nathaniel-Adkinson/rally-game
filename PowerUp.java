import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

class PowerUp {
    private int x;
    private int y;
    private int width = 20;
    private int height = 20;
    private Color color;
    private PowerUpType type;

    public enum PowerUpType {
        INCREASE_PADDLE_SIZE
        // Add other power-up types here
    }

    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.color = Color.GREEN; // Default color
        this.type = type;
        if (type == PowerUpType.INCREASE_PADDLE_SIZE) {
            this.color = Color.CYAN;
        }
        // Customize color based on type if needed
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PowerUpType getType() {
        return type;
    }
}