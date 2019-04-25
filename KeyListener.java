import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Kanishka on 4/23/2017.
 */
public class KeyListener extends KeyAdapter{
    private static boolean left, right, up, down;
    public static boolean fire;

    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_UP:
                up=true;
                break;
            case KeyEvent.VK_DOWN:
                down=true;
                break;
            case KeyEvent.VK_LEFT:
                left=true;
                break;
            case KeyEvent.VK_RIGHT:
                right=true;
                break;
            case KeyEvent.VK_SPACE:
                if (!MainEngine.loss){
                    fire=true;
                }
                break;
        }
    }

    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_UP:
                up=false;
                break;
            case KeyEvent.VK_DOWN:
                down=false;
                break;
            case KeyEvent.VK_LEFT:
                left=false;
                break;
            case KeyEvent.VK_RIGHT:
                right=false;
                break;
        }
    }

    public static boolean isUp(){return up;}

    public static boolean isDown(){return down;}

    public static boolean isLeft(){return left;}

    public static boolean isRight(){return right;}
}
