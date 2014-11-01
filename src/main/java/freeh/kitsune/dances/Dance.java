

package freeh.kitsune.dances;

import freeh.kitsune.Game;
import freeh.kitsune.model.Models;
import un.api.CObject;
import un.api.character.Chars;
import un.api.collection.Collection;
import un.api.collection.Iterator;
import un.api.model3d.Model3DStore;
import un.api.model3d.Model3Ds;
import un.engine.opengl.animation.Animation;
import un.api.path.Path;

/**
 *
 */
public class Dance extends CObject{
    
    private final Path path;
    private Animation animation;
    
    public Dance(Path path) {
        this.path = path;
    }
    
    public Chars getName(){
        return new Chars(path.getName());
    }
        
    public synchronized Animation getAnimation(){
        if(animation==null){
            animation = loadAnimation(path);
        }
        return animation;
    }
    
    public synchronized void unload(){
        animation = null;
    }

    public Chars toChars() {
        return getName();
    }
    
    private static Animation loadAnimation(Path animationPath) {
        Animation animation = null;

        try {
            Model3DStore store = Model3Ds.read(animationPath);
            store.setLogger(Game.LOGGER);
            Collection elements = store.getElements();
            Iterator ite = elements.createIterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (obj instanceof Animation) {
                    animation = (Animation) obj;
                    break;
                }
            }
        } catch (Exception ex) {
            Game.LOGGER.warning(ex);
        }

        return animation;
    }
    
}
