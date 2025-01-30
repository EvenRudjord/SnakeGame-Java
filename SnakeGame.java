import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    private class Tile {
        int x;
        int y;

        Tile (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25; 
    
    //Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody; 

    //Food
    Tile food;
    Random random;

    //GameLogic
    Timer gameLoop;
    int speed;
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        speed = 1;
        velocityX = 0;
        velocityY = 0; 

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Food
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        //SnakeHead
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        //SnakeBody
        for(int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }


        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("GAME OVER", tileSize - 16, tileSize);
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize + 30);

            //calculating new Highscore
            if(snakeBody.size() > loadHighScore()) {
                saveHighScore(snakeBody.size());
            }

            g.drawString("HighScore: " + String.valueOf(loadHighScore()), tileSize + 400, tileSize);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }

    }


    public void placeFood() {
        int randomX = 5;
        int randomY = 5;

        //Makes the food not spawn on top of snakeHead
        while((randomX == 5 && randomY == 5)) {
            randomX = random.nextInt(boardWidth/tileSize); // 600/25 = 24 (0-24)
            randomY = random.nextInt(boardHeight/tileSize);
        }

        food.x = randomX;
        food.y = randomY;
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        //eat food
        if(collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        //SnakeBody
        for(int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if(i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        //SnakeHead
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //GameOver condition
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            //collide with snake head
            if(collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        //went outside screen
    if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight) 
        gameOver = true;
    }

    public static void saveHighScore(int score) {
        try (FileWriter writer = new FileWriter("highscore.txt")) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            System.out.println("Error saving highscore: " + e.getMessage());
        }
    }

    public static int loadHighScore() {
        try {
            File file = new File("highscore.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                int highScore = scanner.nextInt();
                scanner.close();
                return highScore;
            }
        } catch (IOException e) {
            System.out.println("Error loading highscore: " + e.getMessage());
        }
        return 0;
    }

    //Timer calls this
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver)
            gameLoop.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && velocityY != speed) {
            velocityX = 0;
            velocityY = -speed;
        } else if((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && velocityY != -speed) {
            velocityX = 0;
            velocityY = speed;
        } else if((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && velocityX != speed) {
            velocityX = -speed;
            velocityY = 0;
        } else if((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && velocityX != -speed) {
            velocityX = speed;
            velocityY = 0;
        }
    }

    //Dont need
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}
