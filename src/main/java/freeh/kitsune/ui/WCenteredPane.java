

package freeh.kitsune.ui;

import un.api.character.Chars;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;

/**
 *
 */
public class WCenteredPane extends WContainer{

    public WCenteredPane(Widget center) {
        super(new FormLayout());
        getStyle().getSelfRule().setProperties(new Chars(
            "background : none\n"));
        addChild(new WSpace(new Extent(2)), new FormConstraint(0, 0));
        addChild(new WSpace(new Extent(2)), new FormConstraint(2, 2));
        addChild(center, new FormConstraint(1, 1));
    }
    
    
    
}
