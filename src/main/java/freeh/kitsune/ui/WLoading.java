

package freeh.kitsune.ui;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import un.api.tree.Node;
import un.engine.opengl.widget.WMediaPreview;
import un.api.io.IOException;
import un.science.geometry.Extent;
import un.science.math.Maths;
import un.api.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class WLoading extends WCenteredPane{
    
    private WMediaPreview animated;

    
    public WLoading() {
        super(null);
    }

    public synchronized void updateLoader(){
        
        //change loading image
        final Path folder = Paths.resolve(GameInfo.PATH_RESOURCE+"/loaders");
        final Node[] nodes = folder.getChildren();
        final int index = Maths.clip((int)(Math.random()*nodes.length),0,nodes.length);

        if(animated!=null){
            removeChild(animated);
        }

        try {
            this.animated = new WMediaPreview((Path)nodes[index]);
            animated.setBestExtent(new Extent(120, 120));
            setCenter(animated);
        } catch (IOException ex) {
            Game.LOGGER.warning(ex);
        }
    }
       
}
