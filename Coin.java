import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Coin {
    private int tileX; // X-coordinate of the coin
    private int tileY; // Y-coordinate of the coin
    private boolean collected; // Whether the coin has been collected
    private BufferedImage coinImage; // Image of the coin

    public Coin(int x, int y) {
        this.tileX = x;
        this.tileY = y;
        this.collected = false;

    // Load the coin image from the resources folder
    try{
        coinImage = ImageIO.read(getClass().getResource("/res/coin.png"));
    }catch(IOException e){
        e.printStackTrace();
    }
        
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public BufferedImage getCoinImage() {
        return coinImage;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(coinImage, tileX, tileY, null);
    }
}
