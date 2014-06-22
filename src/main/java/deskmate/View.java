
package deskmate;

import com.jogamp.openal.AL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.event.EventListener;
import un.api.event.EventManager;
import un.api.event.EventSource;
import un.api.event.PropertyEvent;
import un.api.media.AudioSamples;
import un.api.media.AudioStreamMeta;
import un.api.media.MediaReader;
import un.api.media.MediaStore;
import un.api.media.Medias;
import un.engine.openal.resource.ALBuffer;
import un.engine.openal.resource.ALSource;
import un.engine.opengl.GLC;
import un.engine.opengl.GLExecutable;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.animation.Animation;
import un.engine.opengl.animation.Updater;
import un.engine.opengl.material.Layer;
import un.engine.opengl.material.mapping.ColorMapping;
import un.engine.opengl.mesh.Crosshair3D;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.MultipartMesh;
import un.engine.opengl.mesh.Shell;
import un.engine.opengl.phase.ClearPhase;
import un.engine.opengl.phase.DeferredRenderPhase;
import un.engine.opengl.phase.FBOResizePhase;
import un.engine.opengl.phase.RenderContext;
import un.engine.opengl.phase.UpdatePhase;
import un.engine.opengl.phase.effect.BloomPhase;
import un.engine.opengl.phase.effect.DOFPhase;
import un.engine.opengl.phase.effect.DirectPhase;
import un.engine.opengl.phase.effect.FastGaussianBlurPhase;
import un.engine.opengl.physic.Skeletons;
import un.engine.opengl.resource.FBO;
import un.engine.opengl.resource.IBO;
import un.engine.opengl.resource.Texture2D;
import un.engine.opengl.resource.Texture2DMS;
import un.engine.opengl.resource.VBO;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.opengl.scenegraph.GLNode;
import un.science.encoding.ArrayOutputStream;
import un.science.encoding.DataOutputStream;
import un.science.encoding.IOException;
import un.science.encoding.NumberEncoding;
import un.science.encoding.color.Color;
import un.science.math.Matrix;
import un.science.math.Tuple;
import un.science.math.Vector;
import un.science.math.Vectors;
import un.storage.imagery.process.ConvolutionMatrices;
import un.storage.imagery.process.ConvolutionMatrix;
import un.system.path.Path;
import un.system.path.Paths;

/**
 * Store what is being displayed.
 */
public class View implements EventSource{
    
    public static final Path DATAPATH = Paths.resolve("file>./data");
    
    public static final Chars PROPERTY_MODEL = new Chars("model");
    public static final Chars PROPERTY_ANIM = new Chars("anim");
    public static final Chars PROPERTY_AUDIO = new Chars("audio");
    
    public final Sequence allModels = new ArraySequence();
    public final Sequence allAnimations = new ArraySequence();
    public final Sequence allAudios = new ArraySequence();
    
    public GLProcessContext glContext;
    public final GLNode scene = new GLNode();
    public final Mesh ground = buildGround();
    public final Crosshair3D cameraTarget = new Crosshair3D();
    public final CameraMono camera = new CameraMono();
    
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
    
    //Music source
    private final ALSource audioSource = new ALSource();
    
    public Path currentModelPath;
    public MultipartMesh currentModel;
    
    public Path currentAnimationPath;
    public Animation currentAnimation;
    
    public Path currentAudioPath;
    public Object currentAudio;
    
    //for events
    private final EventManager events = new EventManager();
    
    public void buildPipeline(){
                
        //default phases
        clearPhase = new ClearPhase();
        resizePhase = new FBOResizePhase();
        updatePhase = new UpdatePhase(scene);
                
        int width = 800;
        int height = 600;
        
        //rendering
        final int sampling = 4;
        final FBO fboMS = new FBO(width, height);
        fboMS.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2DMS(width, height, Texture2DMS.COLOR_RGBA, sampling));
        fboMS.addAttachment(GLC.FBO.Attachment.COLOR_1, new Texture2DMS(width, height, Texture2DMS.VEC3_FLOAT, sampling));
        fboMS.addAttachment(GLC.FBO.Attachment.DEPTH, new Texture2DMS(width, height, Texture2DMS.DEPTH_24, sampling)); 
        final FBO fbo1 = fboMS.createBlitFBO();
        
        
        renderPhase = new DeferredRenderPhase(camera,scene,fboMS, 
                new DeferredRenderPhase.Mapping[]{
                    DeferredRenderPhase.OUT_DIFFUSE,
                    DeferredRenderPhase.OUT_POSITION_CAMERA},
                true);
        renderPhase.setBlitFbo(fbo1);
        direct1Phase = new DirectPhase(fbo1.getColorTexture());
        direct1Phase.setEnable(false);
        
        // Deth of field
        final FBO fbo2 = new FBO(width, height);
        fbo2.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2D(width, height,Texture2D.COLOR_RGBA_CLAMPED));
        final FBO fbo3 = new FBO(width, height);
        fbo3.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2D(width, height,Texture2D.COLOR_RGBA_CLAMPED));
        
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
        phases.add(new ClearPhase(fboMS));
        phases.add(new ClearPhase(fbo1));
        phases.add(new ClearPhase(fbo2));
        phases.add(new ClearPhase(fbo3));
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
        
        //update the depth of field parameters to always focus on the camera target
        camera.getUpdaters().add(new Updater() {
            public void update(RenderContext context, long nanotime, GLNode node) {
                Matrix m = cameraTarget.calculateNodeToView(camera);
                Tuple t = m.transform(new Vector(0,0,0),1);
                double focalDistance = Vectors.length(t.getValues());
                dofPhase.setFocalPlaneDepth((float) focalDistance);
                dofPhase.setNearBlurDepth((float) (focalDistance/10.0));
                dofPhase.setFarBlurDepth((float) (focalDistance+50));
            }
        });
        
        updatePipeline(false, 0.01, false, false);
        
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
    
    
    public synchronized void changeModel(){
        int modelIndex = (int) (Math.random() * (allModels.getSize()- 1));
        Path path = (Path) allModels.get(modelIndex);
        changeModel(path);
    }
    
    public synchronized void changeModel(Path path){
        if(path.equals(currentModelPath)) return;
        currentModelPath = path;
        
        unloadModel();
        
        //load new model
        currentModel = DataLoader.loadModel(path);
        if(currentModel!=null){
            scene.addChild(currentModel);
        }
        
        events.sendPropertyEvent(this, PROPERTY_MODEL, null, currentModel);
    }
    
    public synchronized void changeAnimation(){
        int animIndex = (int) (Math.random() * (allAnimations.getSize()- 1));
        Path path = (Path) allAnimations.get(animIndex);
        changeAnimation(path);
    }
    
    public synchronized void changeAnimation(Path path){
        if(path.equals(currentAnimationPath)) return;
        currentAnimationPath = path;
        
        stopAnimation();
        
        //load new animation
        currentAnimation = DataLoader.loadAnimation(path);
        if(currentModel!=null && currentAnimation!=null){
            Skeletons.mapAnimation(currentAnimation, currentModel.getSkeleton(), currentModel);
            currentModel.getUpdaters().add(currentAnimation);
            currentAnimation.play();
        }
        
        events.sendPropertyEvent(this, PROPERTY_ANIM, null, currentAnimation);
    }
    
    public synchronized void changeAudio(){
        int animIndex = (int) (Math.random() * (allAudios.getSize()- 1));
        Path path = (Path) allAudios.get(animIndex);
        changeAudio(path);
    }
    
    public synchronized void changeAudio(Path path){
        if(path.equals(currentAudioPath)) return;
        currentAudioPath = path;
        
        stopAudio();
        
        //load new audio
        try{
            final MediaStore store = Medias.open(path);

            //tranform audio in a supported byte buffer
            final AudioStreamMeta meta = (AudioStreamMeta) store.getStreamsMeta()[0];
            final MediaReader reader = store.createReader(null);

            //recode stream in a stereo 16 bits per sample.
            final ArrayOutputStream out = new ArrayOutputStream();
            final DataOutputStream ds = new DataOutputStream(out, NumberEncoding.LITTLE_ENDIAN);

            while(reader.hasNext()){
                reader.next();
                final AudioSamples samples = (AudioSamples) reader.getBuffers()[0];
                final int[] audiosamples = samples.asPCM(null, 16);
                if(audiosamples.length==1){
                    ds.writeUShort(audiosamples[0]);
                    ds.writeUShort(audiosamples[0]);
                }else{
                    ds.writeUShort(audiosamples[0]);
                    ds.writeUShort(audiosamples[1]);
                }
            }

            final byte[] all = out.getBuffer().toArray();

            //buffer which contains audio datas
            final ALBuffer data = new ALBuffer();
            data.setFormat(AL.AL_FORMAT_STEREO16);
            data.setFrequency((int) meta.getSampleRate());
            data.setSize(all.length);
            data.setData(ByteBuffer.wrap(all));
            data.load();
            
            audioSource.setBuffer(data);
            audioSource.load();
            audioSource.play();

        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        events.sendPropertyEvent(this, PROPERTY_AUDIO, null, currentAudio);
    }
    
    public void unloadModel(){
        stopAnimation();
        if (currentModel != null) {
            final GLNode trash = currentModel;
            //release model
            glContext.addTask(new GLExecutable() {
                public Object execute() {
                    scene.removeChild(trash);
                    trash.dispose(context);
                    return null;
                }
            });
            currentModel = null;
            currentModelPath = null;
        }
    }
        
    public void stopAnimation(){
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentModel.getUpdaters().remove(currentAnimation);
            currentAnimation = null;
            currentAnimationPath = null;
        }
    }
    
    public void stopAudio(){
        if(audioSource.isLoaded()){
            audioSource.stop();
            audioSource.unload();
        }
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

    @Override
    public Class[] getEventClasses() {
        return new Class[]{PropertyEvent.class};
    }

    @Override
    public void addEventListener(Class eventClass, EventListener listener) {
        events.addEventListener(eventClass, listener);
    }

    @Override
    public void removeEventListener(Class eventClass, EventListener listener) {
        events.removeEventListener(eventClass, listener);
    }
    
}
