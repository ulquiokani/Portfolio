import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import sun.applet.Main;

/**
 * Created by Kanishka on 3/2/2017.
 */
public class CustomCollider implements ContactListener {
    public CustomCollider(){
        MainEngine.world.setContactListener(this);
    }

    @Override
    public void beginContact(Contact contact) {
        //The following method checks the classes of the two objects involved in a collision to determine the corresponding course of action.
        Object ob1 = contact.getFixtureA().getUserData();
        Object ob2 = contact.getFixtureB().getUserData();
        if (ob1 instanceof Polygon && ob2 instanceof Bullet){
            for (Asteroid a:MainEngine.asteroids){
                if (a.Alpha==ob1 || a.Bravo==ob1){
                    if (a.connected){
                        a.connected=false;
                    } else {
                        if (a.Alpha==ob1){
                            a.Amarked=true;
                            MainEngine.points++;
                        } else {
                            a.Bmarked=true;
                            MainEngine.points++;
                        }
                    }
                }
            }
        }
        if (ob2 instanceof Polygon && ob1 instanceof Bullet){
            for (Asteroid a:MainEngine.asteroids){
                if (a.Alpha==ob2 || a.Bravo==ob2){
                    if (a.connected){
                        a.connected=false;
                    } else {
                        if (a.Alpha==ob2){
                            a.Amarked=true;
                            MainEngine.points++;
                        } else {
                            a.Bmarked=true;
                            MainEngine.points++;
                        }
                    }
                }
            }
        }

        if (ob1 instanceof Ship && ob2 instanceof Polygon){
            MainEngine.loss=true;
        }
        if (ob2 instanceof Ship && ob1 instanceof Polygon){
            MainEngine.loss=true;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
