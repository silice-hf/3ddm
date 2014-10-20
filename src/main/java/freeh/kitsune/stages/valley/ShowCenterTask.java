

package freeh.kitsune.stages.valley;

import un.api.character.Chars;
import un.engine.opengl.control.GestureState;
import un.engine.opengl.control.GestureTrigger;
import un.engine.opengl.control.OrbitController;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.ui.ievent.MouseEvent;

/**
 *
 */
public class ShowCenterTask{

    public static class Show extends OrbitController.GestureTask{

        private final GLNode target;
    
        public Show(GLNode target) {
            super(Chars.EMPTY, GestureTrigger.createInstantMouseTrigger(GestureTrigger.ANY_BUTTON_OR_KEY, MouseEvent.TYPE_PRESS));
            this.target = target;
        }
        
        public void execute(GestureState state, OrbitController control) {
            target.setVisible(true);
        }
        
    }
    
    public static class Hide extends OrbitController.GestureTask{

        private final GLNode target;
    
        public Hide(GLNode target) {
            super(Chars.EMPTY, GestureTrigger.createInstantMouseTrigger(GestureTrigger.ANY_BUTTON_OR_KEY, MouseEvent.TYPE_RELEASE));
            this.target = target;
        }
        
        public void execute(GestureState state, OrbitController control) {
            target.setVisible(false);
        }
        
    }
        
}
