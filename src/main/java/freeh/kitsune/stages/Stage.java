

package freeh.kitsune.stages;

import freeh.kitsune.Game;
import un.api.event.Event;
import un.api.event.EventListener;
import un.engine.opengl.GLExecutable;
import un.engine.opengl.phase.GamePhases;
import un.engine.opengl.scenegraph.CameraMono;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.ui.ievent.KeyEvent;
import un.engine.ui.ievent.MouseEvent;

/**
 *
 */
public abstract class Stage extends GLNode {
    
    protected Game game;
    protected GamePhases gamePipeline;

    private final EventListener mouseAndKeyListener = new EventListener() {
        public void receiveEvent(Event event) {
            if(event instanceof MouseEvent){
                mouseEvent((MouseEvent)event);
            }else if(event instanceof KeyEvent){
                keyEvent((KeyEvent)event);
            }
        }
    };
    
    public Stage() {
    }

    public Game getGame() {
        return game;
    }
    
    public void install(Game game){
        this.game = game;
        this.gamePipeline = game.getGamePhases();
        
        game.getFrame().addEventListener(KeyEvent.class, mouseAndKeyListener);
        game.getFrame().addEventListener(MouseEvent.class, mouseAndKeyListener);
    }
        
    public void uninstall(){
        game.getFrame().removeEventListener(KeyEvent.class, mouseAndKeyListener);
        game.getFrame().removeEventListener(MouseEvent.class, mouseAndKeyListener);
    }
    
    public void dispose(){
        uninstall();
        game.getGlcontext().addTask(new GLExecutable() {
            public Object execute() {
                dispose(context);
                return null;
            }
        });
    }
    
    protected void keyEvent(KeyEvent ke){}
    
    protected void mouseEvent(MouseEvent me){}
    
    
}
