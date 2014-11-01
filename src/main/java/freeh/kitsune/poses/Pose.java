

package freeh.kitsune.poses;

import freeh.kitsune.Game;
import un.api.CObject;
import un.api.character.Chars;
import un.api.collection.Collection;
import un.api.collection.Iterator;
import un.engine.opengl.physic.RelativeSkeletonPose;
import un.api.io.IOException;
import un.api.model3d.Model3DStore;
import un.api.model3d.Model3Ds;
import un.api.path.Path;

/**
 *
 */
public class Pose extends CObject{
        
    private final Path path;
    
    public Pose(Path path) throws IOException {
        this.path = path;
    }
    
    public Chars getName(){
        return new Chars(path.getName());
    }
        
    public RelativeSkeletonPose createPose(){
        return loadPose(path);
    }
    
    public Chars toChars() {
        return getName();
    }
    
    public static RelativeSkeletonPose loadPose(Path posePath) {
        RelativeSkeletonPose pose = null;

        try {
            Model3DStore store = Model3Ds.read(posePath);
            store.setLogger(Game.LOGGER);
            Collection elements = store.getElements();
            Iterator ite = elements.createIterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (obj instanceof RelativeSkeletonPose) {
                    pose = (RelativeSkeletonPose) obj;
                    break;
                }
            }
        } catch (Exception ex) {
            Game.LOGGER.warning(ex);
        }

        return pose;
    }
    
}
