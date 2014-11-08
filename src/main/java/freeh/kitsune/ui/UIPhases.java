

package freeh.kitsune.ui;

import un.api.event.AbstractEventSource;
import un.api.event.PropertyEvent;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.phase.DeferredRenderPhase;
import un.engine.opengl.phase.FragmentCollector;
import un.engine.opengl.phase.PhaseSequence;
import un.engine.opengl.phase.RenderPhase;
import un.engine.opengl.phase.UpdatePhase;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.opengl.scenegraph.GLNode;
import un.science.geometry.Extent;

/**
 * UI rendering pipeline.
 * 
 */
public class UIPhases extends AbstractEventSource {
        
    private final GLProcessContext glContext;
    private final PhaseSequence phases = new PhaseSequence();

    private final GLNode scene      = new GLNode();
    private final CameraMono camera = new CameraMono();
    private final Extent size       = new Extent(1024, 768);
    
//    //world update phase
    private final UpdatePhase updatePhase;
        
    //main rendering phase
    private final RenderPhase renderPhase;
    
    public UIPhases(GLProcessContext glContext) {
        this.glContext = glContext;
        this.glContext.getPhases().add(phases);
        
        scene.addChild(camera);
                
        //build phases
        updatePhase = new UpdatePhase(scene);
        renderPhase = new DeferredRenderPhase(camera, scene, null,
                new FragmentCollector[]{
                    DeferredRenderPhase.OUT_DIFFUSE},
                false);   
        
        phases.getPhases().add(updatePhase);
        phases.getPhases().add(renderPhase);        
    }

    public GLNode getScene() {
        return scene;
    }

    public CameraMono getCamera() {
        return camera;
    }
    
    public GLProcessContext getGlContext() {
        return glContext;
    }

    @Override
    public Class[] getEventClasses() {
        return new Class[]{PropertyEvent.class};
    }
    
}
