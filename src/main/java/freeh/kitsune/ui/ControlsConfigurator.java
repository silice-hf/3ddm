

package freeh.kitsune.ui;

import un.api.character.Chars;
import un.engine.ui.widget.WContainer;

/**
 *
 */
public class ControlsConfigurator extends WContainer {

    public ControlsConfigurator() {
        
        getStyle().getSelfRule().setProperties(
                new Chars("margin              : [15,15,15,15]\n"
                        + "background          : none\n"
                        + "border-margin       : [14,14,14,14]\n"
                        + "border-radius       : [30,30,30,30]\n"
                        + "border-fill-paint   : $env-background\n"
                        + "border-brush        : plainbrush(1,'round')\n"
                        + "border-brush-paint  : colorfill($color-main-3)"));
        
        
        
    }
    
    
    
}
