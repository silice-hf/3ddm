

package freeh.kitsune.stages;

import freeh.kitsune.Game;
import freeh.kitsune.toys.Toy;
import freeh.kitsune.toys.Toys;
import un.api.image.Image;
import un.api.image.Images;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.GLUtilities;
import un.engine.opengl.animation.Updater;
import un.engine.opengl.control.OrbitController;
import un.engine.opengl.light.PointLight;
import un.engine.opengl.mesh.SkyBox;
import un.engine.opengl.phase.RenderContext;
import un.engine.opengl.scenegraph.Camera;
import un.engine.opengl.scenegraph.GLNode;
import un.science.geometry.BBox;
import un.science.math.Angles;
import un.science.math.Maths;
import un.system.path.Paths;

/**
 *
 */
public class MenuStage extends Stage{

    private OrbitController controller;
    private Updater rotator;
    
    public MenuStage() {
    }
     
    public void install(Game game){
        super.install(game);
        
        final PointLight light = new PointLight();
        addChild(light);
        
        try{
            final Image tiletex = Images.read(Paths.resolve("file>./resources/game/skybox/tile.png"));
            final SkyBox skyBox = new SkyBox(tiletex,tiletex,tiletex,tiletex,tiletex,tiletex);
            addChild(skyBox);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        try{            
              
            final float dist;
            final GLNode orbitTarget;
            
            final Toy toy = Toys.getRandomToy();
            if(toy!=null){
                final GLNode toyModel = toy.createModel().getNode();
                final BBox bbox = GLUtilities.calculateBBox(toyModel);
                orbitTarget = new GLNode();
                orbitTarget.getNodeTransform().getTranslation().set(bbox.getMiddle());
                orbitTarget.getNodeTransform().notifyChanged();
                toyModel.addChild(orbitTarget);
                addChild(toyModel);

                //set camera at good distance
                final double fov = game.getGamePhases().getCamera().getFieldOfView();
                final double spanX = bbox.getSpan(0);
                final double distX = spanX / Math.tan(fov);
                final double spanY = bbox.getSpan(1);
                final double distY = spanY / Math.tan(fov);
                // x2 because screen space is [-1...+1]
                // x1.2 to compensate perspective effect
                dist = (float)(Maths.max(distX,distY) * 2.0 * 1.2);
            }else{
                orbitTarget = new GLNode();
                addChild(orbitTarget);
                dist = 20;
            }
            
            
            controller = new OrbitController(game.getFrame(),orbitTarget);
            rotator = new Updater() {
                double angle = 0;
                public void update(RenderContext context, GLNode node) {
                    controller.setDistance(dist);
                    angle += context.getDiffTimeSecond()*2;
                    controller.setVerticalAngle(Angles.degreeToRadian(15));
                    controller.setHorizontalAngle(Angles.degreeToRadian(angle));
                }
            };
            
            final Camera camera = gamePipeline.getCamera();
            
            orbitTarget.addChild(camera);
            camera.getUpdaters().add(rotator);
            camera.getUpdaters().add(controller);
            
        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage(),ex);
        }
        
        game.getUI().setMenuVisible();
    }

    public void dispose(GLProcessContext context) {
        gamePipeline.getCamera().getUpdaters().remove(rotator);
        gamePipeline.getCamera().getUpdaters().remove(controller);
        super.dispose(context);
    }
    
}
