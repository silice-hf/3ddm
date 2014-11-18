

package freeh.kitsune.ui;

import freeh.kitsune.GameInfo;
import un.api.array.Arrays;
import un.api.tree.Node;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.api.layout.StackConstraint;
import un.api.layout.StackLayout;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.Widget;

/**
 *
 */
public class ActionLayer extends WContainer{
    
    private final WContainer leftSide = new WContainer(new FormLayout());
    private final WContainer rightSide = new WContainer(new FormLayout());
    private final WContainer leftBar = new WContainer(new FormLayout());
    private final WContainer rightBar = new WContainer(new FormLayout());
        
    private final WContainer borderContainer = new WContainer(new BorderLayout());
    private final WContainer centerContainer = new WContainer();
    
    public ActionLayer(){
        super(new StackLayout());
        
        final FormLayout layout = new FormLayout();
        layout.setRowSize(0, FormLayout.SIZE_EXPAND);
        layout.setRowSize(2, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(1, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(2, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(3, FormLayout.SIZE_EXPAND);
        centerContainer.setLayout(layout);
        
        borderContainer.addChild(leftSide, BorderConstraint.LEFT);
        borderContainer.addChild(centerContainer, BorderConstraint.CENTER);
        borderContainer.addChild(rightSide, BorderConstraint.RIGHT);
              
        leftSide.setVisible(false);
        rightSide.setVisible(false);
        
        leftBar.getFlags().add(GameInfo.FLAG_TOOLBAR_LEFT);
        rightBar.getFlags().add(GameInfo.FLAG_TOOLBAR_RIGHT);    
        configStyleSide(leftSide);
        configStyleSide(rightSide);        
        leftSide.addChild(leftBar, new FormConstraint(0, 1));
        rightSide.addChild(rightBar, new FormConstraint(0, 1));
        
        addChild(borderContainer,new StackConstraint(1));
    }
    
    public void addLeftAction(WButton button){
        button.getFlags().add(GameInfo.FLAG_TOOLBAR_BUTTON);
        leftBar.addChild(button, new FormConstraint(0, leftBar.getChildCount()));
        leftSide.setVisible(true);
    }
    
    public void addRightAction(WButton button){
        button.getFlags().add(GameInfo.FLAG_TOOLBAR_BUTTON);
        rightBar.addChild(button, new FormConstraint(0, rightBar.getChildCount()));
        rightSide.setVisible(true);
    }
    
    /**
     * Set widget visible or not on main pane.
     */
    public void switchVisible(Widget widget, BorderConstraint position){
        final Node[] children = centerContainer.getChildren();
        
        if(Arrays.contains(children, widget)){
            centerContainer.removeChildren();
        }else{
            centerContainer.removeChildren();
            widget.getFlags().add(GameInfo.FLAG_INNERPANE);
            if(position == BorderConstraint.LEFT){
                centerContainer.addChild(widget, new FormConstraint(0, 1, 1, 1, FormConstraint.FILL_BOTH));
            }else if(position == BorderConstraint.CENTER){
                centerContainer.addChild(widget, new FormConstraint(2, 1, 1, 1, FormConstraint.FILL_BOTH));
            }else if(position == BorderConstraint.RIGHT){
                centerContainer.addChild(widget, new FormConstraint(4, 1, 1, 1, FormConstraint.FILL_BOTH));
            }
        }
    }
    
    private static void configStyleSide(WContainer container){        
        final FormLayout layout = (FormLayout) container.getLayout();
        layout.setDefaultColumnSpace(0);
        layout.setDefaultRowSpace(0);
        layout.setRowSize(0, FormLayout.SIZE_EXPAND);
        layout.setRowSize(2, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(0, FormLayout.SIZE_EXPAND);
    }
        
}
