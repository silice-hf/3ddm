

package freeh.kitsune.ui;

import freeh.kitsune.Game;
import freeh.kitsune.GameBundle;
import freeh.kitsune.GameInfo;
import un.api.character.Chars;
import un.engine.opengl.phase.GamePhases;
import un.engine.opengl.widget.WGameGraphics;
import un.api.event.PropertyBinding;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.engine.ui.widget.WCheck;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;

/**
 *
 */
public class GraphicsConfigurator extends WContainer{
    
    //FULLSCREEN
    private final WLabel fullscreenLbl      = new WLabel(GameBundle.get(new Chars("config.fullscreen")));
    private final WCheck fullscreen         = new WCheck(new Chars(" "));
    
    private final WGameGraphics gameEngineConfig;
    
    public GraphicsConfigurator(Game game) {
        final GamePhases gamePhases = game.getGamePhases();
        getFlags().add(GameInfo.FLAG_MAINPANE);
        
        final FormLayout layout = new FormLayout();
        layout.setDefaultRowSpace(10);
        setLayout(layout);
        
        //FULLSCREEN
        addChild(fullscreenLbl,     new FormConstraint(0, 0));
        addChild(fullscreen,        new FormConstraint(1, 0));
        PropertyBinding.bidirectional(game, Game.PROPERTY_FULLSCREEN, fullscreen, WCheck.PROPERTY_CHECK);
        
        gameEngineConfig = new WGameGraphics(gamePhases, GameBundle.getTextBundle());
        addChild(gameEngineConfig,  new FormConstraint(0, 1, 2, 1));
                
    }
        
}
