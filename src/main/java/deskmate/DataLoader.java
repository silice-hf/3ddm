package deskmate;

import un.api.collection.Collection;
import un.api.collection.Iterator;
import un.api.collection.Sequence;
import un.api.tree.DefaultNodeVisitor;
import un.api.tree.Node;
import un.api.tree.NodeVisitor;
import un.engine.opengl.animation.Animation;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.MultipartMesh;
import un.engine.opengl.renderer.Renderer;
import un.engine.opengl.renderer.SilhouetteRenderer;
import un.science.encoding.color.Color;
import un.storage.model3d.Model3DStore;
import un.storage.model3d.Model3Ds;
import un.system.path.Path;

/**
 * Load models and animation functions
 */
public class DataLoader {

    public static final NodeVisitor SET_BORDER_COLOR = new DefaultNodeVisitor() {

        public Object visit(Node node, Object context) {
            if (node instanceof Mesh) {
                Color color = (Color) context;
                Mesh mesh = (Mesh) node;
                mesh.getMaterial().setCellShading(5);
                
                //search for existing silhouette renderer
                boolean found = false;
                final Sequence renderers = mesh.getRenderers();
                for(int i=0;i<renderers.getSize();i++){
                    final Renderer renderer = (Renderer) renderers.get(i);
                    if(renderer instanceof SilhouetteRenderer){
                        ((SilhouetteRenderer)renderer).setColor(color);
                        found = true;
                    }
                }
                
                //create it
                if(!found){
                    mesh.getRenderers().add(new SilhouetteRenderer((Mesh) node, color, Config.BORDER_WIDTH));
                }
            }
            return super.visit(node, context);
        }
    };
    
    public static final NodeVisitor SET_BORDER_WIDTH = new DefaultNodeVisitor() {

        public Object visit(Node node, Object context) {
            if (node instanceof Mesh) {
                float width = ((Number) context).floatValue();
                Mesh mesh = (Mesh) node;
                mesh.getMaterial().setCellShading(5);
                
                //search for existing silhouette renderer
                boolean found = false;
                final Sequence renderers = mesh.getRenderers();
                for(int i=0;i<renderers.getSize();i++){
                    final Renderer renderer = (Renderer) renderers.get(i);
                    if(renderer instanceof SilhouetteRenderer){
                        ((SilhouetteRenderer)renderer).setBorderWidth(width);
                        found = true;
                    }
                }
                
                //create it
                if(!found){
                    mesh.getRenderers().add(new SilhouetteRenderer((Mesh) node, Config.BORDER_COLOR, Config.BORDER_WIDTH));
                }
            }
            return super.visit(node, context);
        }
    };
    
    public static final NodeVisitor SET_TOON_NBSHADE = new DefaultNodeVisitor() {

        public Object visit(Node node, Object context) {
            if (node instanceof Mesh) {
                Integer shades = (Integer) context;
                Mesh mesh = (Mesh) node;
                mesh.getMaterial().setCellShading(shades);
            }
            return super.visit(node, context);
        }
    };

    public static MultipartMesh loadModel(Path modelPath) {
        MultipartMesh mesh = null;

        try {
            Model3DStore store = Model3Ds.read(modelPath);
            Collection elements = store.getElements();
            Iterator ite = elements.createIterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (obj instanceof MultipartMesh) {
                    mesh = (MultipartMesh) obj;

                    //change xna models skeleton to adapth vmd animations
                    //does not work correctly yet :'(
//                    final String name = modelPath.toURI().toLowerCase();
//                    if (name.endsWith(".mesh.ascii") || name.endsWith(".xps") || name.endsWith(".mesh")) {
//                        XNASkeletonToVMD.adapt(mesh.getSkeleton());
//                    }
                    
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mesh.accept(SET_BORDER_COLOR, Config.BORDER_COLOR);
        mesh.accept(SET_BORDER_WIDTH, Config.BORDER_WIDTH);
        mesh.accept(SET_TOON_NBSHADE, Config.TOON_NBSHADE);

        return mesh;
    }

    public static Animation loadAnimation(Path animationPath) {
        Animation animation = null;

        try {
            Model3DStore store = Model3Ds.read(animationPath);
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
            ex.printStackTrace();
        }

        return animation;
    }

}
