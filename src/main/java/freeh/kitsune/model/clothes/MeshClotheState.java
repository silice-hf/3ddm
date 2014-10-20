

package freeh.kitsune.model.clothes;

import freeh.kitsune.model.Model;
import un.api.character.Chars;
import un.engine.opengl.scenegraph.GLNode;

/**
 * clothe state, exemple : base, open, torn apart, disable, ...
 */
public class MeshClotheState {
    
    private final Chars name;
    private final GLNode node;

    public MeshClotheState(Chars name, GLNode node) {
        this.name = name;
        this.node = node;
    }

    public Chars getName() {
        return name;
    }

    public GLNode getNode() {
        return node;
    }
    
    public void update(TextureSet textures){
        // todo
    }
    
    public void install(Model model){
        
    }
    
    public void uninstall(Model model){
        
    }
    
}
