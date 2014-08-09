package deskmate;

import deskmate.config.SceneConfigPane;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.engine.opengl.control.OrbitController;
import un.engine.opengl.light.AmbiantLight;
import un.engine.opengl.light.DirectionalLight;
import un.engine.opengl.widget.NewtFrame;
import un.engine.ui.Frame;
import un.engine.ui.FrameManagers;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.FormConstraint;
import un.engine.ui.layout.FormLayout;
import un.engine.ui.layout.GridLayout;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WPopup;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.menu.WMenuButton;
import un.science.encoding.IOException;
import un.science.geometry.Extent;
import un.science.geometry.Point;
import un.science.math.Angles;
import un.api.image.Images;
import un.system.path.Paths;

/**
 * scene frame
 */
public class DeskMateFrame {

    private final NewtFrame frame = new NewtFrame(true, false);
    private final View view = new View();
    private final SceneConfigPane sceneConfigPane = new SceneConfigPane(view);

    public DeskMateFrame() throws IOException {
        
        //search models and animations
        Config.search(view.allModels, view.allAnimations, view.allPoses, view.allAudios);
        
        view.glContext = frame.getContext(); 
        view.buildPipeline();
        buildScene();
        
        WContainer container = frame.getContainer();
        container.getStyle().getSelfRule().setProperty(new Chars("background"),new Chars("none"));
        container.setLayout(new BorderLayout());

        WContainer popup = new WPopup();
        popup.setLayout(new FormLayout());
        
        int y=0;
        WLabel lbl = new WLabel(null, Images.read(Paths.resolve("mod>/style/logo.png")));
        lbl.getStyle().getSelfRule().setProperty(new Chars("margin"),new Chars("3"));
        popup.addChild(lbl, new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WMenuButton(new Chars("Next Model"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                view.changeModel();
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WMenuButton(new Chars("Next Animation"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                view.changeAnimation();
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WMenuButton(new Chars("Next Pose"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                view.changePose();
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WMenuButton(new Chars("Next Music"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                view.changeAudio();
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WMenuButton(new Chars("Show Border"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                frame.setUndecorated(!frame.isUndecorated());
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
         popup.addChild(new WMenuButton(new Chars("Config"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                new Thread(){
                    @Override
                    public void run() {
                        Frame frame = FrameManagers.getFrameManager().createFrame(false);
                        frame.setTitle(new Chars("Configuration"));
                        WContainer container = frame.getContainer();
                        container.setLayout(new BorderLayout());
                        container.addChild(sceneConfigPane, BorderConstraint.CENTER);
                        frame.setSize(640, 480);
                        frame.setVisible(true);
                    }
                }.start();
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WSpace(new Extent(20, 20)), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        popup.addChild(new WMenuButton(new Chars("Exit"), null, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                System.exit(0);
            }
        }), new FormConstraint(0, y++, FormConstraint.FILL_BOTH));
        container.setPopup(popup);
        
        //place the screen in lower right cornerf
        frame.setTitle(new Chars("DeskMate"));
        frame.setUndecorated(true);
        frame.setSize(800, 600);
        Extent ext = frame.getManager().getDisplaySize();
        frame.setOnScreenLocation(new Point(
                ext.get(0) - frame.getSize().get(0),
                ext.get(1) - frame.getSize().get(1)));


        //show the frame
        frame.setVisible(true);

        view.changeModel();
        view.changeAnimation();
        view.changeAudio();
    }

    private void buildScene(){
        view.scene.addChild(view.ground);        
        //lights
        AmbiantLight alight = new AmbiantLight();
        DirectionalLight dlight = new DirectionalLight();
        view.scene.addChild(alight);
        view.scene.addChild(dlight);
        
        view.scene.addChild(view.cameraTarget);
        view.scene.addChild(view.camera);

        OrbitController controller = new OrbitController(frame,view.cameraTarget).configureDefault();
        controller.setDistance(20);
        controller.setHorizontalAngle(Angles.degreeToRadian(30));
        controller.setVerticalAngle(Angles.degreeToRadian(25));
        view.camera.getUpdaters().add(controller);
        
    }
        
}
