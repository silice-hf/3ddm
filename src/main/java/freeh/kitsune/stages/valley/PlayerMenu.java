

package freeh.kitsune.stages.valley;

import freeh.kitsune.Game;
import freeh.kitsune.GameProperties;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.ModelSelector;
import freeh.kitsune.model.clothes.ClotheStateSelector;
import freeh.kitsune.ui.ActionLayer;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyBinding;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.CircularLayout;
import un.api.layout.StackConstraint;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;

/**
 *
 */
public class PlayerMenu extends ActionLayer {
    
    public static final Chars PROPERTY_MODEL = ModelSelector.PROPERTY_MODEL;
        
    private final WButton clotheButton = new WButton(null,GameProperties.ICON_CLOTHE_ON,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(clothesContainer, BorderConstraint.LEFT);
        }
    });
    private final WButton modelButton = new WButton(null,GameProperties.ICON_MODEL,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(modelsContainer, BorderConstraint.CENTER);
        }
    });
    private final WButton orbitButton = new WButton(null,GameProperties.ICON_ORBIT,new EventListener() {
        public void receiveEvent(Event event) {
            player.setFpsMode(!player.isFpsMode());
        }
    });
        
    private final WButton weaponButton = new WButton(null,GameProperties.ICON_WEAPON,null);
    private final WButton favoriteButton = new WButton(null,GameProperties.ICON_FAVORITE,new EventListener() {
        public void receiveEvent(Event event) {
            favoritesContainer.setVisible(!favoritesContainer.isVisible());
        }
    });
    private final WButton toolButton = new WButton(null,GameProperties.ICON_TOY,null);
    private final WButton motionButton = new WButton(null,GameProperties.ICON_MOTION,null);
    private final WButton lockButton = new WButton(null,GameProperties.ICON_LOCK,null);
    private final WButton cumButton = new WButton(null,GameProperties.ICON_CUM,null);
    
    private final Game game;
    private Player player;
    private Model model;
    
    private final WContainer subContainer = new WContainer(new BorderLayout());
    private final WContainer favoritesContainer = new WContainer(new CircularLayout());
    private final ClotheStateSelector clothesContainer = new ClotheStateSelector();
    private final ModelSelector modelsContainer = new ModelSelector();
    
    public PlayerMenu(Game game, Player player){
        this.game = game;
        this.player = player;
        
        //bindings
        PropertyBinding.bidirectional(this, PROPERTY_MODEL, clothesContainer, ClotheStateSelector.PROPERTY_MODEL);
        PropertyBinding.bidirectional(this, PROPERTY_MODEL, modelsContainer, ModelSelector.PROPERTY_MODEL);
        
                             
        clothesContainer.setBestExtent(new Extent(400, 500));
        modelsContainer.setBestExtent(new Extent(900, 900));
        
        favoritesContainer.getStyle().getSelfRule().setProperties(
                new Chars("border-fill-paint   : colorfill($back-a88);\n"
                        + "border-brush        : plainbrush(1,'round');\n"
                        + "border-brush-paint  : colorfill($back-aFF);"));
        for(int i=0;i<10;i++){
            favoritesContainer.addChild(new WButton(new Chars("+"+i)));
        }
        favoritesContainer.setVisible(false);
                
        addChild(subContainer,new StackConstraint(2));        
        subContainer.addChild(favoritesContainer,BorderConstraint.CENTER);
        
        addLeftAction(clotheButton);
        addLeftAction(modelButton);
        addLeftAction(orbitButton);
        
        addRightAction(weaponButton);
        addRightAction(favoriteButton);
        addRightAction(toolButton);
        addRightAction(motionButton);
        addRightAction(lockButton);
        addRightAction(cumButton);
                        
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        if(this.model == model) return;
        final Model old = this.model;
        this.model = model;
        getEventManager().sendPropertyEvent(this, PROPERTY_MODEL, old, model);
    }
    
}
