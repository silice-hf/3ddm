

package freeh.kitsune.model;

import freeh.kitsune.model.preset.PresetModel;
import freeh.kitsune.audios.Music;
import freeh.kitsune.dances.Dance;
import freeh.kitsune.poses.Pose;
import freeh.kitsune.stages.deskmate.XNASkeletonToVMD;
import un.api.array.Arrays;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Collection;
import un.api.collection.Iterator;
import un.api.collection.Sequence;
import un.api.image.Image;
import un.api.model3d.Model3DStore;
import un.api.model3d.Model3Ds;
import un.api.tree.DefaultNodeVisitor;
import un.api.tree.Node;
import un.api.tree.NodeVisitor;
import un.engine.opengl.animation.Animation;
import un.engine.opengl.material.Layer;
import un.engine.opengl.material.Material;
import un.engine.opengl.material.mapping.UVMapping;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.MultipartMesh;
import un.engine.opengl.phase.picking.PickActor;
import un.engine.opengl.phase.picking.PickResetPhase;
import un.engine.opengl.physic.RelativeSkeletonPose;
import un.engine.opengl.physic.SkinShell;
import un.engine.opengl.renderer.Renderer;
import un.engine.opengl.renderer.SilhouetteRenderer;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.painter2d.ImagePainter2D;
import un.engine.painter2d.Painter2D;
import un.engine.painter2d.Painters;
import un.science.encoding.IOException;
import un.science.geometry.BBox;
import un.science.math.Matrix4;
import un.storage.model2d.svg.RenderState;
import un.storage.model2d.svg.SVGReader;
import un.storage.model2d.svg.model.SVGDocument;
import un.storage.model2d.svg.model.SVGElement;
import un.storage.model2d.svg.model.SVGGraphic;
import un.system.path.Path;
import un.system.path.Paths;

/**
 * Model bank and utils.
 * 
 */
public class Models {
        
    private static final String[] MODEL_EXTS = new String[]{"pmx","pmd","mesh.ascii","xps","mesh"};
    private static final String[] MOTION_EXTS = new String[]{"vmd"};
    private static final String[] POSE_EXTS = new String[]{"vpd","pose"};
    private static final String[] AUDIO_EXTS = new String[]{"wav","flac","mp4","adts"};
    
    private static final NodeVisitor SET_SILHOUETTE = new DefaultNodeVisitor() {

        public Object visit(Node node, Object context) {
            if (node instanceof Mesh) {
                final SilhouetteInfo info = (SilhouetteInfo) context;
                final Mesh mesh = (Mesh) node;
                
                //search for existing silhouette renderer
                boolean found = false;
                final Sequence renderers = mesh.getRenderers();
                for(int i=0;i<renderers.getSize();i++){
                    final Renderer renderer = (Renderer) renderers.get(i);
                    if(renderer instanceof SilhouetteRenderer){
                        found = true;
                        if(info==null){
                            //remove it
                            renderers.remove(i);
                        }else{
                            ((SilhouetteRenderer)renderer).setColor(info.color);
                            ((SilhouetteRenderer)renderer).setBorderWidth(info.width);
                        }
                        break;
                    }
                }
                
                //create it
                if(!found && info!=null){
                    mesh.getRenderers().add(new SilhouetteRenderer((Mesh) node, info.color, info.width));
                }
            }
            return super.visit(node, context);
        }
    };
        
    private static final NodeVisitor SET_TOON_NBSHADE = new DefaultNodeVisitor() {

        public Object visit(Node node, Object context) {
            if (node instanceof Mesh) {
                Integer shades = (Integer) context;
                Mesh mesh = (Mesh) node;
                mesh.getMaterial().setCellShading(shades);
            }
            return super.visit(node, context);
        }
    };
    
    private static final NodeVisitor SET_PICKABLE = new DefaultNodeVisitor(){
        public Object visit(Node node, Object context) {
            
            PickResetPhase pickPhase = (PickResetPhase)context;
            
            test:
            if(node instanceof Mesh){
                final Mesh mesh = ((Mesh)node);

                final Material material = mesh.getMaterial();
                if(!(material.getLayer(Layer.TYPE_DIFFUSE).getMapping() instanceof UVMapping)){
                    break test;
                }
                                
                final UVMapping text = (UVMapping) material.getLayer(Layer.TYPE_DIFFUSE).getMapping();
                Image image = text.getTexture().getImage();
                ImagePainter2D painter = Painters.getPainterManager().createPainter(image.getDimensionSize(0), image.getDimensionSize(1));
                painter.paint(image, new Matrix4().setToIdentity());
                Image rgba = painter.getImage();
                painter.dispose();
                text.getTexture().setImage(rgba);
                text.getTexture().setMipmap(false);
                
                text.getTexture().setForgetOnLoad(false);

                final SkinShell shell = (SkinShell) mesh.getShape();
                shell.getVertices().setForgetOnLoad(false);
                shell.getUVs().setForgetOnLoad(false);
                shell.getIndexes().setForgetOnLoad(false);
                shell.getJointIndexes().setForgetOnLoad(false);
                shell.getWeights().setForgetOnLoad(false);
//                ((SkinShell)mesh.getShell()).getReducedVBO().setForgetOnLoad(false);

                //attach a pick actor on the meshes we want to pick
                mesh.getExtShaderActors().add(new PickActor(pickPhase, mesh));
            }
            
            final Node[] children = node.getChildren();
            for(int i=0;i<children.length;i++){
                if(children[i]!=null){
                    children[i].accept(this, context);
                }
            }
            
            return null;
        }
    };
        
    public static void setSilhouette(GLNode node, SilhouetteInfo info){
        node.accept(SET_SILHOUETTE, info);
    }
    
    public static void setCellShading(GLNode node, int nb){
        node.accept(SET_TOON_NBSHADE, nb);
    }
    
    public static void setHittable(GLNode node, PickResetPhase pickPhase){
        node.accept(SET_PICKABLE, pickPhase);
    }
    
    public static Sequence searchModels(Path path) throws IOException{
        final Sequence sequence = new ArraySequence();
        if (!path.isContainer()) {
            search(path, sequence, null, null, null, false);
        }else{
            for (Node child : path.getChildren()) {
                search((Path) child, sequence, null, null, null, false);
            }
        }
        return sequence;
    }
    
    public static void search(Path candidate, Sequence models, Sequence animations, Sequence poses, Sequence audios, boolean recursive) throws IOException {
        if (!candidate.isContainer()) {
            String name = candidate.getName().toLowerCase();
            final int dotIndex = name.indexOf('.');
            if(dotIndex<0)return;
            final String ext = name.substring(dotIndex+1);
            if (models!=null && Arrays.contains(MODEL_EXTS, ext)) {
                models.add(new PresetModel(candidate));
            } else if (animations!=null && Arrays.contains(MOTION_EXTS, ext)) {
                animations.add(new Dance(candidate));
            } else if (poses!=null && Arrays.contains(POSE_EXTS, ext)) {
                poses.add(new Pose(candidate));
            } else if (audios!=null && Arrays.contains(AUDIO_EXTS, ext)) {
                audios.add(new Music(candidate));
            }
        } else if(!Boolean.TRUE.equals(candidate.getPathInfo(Path.INFO_HIDDEN)) && recursive){
            for (Node child : candidate.getChildren()) {
                search((Path) child, models, animations, poses, audios, recursive);
            }
        }
    }
    
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
                    final String name = modelPath.toURI().toLowerCase();
                    if (name.endsWith(".mesh.ascii") || name.endsWith(".xps") || name.endsWith(".mesh")) {
                        XNASkeletonToVMD.adapt(mesh.getSkeleton());
                    }
                    
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(mesh!=null){
            setSilhouette(mesh, new SilhouetteInfo());
        }
        
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
    
    public static RelativeSkeletonPose loadPose(Path posePath) {
        RelativeSkeletonPose pose = null;

        try {
            Model3DStore store = Model3Ds.read(posePath);
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
            ex.printStackTrace();
        }

        return pose;
    }

    public static Image buildImage(Chars path) throws IOException{
        final SVGReader reader = new SVGReader();
        reader.setInput(Paths.resolve(path.toJVMString()));
        final SVGDocument doc = reader.read();
        
        //find the image best size
        final BBox bbox = new BBox(2);
        bbox.setRange(0, 0, 32);
        bbox.setRange(1, 0, 32);
        
        final ImagePainter2D painter = Painters.getPainterManager().createPainter((int)bbox.getSpan(0), (int)bbox.getSpan(1));
        render(painter, doc);
        painter.flush();
        final Image image = painter.getImage();
        painter.dispose();
        return image;
    }
    
    private static void render(Painter2D painter, SVGElement ele){
        
        if(ele instanceof SVGGraphic){
            final SVGGraphic graphic = (SVGGraphic) ele;
            final RenderState state = graphic.buildRenderState();
            if(state.fillGeometry!=null){
                painter.setPaint(state.fillPaint);
                painter.fill(state.fillGeometry);
            }
            if(state.strokeGeometry!=null){
                painter.setPaint(state.strokePaint);
                painter.setBrush(state.strokeBrush);
                painter.stroke(state.strokeGeometry);
            }
        }
                
        final Node[] children = ele.getChildren();
        for(int i=0; i<children.length; i++){
            render(painter, (SVGElement)children[i]);
        }
    }
    
}
