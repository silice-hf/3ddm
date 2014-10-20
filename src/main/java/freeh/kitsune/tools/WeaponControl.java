

package freeh.kitsune.tools;

import freeh.kitsune.Game;
import un.api.character.Chars;
import un.engine.opengl.control.GestureTrigger;
import un.engine.opengl.control.LocalController;
import un.engine.ui.ievent.MouseEvent;

/**
 *
 */
public class WeaponControl extends ToolControl{
    
    private Weapon weapon = null;
    private WeaponFireTask fireTask = null;

    public WeaponControl() {
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void init(Tool tool, Game game) {
//        fireTask = new WeaponFireTask(Chars.EMPTY, 
//                GestureTrigger.createMouseTrigger(MouseEvent.BUTTON_1,MouseEvent.TYPE_PRESS,false),game);
    }

    public LocalController.GestureTask getGestureTask() {
        return fireTask;
    }

}
