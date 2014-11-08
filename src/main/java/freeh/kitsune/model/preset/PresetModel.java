

package freeh.kitsune.model.preset;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import freeh.kitsune.maps.Map;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.clothes.Clothe;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Iterator;
import un.api.collection.Sequence;
import un.api.model3d.Model3DStore;
import un.api.model3d.Model3Ds;
import un.api.physic.skeleton.Skeleton;
import un.api.tree.DefaultNodeVisitor;
import un.api.tree.Node;
import un.api.tree.NodeVisitor;
import un.engine.opengl.GLExecutable;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.MultipartMesh;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.scenegraph.SceneNode;
import un.api.io.IOException;
import un.storage.binding.xml.QName;
import un.storage.binding.xml.XMLOutputStream;
import un.storage.binding.xml.dom.DomElement;
import un.storage.binding.xml.dom.DomNode;
import un.storage.binding.xml.dom.DomReader;
import un.storage.binding.xml.dom.DomWriter;
import un.api.path.Path;

/**
 * Existing model, pmx, pmd, xna, mqo ...
 */
public class PresetModel extends Model {

    private static final Chars TRUE = new Chars("true");
    private static final Chars FALSE = new Chars("false");
    
    private static final NodeVisitor FIND_CLOTHES = new DefaultNodeVisitor(){
        public Object visit(Node node, Object context) {
            
            final Object[] array = (Object[])context;
            final DomElement dom = (DomElement)array[0];
            
            if(node instanceof Mesh){
                final Mesh mesh = ((Mesh)node);
                final Chars isClothe = (Chars) dom.getProperties().getValue(new Chars("clothe"));
                if(TRUE.equals(isClothe)){
                    //this mesh is a clothe
                    final Sequence clothes = (Sequence) array[1];
                    final Clothe clothe = new PresetModelClothe(mesh);
                    clothes.add(clothe);
                }
            }
            
            final Node[] children = node.getChildren();
            for(int i=0;i<children.length;i++){
                if(children[i]!=null){
                    children[i].accept(this, new Object[]{dom.getChild(i),array[1]});
                }
            }
            
            return null;
        }
    };
    
    private final Sequence allClothes = new ArraySequence();
    private final Path path;
    private MultipartMesh mesh = null;
    private DomNode hitTree = null;
    
    public PresetModel(Path modelPath){
        super(GameInfo.replaceSuffix(modelPath,"meta"));
        this.path = modelPath;
    }

    @Override
    public synchronized GLNode getNode() {
        check();
        return mesh;
    }

    @Override
    public synchronized Skeleton getSkeleton() {
        check();
        return mesh.getSkeleton();
    }
    
    public Path getPath() {
        return path;
    }

    private void check() {
        if(mesh!=null) return;
        
        Game.LOGGER.info(new Chars("Loading : "+this));
        try {
            final Model3DStore store = Model3Ds.read(path);
            store.setLogger(Game.LOGGER);
            final Iterator ite = store.getElements().createIterator();
            while (ite.hasNext()) {
                final Object obj = ite.next();
                if (obj instanceof MultipartMesh) {
                    mesh = (MultipartMesh) obj;

//                    //change xna models skeleton to adapth vmd animations
//                    //does not work correctly yet :'(
//                    final String name = modelPath.toURI().toLowerCase();
//                    if (name.endsWith(".mesh.ascii") || name.endsWith(".xps") || name.endsWith(".mesh")) {
//                        XNASkeletonToVMD.adapt(mesh.getSkeleton());
//                    }
                    
                    break;
                }
            }
        } catch (Exception ex) {
            Game.LOGGER.warning(ex);
        }
        
        //load hit tree
        if(mesh!=null){
            try {
                hitTree = getOrCreateClotheMap();
                updateClothes();
            } catch (IOException ex) {
                Game.LOGGER.warning(ex);
            }
        }

        clothes.addAll(allClothes);
        
    }

    public DomNode getHitTree() {
        return hitTree;
    }

    private DomNode getOrCreateClotheMap() throws IOException{
        //load hit tree
        final Path hitPath = GameInfo.replaceSuffix(path,"hitmap");
        try{
            hitPath.createInputStream().close();
        }catch(Throwable ex){
            //create it if it does not exist
            saveClotheMap();
        }
        
        final DomReader reader = new DomReader();
        reader.setInput(hitPath);
        return reader.read();
    }
        
    public void saveClotheMap() {
        try{
            final Path hitPath = GameInfo.replaceSuffix(path,"hitmap");
            
            hitTree = (DomNode) mesh.accept(new NodeVisitor(){
                public Object visit(Node node, Object context) {                
                    final DomElement dom = new DomElement(new QName(null, new Chars("mesh")));
                    if(node instanceof Mesh){
                        final Mesh mesh = (Mesh) node;
                        dom.getProperties().add(new Chars("name"), mesh.getName());
                        dom.getProperties().add(new Chars("clothe"), PresetModelClothe.isClothe(mesh) ? TRUE : FALSE);
                    }
                    
                    final Node[] children = node.getChildren();
                    for(int i=0;i<children.length;i++){
                        if(children[i]!=null){
                            DomNode n = (DomNode) children[i].accept(this, context);
                            dom.addChild(n);
                        }
                    }                
                    return dom;
                }
            }, null);
            
            final XMLOutputStream out = new XMLOutputStream();
            out.setOutput(hitPath);
            DomWriter writer = new DomWriter();
            writer.setOutput(out);
            out.setIndent(new Chars("  "));
            writer.write(hitTree);
        }catch(IOException ex){
            Game.LOGGER.warning(ex);
        }
    }
    
    public void updateClothes(){
        
        //mesh which are hittable are considered to be clothes
        clothes.removeAll();
        allClothes.removeAll();
        FIND_CLOTHES.visit(mesh, new Object[]{hitTree,allClothes});
        clothes.addAll(allClothes);
                
    }

    /**
     * Detach node from parent and unload resources.
     * @param context 
     */
    public synchronized void unload(GLProcessContext context){
        if(mesh==null) return;
        final GLNode node = mesh;
        mesh = null;
        context.addTask(new GLExecutable() {
            public Object execute() {
                Game.LOGGER.info(new Chars("Unloading : "+PresetModel.this));
                final SceneNode parent = node.getParent();
                if(parent!=null) parent.removeChild(node);
                node.dispose(context);
                return null;
            }
        });
    }
    
    @Override
    public Chars toChars() {
        return new Chars("ModelPreset "+path);
    }
    
}
