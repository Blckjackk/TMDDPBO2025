import java.awt.*;

public class Player {
    private int posX;
    private int posY;

    private int widht;
    private int height;
    private Image image;

    public int getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    private int velocityY;

    public Player(int posX, int posY, int widht, int height, Image image){
        this.posX = posX;
        this.posY = posY;
        this.widht = widht;
        this.height = height;
        this.image = image;

        this.velocityY = -0;
    }

    // Getter Setter bang
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidht() {
        return widht;
    }

    public void setWidht(int widht) {
        this.widht = widht;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }


}
