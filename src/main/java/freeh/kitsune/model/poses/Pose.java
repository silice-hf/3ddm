

package freeh.kitsune.model.poses;

import freeh.kitsune.model.Models;
import un.api.CObject;
import un.api.character.Chars;
import un.engine.opengl.physic.RelativeSkeletonPose;
import un.science.encoding.IOException;
import un.system.path.Path;

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
        return Models.loadPose(path);
    }
    
    public Chars toChars() {
        return getName();
    }
}
