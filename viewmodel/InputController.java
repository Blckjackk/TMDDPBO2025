package viewmodel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class InputController implements KeyListener, MouseListener {
    private GameEngine gameEngine;
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    
    public InputController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        resetKeys();
    }
    
    private void resetKeys() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }
      // Proses input keyboard saat ini
    public void processInput() {
        if (gameEngine.isRunning()) {
            int dx = 0, dy = 0;
            
            if (upPressed) dy -= 1;
            if (downPressed) dy += 1;
            if (leftPressed) dx -= 1;
            if (rightPressed) dx += 1;
            
            // Hanya bergerak jika ada input yang sebenarnya
            if (dx != 0 || dy != 0) {
                gameEngine.movePlayer(dx, dy);
            }
        }
    }
      @Override
    public void keyTyped(KeyEvent e) {
        // Tidak digunakan
    }@Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:                // Akhiri permainan dan kembali ke menu (ini ditangani di GamePanel)
                if (gameEngine.isRunning()) {
                    gameEngine.endGame();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                // Cara alternatif untuk mengakhiri permainan
                if (gameEngine.isRunning()) {
                    gameEngine.endGame();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
        }
    }    @Override
    public void mouseClicked(MouseEvent e) {
        // Tidak digunakan
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Lempar laso saat diklik
        if (gameEngine.isRunning()) {
            gameEngine.throwLasso(new Point(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Tidak digunakan
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Tidak digunakan
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Tidak digunakan
    }
}
