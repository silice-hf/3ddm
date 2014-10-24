

package freeh.kitsune.model.preset;

import freeh.kitsune.GameProperties;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.clothes.Clothe;
import freeh.kitsune.model.clothes.ClotheState;
import un.api.character.Chars;
import un.api.image.Image;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.scenegraph.GLNode;

/**
 *
 */
public class PresetModelClothe extends Clothe {

    private static final Chars PROP_CLOTHE = new Chars("isClothe");
    
    private static final Chars STATE_VISIBLE = new Chars("visible");
    private static final Chars STATE_UNVISIBLE = new Chars("unvisible");
    
    private final Mesh clotheMesh;
    private final PresetClotheState on = new PresetClotheState(STATE_VISIBLE,GameProperties.ICON_CLOTHE_ON,true);
    private final PresetClotheState off = new PresetClotheState(STATE_UNVISIBLE,GameProperties.ICON_CLOTHE_OFF,false);

    public PresetModelClothe(Mesh mesh) {
        this.clotheMesh = mesh;
        getStates().add(on);
        getStates().add(off);
        state = on;
    }

    public Mesh getMesh() {
        return clotheMesh;
    }

    @Override
    public Chars getName() {
        return clotheMesh.getName();
    }
    
    public class PresetClotheState extends ClotheState {

        private final boolean visible;
        
        public PresetClotheState(Chars name, Image icon, boolean visible) {
            super(name, icon);
            this.visible = visible;
        }
        
        @Override
        public void install(Model model) {
            clotheMesh.setVisible(visible);
        }

        @Override
        public void uninstall(Model model) {
            clotheMesh.setVisible(false);
        }
    }
    
    
    public static boolean isClothe(GLNode node){
        return Boolean.TRUE.equals(node.getProperties().getValue(PROP_CLOTHE));
    }
    
    public static void setClothe(GLNode node, boolean isClothe){
        node.getProperties().add(PROP_CLOTHE,isClothe);
    }
    
}
