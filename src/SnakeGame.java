import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JFrame implements ActionListener {
    private static final int CELL_SIZE = 20;
    private static final int ROWS = 20;
    private static final int COLS = 20;
    private static final int INIT_SNAKE_LENGTH = 3;
    private static final int DELAY = 150;

    private LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private int dir = KeyEvent.VK_RIGHT; // 初始方向
    private boolean alive = true;
    private Timer timer;
    private Random random = new Random();

    public SnakeGame() {
        setTitle("贪吃蛇小游戏");
        setSize(COLS * CELL_SIZE + 20, ROWS * CELL_SIZE + 45);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int newDir = e.getKeyCode();
                // 不能直接反向
                if ((dir == KeyEvent.VK_UP && newDir == KeyEvent.VK_DOWN) ||
                    (dir == KeyEvent.VK_DOWN && newDir == KeyEvent.VK_UP) ||
                    (dir == KeyEvent.VK_LEFT && newDir == KeyEvent.VK_RIGHT) ||
                    (dir == KeyEvent.VK_RIGHT && newDir == KeyEvent.VK_LEFT)) {
                    return;
                }
                dir = newDir;
            }
        });

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        setContentPane(panel);

        initGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initGame() {
        snake.clear();
        for (int i = INIT_SNAKE_LENGTH - 1; i >= 0; --i)
            snake.add(new Point(i, 0));
        spawnFood();
        alive = true;
        dir = KeyEvent.VK_RIGHT;
    }

    private void spawnFood() {
        while (true) {
            int x = random.nextInt(COLS);
            int y = random.nextInt(ROWS);
            food = new Point(x, y);
            if (!snake.contains(food)) break;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!alive) return;
        Point head = snake.getFirst();
        Point next = new Point(head);

        switch (dir) {
            case KeyEvent.VK_UP:    next.y--; break;
            case KeyEvent.VK_DOWN:  next.y++; break;
            case KeyEvent.VK_LEFT:  next.x--; break;
            case KeyEvent.VK_RIGHT: next.x++; break;
        }
        // 检查撞墙或撞自己
        if (next.x < 0 || next.x >= COLS || next.y < 0 || next.y >= ROWS || snake.contains(next)) {
            alive = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "游戏结束，得分：" + (snake.size() - INIT_SNAKE_LENGTH));
            return;
        }

        snake.addFirst(next);
        if (next.equals(food)) {
            spawnFood();
        } else {
            snake.removeLast();
        }
        repaint();
    }

    private void drawGame(Graphics g) {
        // 画网格
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= ROWS; i++)
            g.drawLine(0, i * CELL_SIZE, COLS * CELL_SIZE, i * CELL_SIZE);
        for (int j = 0; j <= COLS; j++)
            g.drawLine(j * CELL_SIZE, 0, j * CELL_SIZE, ROWS * CELL_SIZE);

        // 画蛇
        g.setColor(Color.GREEN.darker());
        for (Point p : snake) {
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // 画食物
        g.setColor(Color.RED);
        g.fillOval(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame().setVisible(true));
    }
}