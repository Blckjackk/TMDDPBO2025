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
    
    // Process current keyboard input
    public void processInput() {
        if (gameEngine.isRunning()) {
            int dx = 0, dy = 0;
            
            if (upPressed) dy -= 1;
            if (downPressed) dy += 1;
            if (leftPressed) dx -= 1;
            if (rightPressed) dx += 1;
            
            // Only move if there's actual input
            if (dx != 0 || dy != 0) {
                gameEngine.movePlayer(dx, dy);
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }    @Override
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
            case KeyEvent.VK_SPACE:
                // End game and return to menu (this is handled in GamePanel)
                if (gameEngine.isRunning()) {
                    gameEngine.endGame();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                // Alternative way to end game
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
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Throw lasso on click
        if (gameEngine.isRunning()) {
            gameEngine.throwLasso(new Point(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }
}
