

package freeh.kitsune.ui;

import freeh.kitsune.Game;
import freeh.kitsune.GameBundle;
import freeh.kitsune.GameInfo;
import freeh.kitsune.stages.deskmate.DeskMateStage;
import freeh.kitsune.stages.freemode.FreeModeStage;
import freeh.kitsune.stages.valley.ValleyStage;
import java.util.logging.Level;
import java.util.logging.Logger;
import un.api.character.Chars;
import un.api.character.DefaultLanguage;
import un.api.collection.Iterator;
import un.api.country.Country;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.image.Image;
import un.api.image.Images;
import un.engine.ui.ievent.MouseEvent;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.Widget;
import un.storage.keyvalue.TranslationStore;
import un.system.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class MainMenu extends WContainer {
    
    private final WButton newGame = new WButton(GameBundle.get(new Chars("menu.newgame")), new EventListener() {
        public void receiveEvent(Event event) {
            try {
                game.setStage(new ValleyStage());
            } catch (Exception ex) {
                Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
    private final WButton freeMode = new WButton(GameBundle.get(new Chars("menu.freemode")), new EventListener() {
        public void receiveEvent(Event event) {
            try {
                game.setStage(new FreeModeStage());
            } catch (Exception ex) {
                Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
    private final WButton deskmate = new WButton(GameBundle.get(new Chars("menu.deskmate")), new EventListener() {
        public void receiveEvent(Event event) {
            try {
                game.setStage(new DeskMateStage());
            } catch (Exception ex) {
                Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
        
    private final WButton controls = new WButton(GameBundle.get(new Chars("menu.control")), new EventListener() {
        public void receiveEvent(Event event) {
            if(game.getUI().isControlVisible()){
                game.getUI().setMenuVisible();
            }else{
                game.getUI().setControlVisible();
            }
        }
    });
    
    private final WButton credits = new WButton(GameBundle.get(new Chars("menu.credits")), new EventListener() {
        public void receiveEvent(Event event) {
            if(game.getUI().isCreditVisible()){
                game.getUI().setMenuVisible();
            }else{
                game.getUI().setCreditVisible();
            }
        }
    });
    
    private final WButton config = new WButton(GameBundle.get(new Chars("menu.config")), new EventListener() {
        public void receiveEvent(Event event) {
            if(game.getUI().isConfigVisible()){
                game.getUI().setMenuVisible();
            }else{
                game.getUI().setConfigVisible();
            }
            
        }
    });
    
    private final WButton exit = new WButton(GameBundle.get(new Chars("menu.exit")), new EventListener() {
        public void receiveEvent(Event event) {
            System.exit(0);
        }
    });
 
    private final Game game;
    
    public MainMenu(final Game game){
        this.game = game;
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_MARGIN, new Chars("[10,10,10,10]"));
        final FormLayout layout = new FormLayout();
        setLayout(layout);
        layout.setDefaultRowSpace(10);
        layout.setRowSize(1, FormLayout.SIZE_EXPAND);
        layout.setRowSize(9, FormLayout.SIZE_EXPAND);
        
        newGame.setEnable(false);
        freeMode.setEnable(false);
        controls.setEnable(false);
        
        final WContainer flagsContainer = new WContainer(new FormLayout());
        flagsContainer.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        ((FormLayout)flagsContainer.getLayout()).setDefaultColumnSpace(5);
        ((FormLayout)flagsContainer.getLayout()).setColumnSize(0,FormLayout.SIZE_EXPAND);
        addChild(flagsContainer, new FormConstraint(0, 0, FormConstraint.FILL_BOTH));
        addChild(newGame, new FormConstraint(0, 2));
        addChild(freeMode, new FormConstraint(0, 3));
        addChild(deskmate, new FormConstraint(0, 4));
        addChild(controls, new FormConstraint(0, 5));
        addChild(config, new FormConstraint(0, 6));
        addChild(credits, new FormConstraint(0, 7));
        addChild(exit, new FormConstraint(0, 8));
        
        
        //show a flag for each language available        
        final TranslationStore store = GameBundle.getTextBundle();
        final Iterator ite = store.getLanguages();
        int x = 1;
        int y = 0;
        while(ite.hasNext()){
            final DefaultLanguage lg = (DefaultLanguage) ite.next();
            final Country cnt = (Country) lg.getDivisions()[0];
            final Path flagPath = Paths.resolve(GameInfo.PATH_RESOURCE+"/flags/png/64x42/flags-"+cnt.getISOCode3().toString()+".png");
            Image flagImage = null;
            try{
                flagImage = Images.read(flagPath);
            }catch(Exception ex){
                ex.printStackTrace();
                //no flag image for this country.
            }
            
            final WLabel lbl = new WLabel();
            lbl.setHorizontalAlignment(WLabel.HALIGN_CENTER);
            if(flagImage==null){
                lbl.setText(cnt.getISOCode3());
            }else{
                lbl.setImage(flagImage);
            }
            flagsContainer.addChild(lbl, new FormConstraint(x++, y));
            
            lbl.addEventListener(MouseEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                final MouseEvent me = (MouseEvent) event;
                if(me.getType() == MouseEvent.TYPE_PRESS && me.getButton()== MouseEvent.BUTTON_1){
                    game.setLanguage(lg);
                }
            }});
        }
        
    }
    
}
