

package freeh.kitsune.ui;

import un.api.character.Chars;
import un.api.tree.Node;
import un.engine.opengl.widget.WMediaPreview;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.Widget;
import un.science.encoding.IOException;
import un.science.geometry.Extent;
import un.science.math.Maths;
import un.system.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class WLoading extends WContainer{
    
    private WMediaPreview animated;

    
    public WLoading() {
        super(new FormLayout());
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_MARGIN, new Chars("[15,15,15,15]"));
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        
        ((FormLayout)getLayout()).setDefaultRowSpace(10);
        ((FormLayout)getLayout()).setDefaultColumnSpace(30);
        ((FormLayout)getLayout()).setColumnSize(0, FormLayout.SIZE_EXPAND);
        ((FormLayout)getLayout()).setRowSize(0, FormLayout.SIZE_EXPAND);
    }

    public synchronized void updateLoader(){
        
        //change loading image
        final Path folder = Paths.resolve("file>./resources/loaders");
        final Node[] nodes = folder.getChildren();
        final int index = Maths.clip((int)(Math.random()*nodes.length),0,nodes.length);

        if(animated!=null){
            removeChild(animated);
        }

        try {
            this.animated = new WMediaPreview((Path)nodes[index]);
            animated.setBestExtent(new Extent(120, 120));
            addChild(animated, new FormConstraint(0, 0));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }
       
}
