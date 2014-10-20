

package freeh.kitsune.ui;

import freeh.kitsune.audios.Music;
import freeh.kitsune.items.Item;
import freeh.kitsune.maps.Map;
import freeh.kitsune.model.dances.Dance;
import un.engine.ui.model.DefaultObjectPresenter;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.Widget;

/**
 *
 */
public class GameObjectPresenter extends DefaultObjectPresenter{

    public static final GameObjectPresenter INSTANCE = new GameObjectPresenter();
    
    public Widget createWidget(Object candidate) {
        final WLabel label = (WLabel) super.createWidget(candidate);
        
        if(candidate instanceof Map){
            label.setText(((Map)candidate).getName());
        }else if(candidate instanceof Item){
            label.setText(((Item)candidate).getName());
        }else if(candidate instanceof Music){
            label.setText(((Music)candidate).getName());
        }else if(candidate instanceof Dance){
            label.setText(((Dance)candidate).getName());
        }
        
        return label;
    }
    
}
