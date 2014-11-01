

package freeh.kitsune.tools;

import com.jogamp.openal.AL;
import freeh.kitsune.Game;
import freeh.kitsune.stages.valley.Player;
import java.nio.ByteBuffer;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
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
import un.engine.opengl.control.GestureState;
import un.engine.opengl.control.GestureTrigger;
import un.engine.opengl.control.LocalController;
import un.engine.opengl.material.Layer;
import un.engine.opengl.material.mapping.UVMapping;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.Shell;
import un.engine.opengl.painter.gl3.GL3Painter2D;
import un.engine.opengl.phase.picking.PickEvent;
import un.engine.opengl.physic.Skeletons;
import un.engine.opengl.resource.FBO;
import un.engine.opengl.resource.FBOAttachment;
import un.engine.opengl.resource.Texture2D;
import un.engine.opengl.resource.VBO;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.painter2d.ColorPaint;
import un.api.io.ArrayOutputStream;
import un.api.io.DataOutputStream;
import un.api.io.NumberEncoding;
import un.science.encoding.color.AlphaBlending;
import un.science.encoding.color.Color;
import un.science.geometry.Point;
import un.science.geometry.s2d.Circle;
import un.science.geometry.s2d.Triangle;
import un.science.geometry.s3d.Ray;
import un.science.math.Matrix;
import un.science.math.Vector;
import un.science.math.Vectors;
import un.api.path.Path;

/**
 *
 */
public class WeaponFireTask extends LocalController.GestureTask {
    
    private final Game game;
    private final Player player;
    private ALSource alsource;
    
    private double lastShot = 0;
    
    //cache painter
    private GL3Painter2D painter;
    
    public WeaponFireTask(Chars name, GestureTrigger trigger, Game game, Player player) {
        super(name, trigger);
        this.game = game;
        this.player = player;
    }
    
    public void setSound(Path path){
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
                ds.writeUShort(audiosamples[0]);
                ds.writeUShort(audiosamples[1]);
            }

            final byte[] all = out.getBuffer().toArray();

            //buffer which contains audio datas
            final ALBuffer data = new ALBuffer();
            data.setFormat(AL.AL_FORMAT_STEREO16);
            data.setFrequency((int) meta.getSampleRate());
            data.setSize(all.length);
            data.setData(ByteBuffer.wrap(all));
            data.load();

            //the audio source
            alsource = new ALSource();
            alsource.setLoop(false);
            alsource.setBuffer(data);
            alsource.load();
        }catch(Exception ex){
            Game.LOGGER.warning(ex);
        }
    }
    
    public void execute(GestureState state, LocalController control) {
            
        lastShot += control.getDeltaTime();

//        if(lastShot<0.2){
//            return;
//        }
//
//        lastShot %= 0.2;

        if(alsource!=null){
            alsource.play();
        }
            
        final int mx = (int) ((game.getGamePhases().getSize().get(0) /2.0));// + (Math.random()*12)-6);
        final int my = (int) ((game.getGamePhases().getSize().get(1) /2.0));// + (Math.random()*12)-6);
            
        game.getGamePhases().getPickingPhase().pickAt(new Vector(mx,my), new EventListener() {
            public void receiveEvent(Event event) {
                final PickEvent pe = (PickEvent) event;

                final Mesh mesh = (Mesh) pe.getSelection();
                if(mesh==null) return;

                //find the texture coordinate
                final Shell shell = (Shell) mesh.getShape();

                final int[] vids = pe.findVertexId(null);
                final VBO uvs = shell.getUVs();
                final float[] uv0 = uvs.getTupleFloat(vids[0], null);
                final float[] uv1 = uvs.getTupleFloat(vids[1], null);
                final float[] uv2 = uvs.getTupleFloat(vids[2], null);

                //triangle
                final Matrix ntr = mesh.getNodeToRootSpace();
                Vector v0 = Skeletons.evaluatePosition(mesh, vids[0]);
                Vector v1 = Skeletons.evaluatePosition(mesh, vids[1]);
                Vector v2 = Skeletons.evaluatePosition(mesh, vids[2]);
                v0 = (Vector)ntr.transform(v0, 1);
                v1 = (Vector)ntr.transform(v1, 1);
                v2 = (Vector)ntr.transform(v2, 1);

                final CameraMono camera;
                if(player.isFpsMode()){
                    camera = player.getFpsCamera();
                }else{
                    camera = player.getOrbitCamera();
                }
                
                final Triangle triangle = new Triangle(v0,v1,v2);
                final Ray ray = camera.calculateRayWorldCS(mx,my);

                final Vector hit = new Vector(2);
                try{
                    final Point pt = (Point) triangle.intersection(ray);
                    final double[] bary = Triangle.getBarycentricValue(
                            v0.getValues(), 
                            v1.getValues(), 
                            v2.getValues(), 
                            pt.getCoordinate().getValues());

                    hit.localAdd(new Vector(Vectors.scale(uv0, (float)bary[0])));
                    hit.localAdd(new Vector(Vectors.scale(uv1, (float)bary[1])));
                    hit.localAdd(new Vector(Vectors.scale(uv2, (float)bary[2])));

                }catch(Exception ex){
                    Game.LOGGER.warning(ex);
                }

                final Layer layer = mesh.getMaterial().getLayer(Layer.TYPE_DIFFUSE);
                final UVMapping mapping = (UVMapping) layer.getMapping();
                final Texture2D tex = mapping.getTexture();
                final int width = tex.getWidth();
                final int height = tex.getHeight();

                final FBO fbo = new FBO(width, height);
                fbo.addAttachment(new FBOAttachment(GLC.FBO.Attachment.COLOR_0, tex));

                final GLProcessContext cp = game.getGlcontext();
                if(painter==null){
                    painter = new GL3Painter2D(new FBO(1, 1),0,cp);      
                    painter.setClip(null);
                    painter.setPaint(new ColorPaint(new Color(0, 0, 0, 0)));
                    painter.setAlphaBlending(AlphaBlending.create(AlphaBlending.SRC,1));
                }
                
                painter.getWorker().fbo = fbo;
                painter.setSize(width, height);
                
                hit.localMultiply(new Vector(tex.getWidth(), tex.getHeight()));
                painter.fill(new Circle(hit, 60));
                

                cp.addTask(painter.getWorker());
                cp.addTask(new GLExecutable() {
                    public Object execute() {
                        fbo.removeAttachment(GLC.FBO.Attachment.COLOR_0);
                        fbo.unloadFromGpuMemory(context.getGL());
                        return null;
                    }
                });

            }
        });
    }
}
    
