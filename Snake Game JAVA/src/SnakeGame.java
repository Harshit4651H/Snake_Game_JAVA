import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 25;
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int MAX_BODY_SIZE = (SCREEN_WIDTH * SCREEN_HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private final ArrayList<Point> snakeBody = new ArrayList<>();
    private Point food;
    private int score = 0;
    private char direction = 'R';  // L, R, U, D
    private boolean running = false;
    private Timer timer;
    private Random random;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        random = new Random();
        startGame();
    }

    private void startGame() {
        snakeBody.clear();
        snakeBody.add(new Point(0, 0));  // Snake starting position
        score = 0;
        direction = 'R';
        running = true;
        spawnFood();
        timer = new Timer(100, this);
        timer.start();
    }

    private void spawnFood() {
        int x = random.nextInt(SCREEN_WIDTH / TILE_SIZE) * TILE_SIZE;
        int y = random.nextInt(SCREEN_HEIGHT / TILE_SIZE) * TILE_SIZE;
        food = new Point(x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            draw(g);
        } else {
            gameOver(g);  // Display "Game Over" when the game ends
        }
    }

    private void draw(Graphics g) {
        // Draw the food
        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, TILE_SIZE, TILE_SIZE);

        // Draw the snake
        for (Point p : snakeBody) {
            g.setColor(Color.GREEN);
            g.fillRect(p.x, p.y, TILE_SIZE, TILE_SIZE);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.drawString("Score: " + score, 10, 30);
    }

    private void gameOver(Graphics g) {
        // Set font for "Game Over"
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));

        // Center the "Game Over" text
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Display final score
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        g.drawString("Score: " + score, (SCREEN_WIDTH - metrics.stringWidth("Score: " + score)) / 2, g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    private void move() {
        Point head = snakeBody.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case 'U': newHead.y -= TILE_SIZE; break;
            case 'D': newHead.y += TILE_SIZE; break;
            case 'L': newHead.x -= TILE_SIZE; break;
            case 'R': newHead.x += TILE_SIZE; break;
        }

        // Add the new head and remove the last segment (movement)
        snakeBody.add(0, newHead);
        snakeBody.remove(snakeBody.size() - 1);
    }

    private void checkFood() {
        Point head = snakeBody.get(0);
        if (head.equals(food)) {
            score++;
            snakeBody.add(new Point(-1, -1));  // Grow the snake
            spawnFood();
        }
    }

    private void checkCollisions() {
        Point head = snakeBody.get(0);

        // Check collision with walls
        if (head.x < 0 || head.x >= SCREEN_WIDTH || head.y < 0 || head.y >= SCREEN_HEIGHT) {
            running = false;
        }

        // Check collision with itself
        for (int i = 1; i < snakeBody.size(); i++) {
            if (head.equals(snakeBody.get(i))) {
                running = false;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
