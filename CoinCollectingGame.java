import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CoinCollectingGame extends JFrame implements KeyListener, ActionListener {

    private static final int WIDTH = 800;  // Width of the game window
    private static final int HEIGHT = 600; // Height of the game window
    private static final int DELAY = 16; // Delay in milliseconds (60 FPS)

    private boolean upKeyPressed;
    private boolean downKeyPressed;
    private boolean leftKeyPressed;
    private boolean rightKeyPressed;
    private boolean spaceKeyPressed;

    // PLAYER
    // Player Images
    private BufferedImage[] playerUpImages;
    private BufferedImage[] playerDownImages;
    private BufferedImage[] playerLeftImages;
    private BufferedImage[] playerRightImages;
    private BufferedImage currImg;
    // Player settings
    public int playerX;
    public int playerY;
    public int speed = 5;
    public int playerWorldX;
    public int playerWorldY;
    String direction;
    //Player Collision
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    // MAP
    public Map gameMap;
    public int tileSize;
    // private int tileHeight;
    private double scaleFactor = 1.1;
    private double tileScaleFactor = 1.0;

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    //Timer and Animation
    private int animationFrame;
    private int animationDelay = 10; // Delay between animation frames
    private int timeLeft; // Remaining time in seconds
    private static final int GAME_DURATION = 10000; // Game duration in seconds

    //COINS
    public ArrayList<Coin> coins;
    private int coinsHeld;


    public CoinCollectingGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setTitle("Coin Collector Game");
        setResizable(false);
        setLocationRelativeTo(null);

        // Add key listener to the game window
        addKeyListener(this);

        //configure player
        playerX = WIDTH / 2 - 10;
        playerY = HEIGHT / 2 - 10;
        //player collisions
        solidArea = new Rectangle();
       //upper left corner of the blocked "solid" area on the character
        solidArea.x = tileSize/6;
        solidArea.y = tileSize/3;
        //size of the blocked "solid" area on the character
        solidArea.width = (tileSize*6)/12;
        solidArea.height = (tileSize*7)/12;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Coins
        coins = new ArrayList<>();

        // Load images
        try {
            playerUpImages = new BufferedImage[2];
            playerUpImages[0] = ImageIO.read(getClass().getResource("/res/player-up1.png"));
            playerUpImages[1] = ImageIO.read(getClass().getResource("/res/player-up2.png"));

            playerDownImages = new BufferedImage[2];
            playerDownImages[0] = ImageIO.read(getClass().getResource("/res/player-down1.png"));
            playerDownImages[1] = ImageIO.read(getClass().getResource("/res/player-down2.png"));

            playerLeftImages = new BufferedImage[2];
            playerLeftImages[0] = ImageIO.read(getClass().getResource("/res/player-left1.png"));
            playerLeftImages[1] = ImageIO.read(getClass().getResource("/res/player-left2.png"));

            playerRightImages = new BufferedImage[2];
            playerRightImages[0] = ImageIO.read(getClass().getResource("/res/player-right1.png"));
            playerRightImages[1] = ImageIO.read(getClass().getResource("/res/player-right2.png"));

            currImg = playerDownImages[0];

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up map
        tileSize = 48;

        playerWorldX = tileSize * 21;
        playerWorldY = tileSize * 23;

        // tileWidth = 48;
        gameMap = new Map(this);

        // Set the initial window size based on the scale factor
        int scaledWidth = (int) (WIDTH * scaleFactor);
        int scaledHeight = (int) (HEIGHT * scaleFactor);
        setSize(scaledWidth, scaledHeight);

        // Load the map from a file
        gameMap.loadMap1();

        // Set up the game panel
        GamePanel gamePanel = new GamePanel();
        getContentPane().add(gamePanel);

        // Add key listener to the game panel
        gamePanel.addKeyListener(this);
        gamePanel.setFocusable(true);

        // Set up the game timer
        Timer timer = new Timer(DELAY, this);
        timer.start();

        //Time Left
        timeLeft = GAME_DURATION;

        setVisible(true);

        //add coins
        addCoin(10, 9); // Add a coin at tile position (2, 3)
        addCoin(13, 25); // Add a coin at tile position (4, 1)
        addCoin(17, 29);
        addCoin(11, 41);
        addCoin(10, 32);
        addCoin(16, 9); 
        addCoin(25, 25);
        addCoin(14, 14);

    }

    public void addCoin(int tileX, int tileY) {
        Coin coin = new Coin(tileX, tileY);
        coins.add(coin);
    }    

    private boolean checkCollision(int x, int y) {
        // Calculate the bounds of the player's solid area at the potential new position
        int solidAreaX = x + solidAreaDefaultX;
        int solidAreaY = y + solidAreaDefaultY;
        int solidAreaWidth = solidArea.width;
        int solidAreaHeight = solidArea.height;
    
        // Convert the player's solid area bounds to map tile coordinates
        int startX = solidAreaX / tileSize;
        int startY = solidAreaY / tileSize;
        int endX = (solidAreaX + solidAreaWidth) / tileSize;
        int endY = (solidAreaY + solidAreaHeight) / tileSize;
    
        // Iterate over the map tiles within the player's solid area
        for (int row = startY; row <= endY; row++) {
            for (int col = startX; col <= endX; col++) {
                if (gameMap.isTileBlocked(row, col)) {
                    // Collision detected with a blocked tile
                    return true;
                }
            }
        }
    
        // No collision detected
        return false;
    }
    
    private void updateGame() {
        // UPDATE GAME LOGIC HERE
    
        // Update the timer
        timeLeft-=16;

        // Check if the timer has run out
        if (timeLeft <= 0) {
            // Timer has run out, close the game
            JOptionPane.showMessageDialog(this, "Time's up!");
            //timeLeft = GAME_DURATION;
            System.exit(0);

            //gameMap.loadMap2();
            
        }

        // Calculate the potential new position of the player
        int newPlayerWorldX = playerWorldX;
        int newPlayerWorldY = playerWorldY;
        if (upKeyPressed) {
            newPlayerWorldY -= speed;
        }
        if (downKeyPressed) {
            newPlayerWorldY += speed;
        }
        if (leftKeyPressed) {
            newPlayerWorldX -= speed;
        }
        if (rightKeyPressed) {
            newPlayerWorldX += speed;
        }
    
        // Check for collision with the game map
        if (checkCollision(newPlayerWorldX, newPlayerWorldY)) {
            // Collision detected, do not update player position
            return;
        }
    
        // Check for collision with coins
        for (Coin coin : coins) {
            if (!coin.isCollected() && coin.getTileX() == playerWorldX / tileSize && coin.getTileY() == playerWorldY / tileSize) {
                // Coin collision detected
                coin.setCollected(true);
                // Increment the coinsHeld counter
                coinsHeld++;
            }
        }
    
        // Update player position if no collision
        playerWorldX = newPlayerWorldX;
        playerWorldY = newPlayerWorldY;
    
        if (spaceKeyPressed) {
            // Handle shooting
        }
    
        // Update animation frame
        animationFrame++;
        if (animationFrame >= animationDelay) {
            animationFrame = 0;
        }
    }
    
    

    private void selectImage() {
        if (upKeyPressed) {
            currImg = playerUpImages[animationFrame / (animationDelay / playerUpImages.length)];
        } else if (downKeyPressed) {
            currImg = playerDownImages[animationFrame / (animationDelay / playerDownImages.length)];
        } else if (leftKeyPressed) {
            currImg = playerLeftImages[animationFrame / (animationDelay / playerLeftImages.length)];
        } else if (rightKeyPressed) {
            currImg = playerRightImages[animationFrame / (animationDelay / playerRightImages.length)];
        }
    }

    private void renderGame(Graphics2D g) {
        // Render game objects here
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.scale(scaleFactor, scaleFactor);

        // Draw tiles with scaling
        g2d.scale(tileScaleFactor, tileScaleFactor);
        gameMap.draw(g2d);

        // Draw player
        selectImage();
        int scaledPlayerX = (int) (playerX / scaleFactor);
        int scaledPlayerY = (int) (playerY / scaleFactor);
        int scaledPlayerWidth = (int) (currImg.getWidth() * scaleFactor);
        int scaledPlayerHeight = (int) (currImg.getHeight() * scaleFactor);
        g2d.drawImage(currImg, scaledPlayerX, scaledPlayerY, scaledPlayerWidth, scaledPlayerHeight, null);

        g2d.dispose();
    }

    private class GamePanel extends JPanel {
        private String gameText;

        public Dimension getPreferredSize() {
            int scaledWidth = (int) (WIDTH * scaleFactor * tileScaleFactor);
            int scaledHeight = (int) (HEIGHT * scaleFactor * tileScaleFactor);
            return new Dimension(scaledWidth, scaledHeight);
        }

        @Override
        protected void paintComponent(Graphics g) {

            gameText= "Number of keys collected: " + coinsHeld;

            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(scaleFactor * tileScaleFactor, scaleFactor * tileScaleFactor);
            renderGame(g2d);

            //TEXT
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Trattatello Regular", Font.BOLD, 20));
            int textX = 50;
            int textY = 50;
            g2d.drawString(gameText, textX, textY);
            g2d.drawString("Time Left: " + timeLeft/1000, 100, 100);

            g2d.dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                direction = "up";
                upKeyPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                direction = "down";
                downKeyPressed = true;
                break;
            case KeyEvent.VK_LEFT:
                direction = "left";
                leftKeyPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                direction = "right";
                rightKeyPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spaceKeyPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                upKeyPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downKeyPressed = false;
                break;
            case KeyEvent.VK_LEFT:
                leftKeyPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightKeyPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spaceKeyPressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CoinCollectingGame::new);
    }
}