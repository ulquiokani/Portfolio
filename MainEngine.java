import java.awt.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kanishka on 4/23/2017.
 */
//Based on Box2D and the BasicPhysicsEngineUsingBox2D used during the labs
public class MainEngine {
    public static final int SH=1000; //Screen Height
    public static final int SW=1000; //Screen Width
    public static final Dimension FS = new Dimension(SW,SH);
    public static final float WW=100; //World Width
    public static final float WH=SH*(WW/SW); //World Height
    public static final float G=9.8f; //Gravity
    public static float timer=8.0f;
    public float currenttime=0.0f;

    public static int points=0;
    public static boolean loss=false;

    public static World world;

    public static final int Delay=20;
    public static final int EulerUpdates=10; //Number of euler updates per screen refresh
    public static final float DeltaT = Delay/1000.0f;

    public static int convertWorldXtoScreenX(float worldX){
        return (int) (worldX/WW*SW);
    }
    public static int convertWorldYtoScreenY(float worldY){
        return (int) (SH-(worldY/WH*SH));
    }
    public static float convertWorldLengthToScreenLength(float worldLength){
        return (worldLength/WW*SW);
    }

    public List<AnchoredBarrier> barriers;
    public static Ship ship;
    public List<Bullet> bullets;
    public static List<Asteroid> asteroids;

    public MainEngine(){
        world = new World(new Vec2(0,-G));
        world.setContinuousPhysics(true);

        CustomCollider col = new CustomCollider();

        barriers = new ArrayList<AnchoredBarrier>();
        bullets = new ArrayList<Bullet>();
        asteroids = new ArrayList<Asteroid>();

        barriers.add(new AnchoredBarrier_StraightLine(0, 0, WW, 0, Color.WHITE));
        barriers.add(new AnchoredBarrier_StraightLine(WW, 0, WW, WH, Color.WHITE));
        barriers.add(new AnchoredBarrier_StraightLine(WW, WH, 0, WH, Color.WHITE));
        barriers.add(new AnchoredBarrier_StraightLine(0, WH, 0, 0, Color.WHITE));

        ship = new Ship(1,10,0);
        asteroids.add(new Asteroid());
        asteroids.add(new Asteroid());
        asteroids.add(new Asteroid());
    }

    public static void main(String[] args)throws Exception{
        final MainEngine game = new MainEngine();
        final View view = new View(game);
        JEasyFrame frame = new JEasyFrame(view, "Scattershot");
        frame.addKeyListener(new KeyListener());
        game.startThread(view);
    }

    private void startThread(final View view) throws InterruptedException {
        final MainEngine game=this;
        while (true) {
            game.update();
            view.repaint();

            try {
                Thread.sleep(Delay);
            } catch (InterruptedException e) {
            }
        }
    }

    public void update() {
        if (currenttime>timer){
            asteroids.add(new Asteroid());
            asteroids.add(new Asteroid());
            asteroids.add(new Asteroid());
            currenttime=0;
        }
        if (KeyListener.fire){
            bullets.add(new Bullet(0.3f,0,0));
            KeyListener.fire=false;
        }

        List<Bullet> tempb = new ArrayList<Bullet>();
        for (Bullet b:bullets){
            if (b.life>0){
                tempb.add(b);
            } else {
                world.destroyBody(b.body);
            }
        }
        bullets=tempb;

        for (Bullet b:bullets){
            b.update();
        }

        List<Asteroid> tempasteroids=new ArrayList<Asteroid>();
        for (Asteroid a:asteroids){
            if (a.Amarked){
                world.destroyBody(a.Alpha.body);
            }
            if (a.Bmarked){
                world.destroyBody(a.Bravo.body);
            }
            if (!a.Amarked || !a.Bmarked){
                tempasteroids.add(a);
            }
        }
        asteroids=tempasteroids;

        for (Asteroid a:asteroids){
            a.updater();
        }
        int VI = EulerUpdates; //Velocity Iterations
        int PI = EulerUpdates; //Position Iterations

        ship.updater();
        world.step(DeltaT,VI,PI);
        currenttime=currenttime+DeltaT;
        System.out.println(currenttime);
    }
}
