

package freeh.kitsune.model.clothes;

import freeh.kitsune.Game;
import freeh.kitsune.GameProperties;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.Models;
import un.api.character.Chars;
import un.engine.opengl.mesh.Mesh;

/**
 *
 */
public class PresetClothe extends Clothe {

    private static final Chars STATE_VISIBLE = new Chars("visible");
    private static final Chars STATE_UNVISIBLE = new Chars("unvisible");
    
    private final Mesh clotheMesh;

    public PresetClothe(Mesh mesh) {
        this.clotheMesh = mesh;
        getStates().add(new PresetClotheState(true));
        getStates().add(new PresetClotheState(false));
        setState((ClotheState) getStates().get(0));
    }

    public Mesh getMesh() {
        return clotheMesh;
    }

    @Override
    public Chars getName() {
        return clotheMesh.getName();
    }
    
    public class PresetClotheState extends ClotheState {

        public PresetClotheState(boolean visible) {
            super(visible ? STATE_VISIBLE : STATE_UNVISIBLE,
                  visible ? GameProperties.ICON_CLOTHE_ON : GameProperties.ICON_CLOTHE_OFF);
        }
        
        @Override
        public void install(Model model) {
            clotheMesh.setVisible(getName()==STATE_VISIBLE);
            model.getNode().addChild(clotheMesh);
        }

        @Override
        public void uninstall(Model model) {
            clotheMesh.setVisible(false);
            model.getNode().removeChild(clotheMesh);
        }
    }
    
}
