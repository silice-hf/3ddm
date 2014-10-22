

package freeh.kitsune.dances;

import freeh.kitsune.model.Models;
import un.api.CObject;
import un.api.character.Chars;
import un.engine.opengl.animation.Animation;
import un.system.path.Path;

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
            animation = Models.loadAnimation(path);
        }
        return animation;
    }
    
    public synchronized void unload(){
        animation = null;
    }

    public Chars toChars() {
        return getName();
    }
    
}
