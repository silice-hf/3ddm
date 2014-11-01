

package freeh.kitsune.stages.freemode;

import freeh.kitsune.Game;
import freeh.kitsune.GameProperties;
import freeh.kitsune.maps.MapSelector;
import freeh.kitsune.model.Model;
import freeh.kitsune.stages.DefaultStage;
import freeh.kitsune.stages.valley.Player;
import freeh.kitsune.stages.valley.PlayerMenu;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyBinding;
import un.api.event.PropertyEvent;
import un.api.layout.BorderConstraint;
import un.engine.opengl.GLUtilities;
import un.engine.opengl.animation.Updater;
import un.engine.opengl.control.PlayerNavigationMapConstraint;
import un.engine.opengl.control.PlayerOrientationConstraint;
import un.engine.opengl.light.AmbientLight;
import un.engine.opengl.light.DirectionalLight;
import un.engine.opengl.operation.GridBuilder;
import un.engine.ui.ievent.KeyEvent;
import un.engine.ui.widget.WButton;
import un.science.geometry.BBox;
import un.science.geometry.Extent;
import un.science.geometry.s3d.Grid;
import un.science.math.Angles;
import un.science.math.Vector;


/**
 *
 */
public class FreeModeStage extends DefaultStage{

    private final EventListener playerListener = new EventListener() {
        public void receiveEvent(Event event) {
            final PropertyEvent pe = (PropertyEvent) event;
            if(Player.PROPERTY_ACTIONMODE.equals(pe.getPropertyName())){
                //desactivate gesture control in action mode
                if(Boolean.TRUE.equals(pe.getNewValue())){
                    game.getFrame().glWindow.setPointerVisible(true);
                }else{
                    game.getFrame().glWindow.setPointerVisible(false);
                }
            }else if(Player.PROPERTY_FPSMODE.equals(pe.getPropertyName())){
                //change control mode
                if(player.isFpsMode()){
                    game.getGamePhases().setCamera(player.getFpsCamera());
                }else{
                    game.getGamePhases().setCamera(player.getOrbitCamera());
                }
            }
        }
    };
        
    private final WButton stageButton = new WButton(null,GameProperties.ICON_STAGE,new EventListener() {
        public void receiveEvent(Event event) {
            uiPlayer.switchVisible(mapsContainer, BorderConstraint.LEFT);
        }
    });
    private final WButton placeButton = new WButton(null,GameProperties.ICON_PLACE,null);
    private final WButton moveButton = new WButton(null,GameProperties.ICON_MOVE,null);
    
    private final MapSelector mapsContainer = new MapSelector();
    
    //we don't want the camera to have any roll and a max look up/down of 40°
    private final Updater cameraConstraint = new PlayerOrientationConstraint(
            Double.NaN, Double.NaN, Angles.degreeToRadian(-80), Angles.degreeToRadian(+80));    
    //navigation map
    private PlayerNavigationMapConstraint navConstraint;
    
    private Player player;
    private PlayerMenu uiPlayer;
        
    public FreeModeStage() {
        mapsContainer.setBestExtent(new Extent(400, 500));
        PropertyBinding.bidirectional(this, PROPERTY_MAP, mapsContainer, MapSelector.PROPERTY_MAP);
    }
     
    public Player getPlayer() {
        return player;
    }

    protected void keyEvent(KeyEvent ke) {
        super.keyEvent(ke);
        //27 == echap
        if(ke.getCode() == 17 && ke.getType() == KeyEvent.TYPE_PRESS){
            player.setActionMode(!player.isActionMode());
        }
    }
        
    public void install(Game game){
        super.install(game);
        game.getUI().setLoadingVisible();
        
        try{
            randomModel();
            randomDance();
            randomMap();
            final Model model = getModel();

            player = new Player(game);
            uiPlayer = new PlayerMenu(game,player);
            uiPlayer.setModel(model);
            uiPlayer.addLeftAction(stageButton);
            uiPlayer.addLeftAction(placeButton);
            uiPlayer.addLeftAction(moveButton);
            addChild(player);
            game.getGamePhases().setCamera(player.getFpsCamera());
            player.addEventListener(PropertyEvent.class, playerListener);
            player.setFpsMode(true);
            player.setActionMode(false);
                    
            final AmbientLight ambiantLight = new AmbientLight();
            ambiantLight.setColor(new Vector(0.4, 0.4, 0.4));
            ambiantLight.setAmbient(0.4f);
            ambiantLight.setDiffuse(0.1f);
            addChild(ambiantLight);
                        
//            final SpotLight light = new SpotLight();
//            light.getNodeTransform().getRotation().set(Matrix3.createRotation3(Angles.degreeToRadian(-90), new Vector(1, 0, 0)));
//            light.getNodeTransform().getTranslation().setXYZ(-7, 25, -7);
//            light.setFallOffAngle(25);
//            light.getNodeTransform().notifyChanged();
//            addChild(light);
            
            final DirectionalLight dl = new DirectionalLight();
            dl.setDirection(new Vector(-1,-1,-1).localNormalize());
            addChild(dl);
            
//            try{
//                final Image tiletex = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/skybox/tile.png"));
//                final SkyBox skyBox = new SkyBox(tiletex,tiletex,tiletex,tiletex,tiletex,tiletex);
//                addChild(skyBox);
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
            
            //build navigation map
            final BBox bbox = GLUtilities.calculateBBox(this);
            if(bbox.getSpan(0)>0 && bbox.getSpan(1)>0 && bbox.getSpan(2)>0){
                final Extent ext = new Extent(200, 200, 200);
                final GridBuilder gridBuilder = new GridBuilder(bbox, ext);
                gridBuilder.append(this);
                final Grid navMap = gridBuilder.getResult();
                final double cellHeight = navMap.getGridToGeom().get(1, 1);
                navConstraint = new PlayerNavigationMapConstraint(navMap);
                navConstraint.setGroundDistance(cellHeight*6);
            }
                        
        }catch(Exception ex){
            Game.LOGGER.warning(ex);
        }finally{
            game.getUI().setVisible(uiPlayer,true);
        }
        
    }
    
}
