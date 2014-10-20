
package freeh.kitsune.stages.deskmate;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import freeh.kitsune.audios.Musics;
import freeh.kitsune.model.Models;
import freeh.kitsune.model.dances.Dances;
import freeh.kitsune.model.poses.Poses;
import freeh.kitsune.stages.DefaultStage;
import freeh.kitsune.stages.valley.ShowCenterTask;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import un.engine.opengl.control.OrbitController;
import un.engine.opengl.light.AmbientLight;
import un.engine.opengl.light.SpotLight;
import un.engine.opengl.material.Layer;
import un.engine.opengl.material.mapping.ColorMapping;
import un.engine.opengl.material.mapping.ReflectionMapping;
import un.engine.opengl.mesh.BillBoard;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.mesh.Plan;
import un.engine.opengl.mesh.Shell;
import un.engine.opengl.resource.IBO;
import un.engine.opengl.resource.Texture2D;
import un.engine.opengl.resource.VBO;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.opengl.widget.NewtFrame;
import un.engine.ui.ievent.MouseEvent;
import un.science.encoding.color.Color;
import un.science.geometry.Extent;
import un.science.geometry.Point;
import un.science.math.Angles;
import un.science.math.Matrix3;
import un.science.math.Vector;

/**
 * Store what is being displayed.
 */
public class DeskMateStage extends DefaultStage {
                
    private final Mesh groundGrid = buildGridGround();
    private final Mesh groundMirror = buildMirrorGround();
    private final BillBoard marker = new BillBoard(new Texture2D(GameInfo.BIG_CROSSHAIR,false), 2, 2);
    private final GLNode cameraTarget = new GLNode();
    private final CameraMono camera = new CameraMono();
    private final DeskMateMenu menu = new DeskMateMenu(this);
        
    
    public DeskMateStage() {
        
    }
    
    protected void mouseEvent(MouseEvent me) {
        super.mouseEvent(me);
        if(!menuVisible && me.getButton()==MouseEvent.BUTTON_3 && me.getType()==MouseEvent.TYPE_RELEASE){
            if(game.getUI().isVisible(menu)){
                game.getUI().setNoneVisible();
            }else{
                game.getUI().setVisible(menu,true);
            }
        }
    }

    public void install(final Game game) {
        super.install(game);
        game.getUI().setLoadingVisible();
                
        final NewtFrame frame = game.getFrame();
        frame.setUndecorated(true);
        Extent ext = frame.getManager().getDisplaySize();
        frame.setOnScreenLocation(new Point(
                ext.get(0) - frame.getSize().get(0),
                ext.get(1) - frame.getSize().get(1)));
        
        try{
            buildScene();
            randomModel();
            randomMusic();

            game.getGamePhases().setCamera(camera);
        }finally{
            game.getUI().setNoneVisible();
            game.getUI().setVisible(menu,true);
        }
        
    }

    private void buildScene(){
        addChild(groundGrid);        
        addChild(groundMirror);        
        //lights
        AmbientLight ambiantLight = new AmbientLight();
        ambiantLight.setColor(new Vector(0.7, 0.7, 0.7));
        ambiantLight.setAmbient(0.5f);
        ambiantLight.setDiffuse(0.3f);
        addChild(ambiantLight);
                
        final SpotLight light = new SpotLight();
        light.getNodeTransform().getRotation().set(Matrix3.createRotation3(Angles.degreeToRadian(-90), new Vector(1, 0, 0)));
        light.getNodeTransform().getTranslation().setXYZ(0, 30, 0);
        light.setFallOffAngle(40);
        light.getNodeTransform().notifyChanged();
        addChild(light);
        
        
        camera.setNearPlane(0.1);
        camera.setFarPlane(1000);        
        marker.getMaterial().setLightVulnerable(false);
        marker.setVisible(false);
        cameraTarget.addChild(marker);
        cameraTarget.addChild(camera);
        //center.getFocus().setTarget(camera);
        
        addChild(cameraTarget);

        final OrbitController controller = new OrbitController(game.getFrame(),cameraTarget).configureDefault();
        controller.getGestureTasks().add(new ShowCenterTask.Show(marker));
        controller.getGestureTasks().add(new ShowCenterTask.Hide(marker));
        controller.setDistance(20);
        controller.setHorizontalAngle(Angles.degreeToRadian(30));
        controller.setVerticalAngle(Angles.degreeToRadian(25));
        camera.getUpdaters().add(controller);
        
    }
    
    public boolean isGridActive(){
        return groundGrid.isVisible();
    }
    
    public void setGridVisible(boolean visible){
        groundGrid.setVisible(visible);
    }
    
    public boolean isReflectActive(){
        return groundMirror.isVisible();
    }
    
    public void setReflectVisible(boolean visible){
        groundMirror.setVisible(visible);
    }
    
    private static Mesh buildGridGround(){
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
//        mesh.getMaterial().setLightVulnerable(false);
        mesh.getMaterial().putOrReplaceLayer(new Layer(new ColorMapping(Color.WHITE)));

        return mesh;
    }
    
    public static Mesh buildMirrorGround(){
        double size = 500;
        
        final Plan plan = new Plan(
                new Vector(-size, 0, -size),
                new Vector(-size, 0, +size),
                new Vector(+size, 0, +size),
                new Vector(+size, 0, -size));
        plan.getMaterial().putOrReplaceLayer(new Layer(new ReflectionMapping()));
        plan.getMaterial().getLayers().add(new Layer(new ColorMapping(new Color(255, 255, 255, 150)),Layer.TYPE_DIFFUSE,Layer.METHOD_MULTIPLY));
        plan.getMaterial().setLightVulnerable(false);
        plan.getShell().getNormals().setForgetOnLoad(false);
        plan.getShell().getVertices().setForgetOnLoad(false);
        plan.setCullFace(-1);
        
        return plan;
        
    }
}
