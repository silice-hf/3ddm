

package freeh.kitsune;

import com.jogamp.opengl.JoglVersion;
import freeh.kitsune.stages.Stage;
import freeh.kitsune.stages.StartingStage;
import freeh.kitsune.ui.UI;
import freeh.kitsune.ui.UIPhases;
import java.awt.GridLayout;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import un.api.character.Chars;
import un.api.character.Language;
import un.api.country.Country;
import un.api.event.AbstractEventSource;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.api.logging.FileLogger;
import un.api.logging.Logger;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.phase.ClearPhase;
import un.engine.opengl.phase.GamePhases;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.opengl.widget.NewtFrame;
import un.engine.ui.io.WCSParser;
import un.engine.ui.style.SystemStyle;
import un.api.io.IOException;
import un.science.encoding.color.AlphaBlending;
import un.science.geometry.Extent;
import un.science.geometry.Point;
import un.system.path.Paths;

/**
 * Main game class.
 */
public class Game extends AbstractEventSource {
        
    public static final Logger LOGGER;
    static {
        try {
            LOGGER = new FileLogger(Paths.resolve("file>./log.txt"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static final Chars PROPERTY_FULLSCREEN = new Chars("FullScreen");
    public static final Chars PROPERTY_LANGUAGE = new Chars("Language");
    
    private final NewtFrame frame;
    
    //game context pipeline
    private final GLProcessContext glcontext;
    private final ClearPhase clearPhase = new ClearPhase();
    private final GamePhases gamePhases;
    
    //ui context
    private final UIPhases uiPhases;
    private final UI ui;
    
    //game state
    private Stage currentStage;
    
    //configuration
    private Language language = null;
    private boolean fullscreen = false;
    
    public Game(){
        
        //log debug infos
        final JoglVersion jogl = JoglVersion.getInstance(); 
        LOGGER.info(new Chars(jogl.toString()));
        frame = new NewtFrame(false,false){
            @Override
            public void init(GLAutoDrawable e) {
                try{
                    super.init(e);
                    LOGGER.info(new Chars(jogl.toString(frame.getContext().getGL())));
                }catch(Throwable ex){
                    LOGGER.critical(ex);
                }
            }

            @Override
            public void display(GLAutoDrawable e) {
                try{
                    super.display(e);
                }catch(Throwable ex){
                    LOGGER.critical(ex);
                }
            }

            @Override
            public void reshape(GLAutoDrawable e, int i, int i1, int i2, int i3) {
                try{
                    super.reshape(e, i, i1, i2, i3);
                }catch(Throwable ex){
                    LOGGER.critical(ex);
                }
            }

            @Override
            public void dispose() {
                try{
                    super.dispose();
                }catch(Throwable ex){
                    LOGGER.critical(ex);
                }
            }
            
        };        
        frame.getContext().getPhases().removeAll();
        frame.getContext().getPhases().add(new ClearPhase(new float[]{0.1f,0.1f,0.1f,1.0f}));
        
        
        glcontext = frame.getContext();
        gamePhases = new GamePhases();
        gamePhases.getPaintPhase().setBlending(AlphaBlending.create(AlphaBlending.SRC,1.0f));
        glcontext.getPhases().add(clearPhase);
        glcontext.getPhases().add(gamePhases);
        uiPhases = new UIPhases(glcontext);
//        uiPhases.getPaintPhase().setBlending(AlphaBlending.create(AlphaBlending.SRC_OVER,1.0f));
        
        //load configuration
        final GameProperties props = GameProperties.INSTANCE;
        gamePhases.setAAState(props.getPropertyChars(GamePhases.PROPERTY_AASTATE, GamePhases.ANTIALIAZING_NONE));
        gamePhases.setShadowEnable(   props.getPropertyBoolean(GamePhases.PROPERTY_SHADOW, false));
        gamePhases.setShadowTextureSize(props.getPropertyInt(GamePhases.PROPERTY_SHADOW_TEXTURE_SIZE, 512));
        gamePhases.setDofEnable(      props.getPropertyBoolean(GamePhases.PROPERTY_DOF, false));
        gamePhases.setDofFocalMinPlane( props.getPropertyFloat(GamePhases.PROPERTY_DOF_MIN, 5f));
        gamePhases.setDofFocalMaxPlane( props.getPropertyFloat(GamePhases.PROPERTY_DOF_MAX, 15f));
        gamePhases.setDofBlurRadius(    props.getPropertyFloat(GamePhases.PROPERTY_DOF_RADIUS, 4f));
        gamePhases.setBloomEnable(    props.getPropertyBoolean(GamePhases.PROPERTY_BLOOM, false));
        gamePhases.setBloomValue(     props.getPropertyFloat(GamePhases.PROPERTY_BLOOM_VALUE, 0.1f));
        gamePhases.setSsaoEnable(     props.getPropertyBoolean(GamePhases.PROPERTY_SSAO, false));
        gamePhases.setSsaoRadius(     props.getPropertyFloat(GamePhases.PROPERTY_SSAO_RADIUS, 0.3f));
        gamePhases.setSsaoIntensity(  props.getPropertyFloat(GamePhases.PROPERTY_SSAO_INTENSITY, 2.0f));
        gamePhases.setSsaoScale(      props.getPropertyFloat(GamePhases.PROPERTY_SSAO_SCALE, 1.0f));
        gamePhases.setSsaoBias(       props.getPropertyFloat(GamePhases.PROPERTY_SSAO_BIAS, 0.5f));
        setFullScreen(                props.getPropertyBoolean(PROPERTY_FULLSCREEN, false));
        language = Country.getForIsoCode3(props.getPropertyChars(PROPERTY_LANGUAGE, new Chars("GBR"))).asLanguage();
        //listen to configuration change for saving
        final EventListener propListener = new EventListener() {
            public void receiveEvent(Event event) {
                final PropertyEvent pe = (PropertyEvent) event;
                props.setProperty(pe.getPropertyName(), pe.getNewValue());
            }
        };
        gamePhases.addEventListener(PropertyEvent.class, propListener);
        addEventListener(PropertyEvent.class, propListener);
                
        //build interface
        ui = new UI(this);
                
        //load the menu stage
        setStage(new StartingStage());
        
        //show frame
        frame.setTitle(new Chars(GameInfo.NAME));
        final Extent screenSize = frame.getManager().getDisplaySize();
        frame.setSize(1024, 768);
        final Extent frameSize = frame.getSize();
        frame.setOnScreenLocation(new Point(
                (screenSize.get(0) - frameSize.get(0))/2,
                (screenSize.get(1) - frameSize.get(1))/2));
        frame.setVisible(true);
    }

    public NewtFrame getFrame() {
        return frame;
    }

    public GLProcessContext getGlcontext() {
        return glcontext;
    }

    public ClearPhase getClearPhase() {
        return clearPhase;
    }

    public UIPhases getUIPhases() {
        return uiPhases;
    }

    public GamePhases getGamePhases() {
        return gamePhases;
    }

    public UI getUI() {
        return ui;
    }
    
    public Stage getStage(){
        return currentStage;
    }
    
    public void setStage(Stage stage){
        if(currentStage!=null){
            //set a empty stage
            final GLNode scene = new GLNode();
            final CameraMono camera = new CameraMono();
            scene.addChild(camera);
            getGamePhases().setScene(scene);
            getGamePhases().setCamera(camera);
            currentStage.uninstall();
            currentStage.dispose();
        }
        currentStage = stage;
        if(currentStage!=null){
            currentStage.install(this);
            gamePhases.setScene(stage);
        }
    }
    
    public boolean isFullScreen() {
        return fullscreen;
    }

    public void setFullScreen(boolean fullscreen) {
        if(fullscreen==this.fullscreen) return;
        this.fullscreen = fullscreen;
        frame.setFullscreen(fullscreen);
        getEventManager().sendPropertyEvent(this, PROPERTY_FULLSCREEN, !fullscreen, fullscreen);
    }
    
    public void setLanguage(Language lg) {
        if(lg==this.language) return;
        this.language = lg;        
        ui.setLanguage(lg);
        getEventManager().sendPropertyEvent(this, PROPERTY_LANGUAGE, null, ((Country)language.getDivisions()[0]).getISOCode3());
    }
    
    public Language getLanguage(){
        return language;
    }
    
    public Class[] getEventClasses() {
        return new Class[]{PropertyEvent.class};
    }
    
    public static void main(String[] args) {
        checkMinimumSpec();
        try {
            SystemStyle.INSTANCE.getRules().addAll(WCSParser.readStyle(Paths.resolve("mod>/style/game-theme.wcs")).getRules());
        } catch (IOException ex) {
            Game.LOGGER.warning(new Chars("Failed to load default style : "+ex.getMessage()),ex);
        }
        new Game();
    }

    private static void checkMinimumSpec(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}
        
        if(!GLProfile.isAvailable(GLProfile.GL4)){
            final JPanel pane = new JPanel(new GridLayout(-1, 1));
            pane.add(new JLabel("Your graphic card does not support OpenGL 4, try updating your driver."));
            pane.add(new JLabel(""));
            pane.add(new JLabel("Minimum graphic card"));
            pane.add(new JLabel("- Nvidia GeForce 400 series"));
            pane.add(new JLabel("- AMD Radeon 5000 series"));
                        
            JOptionPane.showMessageDialog(null, pane, "Minimum spec error", JOptionPane.ERROR_MESSAGE);
            
            System.exit(0);
        }
        
    }
    
}
