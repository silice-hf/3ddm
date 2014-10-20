

package freeh.kitsune.stages.valley;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import freeh.kitsune.tools.Tool;
import freeh.kitsune.tools.WeaponFireTask;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.image.Image;
import un.api.image.Images;
import un.engine.opengl.control.GestureTrigger;
import un.engine.opengl.control.LocalController;
import un.engine.opengl.control.OrbitController;
import un.engine.opengl.material.Layer;
import un.engine.opengl.material.mapping.UVMapping;
import un.engine.opengl.mesh.BillBoard;
import un.engine.opengl.mesh.Plan;
import un.engine.opengl.resource.Texture2D;
import un.engine.opengl.resource.VBO;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.ui.ievent.MouseEvent;
import un.science.encoding.IOException;
import un.science.geometry.Extent;
import un.science.math.DefaultTuple;
import un.science.math.Vector;
import un.system.path.Paths;

/**
 *
 */
public class Player extends GLNode {
    
    public static final Chars PROPERTY_TOOL = new Chars("Tool");
    public static final Chars PROPERTY_ACTIONMODE = new Chars("ActionMode");
    public static final Chars PROPERTY_FPSMODE = new Chars("FPSMode");
    public static final Image IMAGE_CENTER;
    static {
        try {
            IMAGE_CENTER = Images.read(Paths.resolve("file>./resources/crosshair2.gif"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private final Sequence tools = new ArraySequence();
    private Tool tool;
    private boolean actionMode = false;
    private boolean fpsMode = false;
    
    private final CameraMono fpsCamera = new CameraMono();
    private final CameraMono orbitCamera = new CameraMono();
    private final GLNode orbitParent = new GLNode();
    private final BillBoard orbitTarget = new BillBoard(new Texture2D(IMAGE_CENTER),1f,1f);
    private final LocalController fpsController;
    private final OrbitController orbitController;

    public Player(Game game) {
        
        orbitTarget.getMaterial().setLightVulnerable(false);
//        orbitTarget.getFocus().setTarget(orbitCamera);
        orbitTarget.setVisible(false);
        orbitParent.addChild(orbitTarget);
        
        fpsCamera.setNearPlane(0.1);
        fpsCamera.setFarPlane(1000);
        
        orbitCamera.setNearPlane(0.1);
        orbitCamera.setFarPlane(1000);
        
        addChild(fpsCamera);
        addChild(orbitParent);
        orbitParent.addChild(orbitCamera);
        
        orbitController = new OrbitController(game.getFrame(), orbitParent).configureDefault();
        orbitController.setMinDistance(0.0001);
        orbitController.setDistance(0.1f);
        orbitController.setMaxDistance(100f);
        orbitController.setWarpPointer(new DefaultTuple(200, 200));
        orbitController.getGestureTasks().add(new ShowCenterTask.Show(orbitTarget));
        orbitController.getGestureTasks().add(new ShowCenterTask.Hide(orbitTarget));
                
        fpsController = new LocalController(game.getFrame(), this, new Vector(0, 1, 0), new Vector(1, 0, 0));
        fpsController.setWarpPointer(new DefaultTuple(200, 200));        
        final Sequence gestureTasks = fpsController.getGestureTasks();
        gestureTasks.add(new LocalController.MoveForward(GestureTrigger.createRepeatKeyTrigger('z')));
        gestureTasks.add(new LocalController.MoveBackward(GestureTrigger.createRepeatKeyTrigger('s')));
        gestureTasks.add(new LocalController.MoveLeft(GestureTrigger.createRepeatKeyTrigger('q')));
        gestureTasks.add(new LocalController.MoveRight(GestureTrigger.createRepeatKeyTrigger('d')));
        gestureTasks.add(new LocalController.RotateHorizontal(GestureTrigger.createMouseTrigger(GestureTrigger.ANY_BUTTON_OR_KEY,MouseEvent.TYPE_MOVE,true)));
        gestureTasks.add(new LocalController.RotateVertical(GestureTrigger.createMouseTrigger(GestureTrigger.ANY_BUTTON_OR_KEY,MouseEvent.TYPE_MOVE,true)));
        gestureTasks.add(new LocalController.RotateRoll(GestureTrigger.createMouseTrigger(MouseEvent.BUTTON_3,MouseEvent.TYPE_MOVE,true)));
        gestureTasks.add(new WeaponFireTask(Chars.EMPTY, 
                GestureTrigger.createMouseTrigger(MouseEvent.BUTTON_1,MouseEvent.TYPE_PRESS,false), game,this));
        
        orbitCamera.getUpdaters().add(orbitController);
        getUpdaters().add(fpsController);
        
        //crosshair
        final Plan plan = new Plan(
            new Vector(-1, -1, 0),
            new Vector(1, -1, 0),
            new Vector(1, 1, 0),
            new Vector(-1, 1, 0));
        plan.getMaterial().setLightVulnerable(false);
//        plan.setPickable(false);
//        plan.getExtShaderActors().add(new PickActor(pickingPhase, plan));
        fpsCamera.addChild(plan);

        final UVMapping text = new UVMapping(new Texture2D(GameInfo.SMALL_CROSSHAIR));
        plan.getShell().setUVs(new VBO(new float[]{0,0,1,0,1,1,0,1},2));
        plan.getMaterial().putOrReplaceLayer(new Layer(text));
        plan.getNodeTransform().getTranslation().setXYZ(0, 0, -1);
        plan.getNodeTransform().getScale().setXYZ(0.01, 0.01, 0.01);
        plan.getNodeTransform().notifyChanged();
        
        
        setActionMode(true);
        setFpsMode(true);
    }

    public CameraMono getFpsCamera() {
        return fpsCamera;
    }

    public CameraMono getOrbitCamera() {
        return orbitCamera;
    }

    public Sequence getTools() {
        return tools;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool activeTool) {
        if(this.tool==activeTool) return;
        final Tool old = this.tool;
        this.tool = activeTool;
        getEventManager().sendPropertyEvent(this, PROPERTY_TOOL, old, activeTool);
    }

    public boolean isActionMode() {
        return actionMode;
    }

    public void setActionMode(boolean actionMode) {
        if(this.actionMode==actionMode) return;
        this.actionMode = actionMode;
        getEventManager().sendPropertyEvent(this, PROPERTY_ACTIONMODE, !actionMode, actionMode);
        
        if(actionMode){
            fpsController.setEnable(false);
            orbitController.setEnable(false);
        }else{
            fpsController.setEnable(fpsMode);
            orbitController.setEnable(!fpsMode);
        }
    }

    public boolean isFpsMode() {
        return fpsMode;
    }

    public void setFpsMode(boolean fpsMode) {
        if(this.fpsMode==fpsMode) return;
        this.fpsMode = fpsMode;
        getEventManager().sendPropertyEvent(this, PROPERTY_FPSMODE, !fpsMode, fpsMode);
        
        if(actionMode){
            fpsController.setEnable(false);
            orbitController.setEnable(false);
        }else{
            fpsController.setEnable(fpsMode);
            orbitController.setEnable(!fpsMode);
        }
    }
        
}
