

package freeh.kitsune.ui;

import freeh.kitsune.GameProperties;
import un.api.array.Arrays;
import un.api.character.Chars;
import un.api.tree.Node;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.api.layout.StackConstraint;
import un.api.layout.StackLayout;
import un.engine.ui.style.StyleRule;
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
    
    private final WButton visibleLeftButton = new WButton();
    private final WButton visibleRightButton = new WButton();
    
    private final WContainer borderContainer = new WContainer(new BorderLayout());
    private final WContainer centerContainer = new WContainer();
    
    public ActionLayer(boolean showHideButton){
        super(new StackLayout());
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        
        final FormLayout layout = new FormLayout();
        layout.setRowSize(0, FormLayout.SIZE_EXPAND);
        layout.setRowSize(2, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(1, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(2, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(3, FormLayout.SIZE_EXPAND);
        centerContainer.setLayout(layout);
        centerContainer.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        
        borderContainer.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        borderContainer.addChild(leftSide, BorderConstraint.LEFT);
        borderContainer.addChild(centerContainer, BorderConstraint.CENTER);
        borderContainer.addChild(rightSide, BorderConstraint.RIGHT);
              
        leftSide.setVisible(false);
        rightSide.setVisible(false);
        
        configStyleBar(leftBar, true);
        configStyleBar(rightBar, false);        
        configStyleSide(leftSide);
        configStyleSide(rightSide);        
        configStyleVisible(visibleLeftButton,true,showHideButton);        
        configStyleVisible(visibleRightButton,false,showHideButton);  
        leftSide.addChild(leftBar, new FormConstraint(0, 1));
        leftSide.addChild(visibleLeftButton, new FormConstraint(0, 2));
        rightSide.addChild(rightBar, new FormConstraint(0, 1));
        rightSide.addChild(visibleRightButton, new FormConstraint(0, 2));
        
        addChild(borderContainer,new StackConstraint(1));
    }
    
    public void addLeftAction(WButton button){
        configStyleButton(button);
        leftBar.addChild(button, new FormConstraint(0, leftBar.getChildCount()));
        leftSide.setVisible(true);
    }
    
    public void addRightAction(WButton button){
        configStyleButton(button);
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
            configPaneStyle(widget);
            if(position == BorderConstraint.LEFT){
                centerContainer.addChild(widget, new FormConstraint(0, 1, 1, 1, FormConstraint.FILL_BOTH));
            }else if(position == BorderConstraint.CENTER){
                centerContainer.addChild(widget, new FormConstraint(2, 1, 1, 1, FormConstraint.FILL_BOTH));
            }else if(position == BorderConstraint.RIGHT){
                centerContainer.addChild(widget, new FormConstraint(4, 1, 1, 1, FormConstraint.FILL_BOTH));
            }
        }
    }
    
    private static void configPaneStyle(Widget container){    
        container.getStyle().getSelfRule().setProperties(new Chars(
                "background          : none\n" +
                "margin              : [30,30,30,30]\n" +
                "border-margin       : [20,20,20,20]\n" +
                "border-radius       : [30,30,30,30]\n" +
                "border-brush        : plainbrush(2,'round')\n" +
                "border-brush-paint  : colorfill(#FFFFFF)\n" +
                "border-fill-paint   : lineargradientfill('%',0,0,1,1,0,#FFFFFF33,0.3,#FFFFFF88,1,#FFFFFF33)\n"));
    }
    
    private static void configStyleBar(WContainer container, boolean left){
        final StyleRule rule = container.getStyle().getSelfRule();                
        if(left){
            rule.setProperties(new Chars(
                    "background          : none\n" +
                    "margin              : [0,0,0,0]\n" +
                    "border-radius       : [0,30,0,0]\n" +
                    "border-brush        : plainbrush(2,'round')\n" +
                    "border-brush-paint  : colorfill(#FFFFFF)\n" +
                    "border-fill-paint   : lineargradientfill('%',0,0,1,1,0,#FFFFFF33,0.3,#FFFFFF88,1,#FFFFFF33)\n" +
                    "border-margin       : [0,0,0,0]"));
        }else{
            rule.setProperties(new Chars(
                    "background          : none\n" +
                    "margin              : [0,0,0,0]\n" +
                    "border-radius       : [30,0,0,0]\n" +
                    "border-brush        : plainbrush(2,'round')\n" +
                    "border-brush-paint  : colorfill(#FFFFFF)\n" +
                    "border-fill-paint   : lineargradientfill('%',0,0,1,1,0,#FFFFFF33,0.3,#FFFFFF88,1,#FFFFFF33)\n" +
                    "border-margin       : [0,0,0,0]"));
        }
    }
    
    private static void configStyleSide(WContainer container){
        final StyleRule rule = container.getStyle().getSelfRule();
        rule.setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        rule.setProperty(Widget.STYLE_PROP_MARGIN, new Chars("[0,0,0,0]"));
        
        final FormLayout layout = (FormLayout) container.getLayout();
        layout.setDefaultColumnSpace(0);
        layout.setDefaultRowSpace(0);
        layout.setRowSize(0, FormLayout.SIZE_EXPAND);
        layout.setRowSize(3, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(0, FormLayout.SIZE_EXPAND);
    }
    
    private static void configStyleButton(WButton candidate){
                
        final StyleRule rule = candidate.getStyle().getSelfRule();        
        rule.setProperties(new Chars(
                "background          : none\n" +
                "margin              : [14,14,14,14]\n" +
                "border-radius       : [0,0,0,0]\n" +
                "border-brush        : plainbrush(0,'round')\n" +
                "border-brush-paint  : colorfill(#FFFFFF00)\n" +
                "border-fill-paint   : colorfill(#FFFFFF00)\n" +
                "border-margin       : [14,14,14,14]"));
    }
    
    private static void configStyleVisible(WButton candidate, boolean left, boolean showButton){     
        if(showButton){
            candidate.setImage(GameProperties.ICON_LOCK);
        }
        candidate.setEnable(showButton);
        
        final StyleRule rule = candidate.getStyle().getSelfRule();
        if(left){
            rule.setProperties(new Chars(
                    "background          : none\n" +
                    "margin              : [14,14,14,14]\n" +
                    "border-radius       : [0,0,30,0]\n" +
                    "border-brush        : plainbrush(2,'round')\n" +
                    "border-brush-paint  : colorfill(#FFFFFF)\n" +
                    "border-fill-paint   : colorfill(#FFFFFF88)\n" +
                    "border-margin       : [16,14,14,14]"));
        }else{
            rule.setProperties(new Chars(
                    "background          : none\n" +
                    "margin              : [14,14,14,14]\n" +
                    "border-radius       : [0,0,0,30]\n" +
                    "border-brush        : plainbrush(2,'round')\n" +
                    "border-brush-paint  : colorfill(#FFFFFF)\n" +
                    "border-fill-paint   : colorfill(#FFFFFF88)\n" +
                    "border-margin       : [16,14,14,14]"));
        }
    }
}
