

package freeh.kitsune.ui;

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
        super();
        final FormLayout layout = new FormLayout();
        layout.setColumnSize(0, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(2, FormLayout.SIZE_EXPAND);
        layout.setRowSize(0, FormLayout.SIZE_EXPAND);
        layout.setRowSize(2, FormLayout.SIZE_EXPAND);
        
        setLayout(layout);
        addChild(new WSpace(new Extent(2)), new FormConstraint(0, 0));
        addChild(new WSpace(new Extent(2)), new FormConstraint(2, 2));
        
        setCenter(center);
    }
    
    public void setCenter(Widget center){
        if(center!=null){
            addChild(center, new FormConstraint(1, 1));
        }
    }
    
}
