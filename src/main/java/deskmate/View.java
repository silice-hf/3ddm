
package deskmate;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import un.api.collection.Sequence;
import un.engine.opengl.GLC;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.material.Layer;
import un.engine.opengl.material.mapping.ColorMapping;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.MultipartMesh;
import un.engine.opengl.mesh.Shell;
import un.engine.opengl.phase.ClearPhase;
import un.engine.opengl.phase.DeferredRenderPhase;
import un.engine.opengl.phase.FBOResizePhase;
import un.engine.opengl.phase.UpdatePhase;
import un.engine.opengl.phase.effect.BloomPhase;
import un.engine.opengl.phase.effect.DOFPhase;
import un.engine.opengl.phase.effect.DirectPhase;
import un.engine.opengl.phase.effect.FastGaussianBlurPhase;
import un.engine.opengl.physic.SkeletonAnimation;
import un.engine.opengl.resource.FBO;
import un.engine.opengl.resource.IBO;
import un.engine.opengl.resource.Texture2D;
import un.engine.opengl.resource.VBO;
import un.engine.opengl.scenegraph.Camera3D;
import un.engine.opengl.scenegraph.GLScene;
import un.science.encoding.color.Color;
import un.storage.imagery.process.ConvolutionMatrices;
import un.storage.imagery.process.ConvolutionMatrix;
import un.system.path.Path;

/**
 * Store what is being displayed.
 */
public class View {
    
    public final List<Path> allModels = new ArrayList<Path>();
    public final List<Path> allAnimations = new ArrayList<Path>();
    
    public GLProcessContext glContext;
    public final GLScene scene = new GLScene(new float[]{0f, 0f, 0f, 0f});
    public final Mesh ground = buildGround();
    public final Camera3D camera = new Camera3D();
    
    //rendering pipeline
    private ClearPhase clearPhase;
    private FBOResizePhase resizePhase;
    private UpdatePhase updatePhase;
    private DeferredRenderPhase renderPhase;
    private DirectPhase direct1Phase;
    private FastGaussianBlurPhase horizontalBlurPhase;
    private FastGaussianBlurPhase verticalBlurPhase;
    private DOFPhase dofPhase;
    private DirectPhase direct2Phase;
    private BloomPhase bloomAfterDofPhase;
    private BloomPhase bloomNoDofPhase;
    
    
    public Path currentModelPath;
    public MultipartMesh currentModel;
    
    public Path currentAnimationPath;
    public SkeletonAnimation currentAnimation;
    
    public void buildPipeline(){
                
        //default phases
        clearPhase = new ClearPhase();
        resizePhase = new FBOResizePhase();
        updatePhase = new UpdatePhase(scene);
                
        int width = 800;
        int height = 600;
        
        //rendering
        final FBO fbo1 = new FBO(width, height);
        fbo1.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2D(width, height, Texture2D.COLOR_RGBA));
        fbo1.addAttachment(GLC.FBO.Attachment.COLOR_1, new Texture2D(width, height, Texture2D.VEC3_FLOAT));
        fbo1.addAttachment(GLC.FBO.Attachment.DEPTH, new Texture2D(width, height, Texture2D.DEPTH_24)); 
        
        renderPhase = new DeferredRenderPhase(camera,scene,fbo1, 
                new DeferredRenderPhase.Mapping[]{
                    DeferredRenderPhase.OUT_DIFFUSE,
                    DeferredRenderPhase.OUT_POSITION_CAMERA},
                true);
        direct1Phase = new DirectPhase(fbo1.getColorTexture());
        direct1Phase.setEnable(false);
        
        // Deth of field
        final FBO fbo2 = new FBO(width, height);
        fbo2.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2D(width, height,Texture2D.COLOR_RGBA));
        final FBO fbo3 = new FBO(width, height);
        fbo3.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2D(width, height,Texture2D.COLOR_RGBA));
        
        ConvolutionMatrix gaussianRamp = ConvolutionMatrices.createGaussian(5, 6);
        horizontalBlurPhase = new FastGaussianBlurPhase(fbo2,fbo1.getColorTexture(),gaussianRamp,true);
        verticalBlurPhase = new FastGaussianBlurPhase(fbo3,fbo2.getColorTexture(),gaussianRamp,false);
        dofPhase = new DOFPhase(fbo2,
                fbo1.getTexture(GLC.FBO.Attachment.COLOR_0),
                fbo1.getTexture(GLC.FBO.Attachment.COLOR_1),
                fbo3.getColorTexture(),1f,15f,50f,20f);
        direct2Phase = new DirectPhase(fbo2.getColorTexture());
        direct2Phase.setEnable(false);
        
        // Bloom
        bloomAfterDofPhase = new BloomPhase(fbo2.getColorTexture(),0.10f);
        bloomNoDofPhase = new BloomPhase(fbo1.getColorTexture(),0.10f);
        bloomNoDofPhase.setEnable(false);
                        
        
        //add all phases
        Sequence phases = glContext.getPhases();
        phases.add(clearPhase);
        phases.add(resizePhase);
        phases.add(updatePhase);
        phases.add(renderPhase);
        phases.add(direct1Phase);
        phases.add(horizontalBlurPhase);
        phases.add(verticalBlurPhase);
        phases.add(dofPhase);
        phases.add(direct2Phase);
        phases.add(bloomAfterDofPhase);
        phases.add(bloomNoDofPhase);
    }
    
    public void updatePipeline(boolean bloom, double bloomVal, boolean depthOfField, boolean ground){
        
        bloomAfterDofPhase.setEnable(bloom && depthOfField);
        bloomAfterDofPhase.setBloomFactor((float)bloomVal);
        bloomNoDofPhase.setEnable(bloom && !depthOfField);
        bloomNoDofPhase.setBloomFactor((float)bloomVal);
        
        horizontalBlurPhase.setEnable(depthOfField);
        verticalBlurPhase.setEnable(depthOfField);
        dofPhase.setEnable(depthOfField);
        
        direct1Phase.setEnable(!bloom && !depthOfField);
        direct2Phase.setEnable(!bloom && depthOfField);
        
        this.ground.setVisible(ground);
    }
    
    public static Mesh buildGround(){
        final FloatBuffer vertices = FloatBuffer.allocate(100*2*2*3);
        final FloatBuffer normals = FloatBuffer.allocate(100*2*2*3);
        final IntBuffer indexes = IntBuffer.allocate(100*2*2*2);

        final float[] normal = new float[]{0,0,1};
        int i=0;
        for(int x=-50;x<50;x++){
            vertices.put(x).put(0).put(-50);
            vertices.put(x).put(0).put(+50);
            normals.put(normal);
            normals.put(normal);
            indexes.put(i++);
            indexes.put(i++);
        }
        for(int z=-50;z<50;z++){
            vertices.put(-50).put(0).put(z);
            vertices.put(+50).put(0).put(z);
            normals.put(normal);
            normals.put(normal);
            indexes.put(i++);
            indexes.put(i++);
        }

        final Shell shell = new Shell();
        shell.setVertices(new VBO(vertices, 3));
        shell.setNormals(new VBO(normals, 3));
        shell.setIndexes(new IBO(indexes, 2), IBO.Range.LINES(0, indexes.capacity()));

        final Mesh mesh = new Mesh();
        mesh.setShell(shell);
        mesh.getMaterial().setLightVulnerable(false);
        mesh.getMaterial().putLayer(new Layer(new ColorMapping(Color.GRAY_DARK)));

        return mesh;
    }
    
}
