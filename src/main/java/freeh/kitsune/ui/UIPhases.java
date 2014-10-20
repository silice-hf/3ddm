

package freeh.kitsune.ui;

import un.api.event.AbstractEventSource;
import un.api.event.PropertyEvent;
import un.engine.opengl.GLC;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.phase.ClearPhase;
import un.engine.opengl.phase.DeferredRenderPhase;
import un.engine.opengl.phase.FBOResizePhase;
import un.engine.opengl.phase.FragmentCollector;
import un.engine.opengl.phase.PhaseSequence;
import un.engine.opengl.phase.RenderPhase;
import un.engine.opengl.phase.UpdatePhase;
import un.engine.opengl.phase.effect.DirectPhase;
import un.engine.opengl.resource.FBO;
import un.engine.opengl.resource.Texture;
import un.engine.opengl.resource.Texture2DMS;
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
    private final int subSampling   = 4;
    
    //world update phase
    private final FBOResizePhase resizePhase;
    private final UpdatePhase updatePhase;
        
    //main rendering phase
    private final ClearPhase clearRenderFboMsPhase;
    private final ClearPhase clearRenderFboPhase;
    private final RenderPhase renderPhase;
    private final FBO renderFboMs;
    private final FBO renderFbo;
    private final Texture colorTex;
    
    private final DirectPhase paintPhase;
    
    public UIPhases(GLProcessContext glContext) {
        this.glContext = glContext;
        this.glContext.getPhases().add(phases);
        
        scene.addChild(camera);
        
        final int width = (int) size.get(0);
        final int height = (int) size.get(1);
        
        //build phases
        resizePhase = new FBOResizePhase();
        updatePhase = new UpdatePhase(scene);
        
        phases.getPhases().add(resizePhase);
        phases.getPhases().add(updatePhase);
        
        //main rendering phase
        renderFboMs = new FBO(width, height);
        renderFboMs.addAttachment(GLC.FBO.Attachment.COLOR_0, new Texture2DMS(width, height, Texture2DMS.COLOR_RGBA, subSampling));
        renderFboMs.addAttachment(GLC.FBO.Attachment.DEPTH, new Texture2DMS(width, height, Texture2DMS.DEPTH_24, subSampling)); 
        renderFbo = renderFboMs.createBlitFBO();
        renderPhase = new DeferredRenderPhase(camera, scene, renderFboMs,
                new FragmentCollector[]{
                    DeferredRenderPhase.OUT_DIFFUSE},
                false);
        renderPhase.setBlitFbo(renderFbo);
        colorTex            = renderFbo.getTexture(GLC.FBO.Attachment.COLOR_0);
        
        clearRenderFboMsPhase = new ClearPhase(renderFboMs);
        clearRenderFboPhase = new ClearPhase(renderFbo);
        paintPhase = new DirectPhase(colorTex);
                
        phases.getPhases().add(clearRenderFboMsPhase);
        phases.getPhases().add(clearRenderFboPhase);
        phases.getPhases().add(renderPhase);
        phases.getPhases().add(paintPhase);
        
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
