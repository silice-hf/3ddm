

package freeh.kitsune.maps;

import freeh.kitsune.model.Models;
import freeh.kitsune.model.preset.PresetModel;
import javax.media.opengl.GLContext;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Collection;
import un.api.collection.Iterator;
import un.api.collection.Sequence;
import un.api.model3d.Model3DStore;
import un.api.model3d.Model3Ds;
import un.engine.opengl.GLExecutable;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.scenegraph.SceneNode;
import un.science.encoding.IOException;
import un.system.path.Path;

/**
 *
 */
public class Map {
    
    private final Path path;
    private GLNode root;

    public Map(Path path) {
        this.path = path;
    }
    
    public Chars getName(){
        return new Chars(path.getName());
    }
    
    public synchronized GLNode getNode(){
        if(root!=null) return root;
        
        System.out.println("Loading : "+this);
        final Sequence parts = new ArraySequence();
        try {
            Models.search(path, parts, null, null, null, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        //maps are often composed of multiple parts
        root = new GLNode();
        for(int i=0;i<parts.getSize();i++){
            final PresetModel pm = (PresetModel) parts.get(i);
            final Path partPath = pm.getPath();
            try{
                final Model3DStore store = Model3Ds.read(partPath);
                final Collection elements = store.getElements();
                final Iterator ite = elements.createIterator();
                while (ite.hasNext()) {
                    final Object obj = ite.next();
                    if (obj instanceof GLNode) {
                        root.addChild((GLNode)obj);
                    }
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        
        return root;
    }

    /**
     * Detach node from parent and unload resources.
     * @param context 
     */
    public synchronized void unload(GLProcessContext context){
        if(root==null) return;
        final GLNode node = root;
        root = null;
        context.addTask(new GLExecutable() {
            public Object execute() {
                System.out.println("Unloading : "+Map.this);
                final SceneNode parent = node.getParent();
                if(parent!=null) parent.removeChild(node);
                node.dispose(context);
                return null;
            }
        });
    }
    
    @Override
    public String toString() {
        return "Map:"+path;
    }
    
}
