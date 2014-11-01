

package freeh.kitsune.tools;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import un.api.collection.ArraySequence;
import un.api.collection.Collections;
import un.api.collection.Sequence;
import un.api.tree.Node;
import un.api.io.IOException;
import un.api.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class Tools {
    
    private static Tool[] TOOLS = null;
    
    private Tools(){}
    
    public static synchronized Tool[] getTools(){
        if(TOOLS==null){
            final Path path = Paths.resolve(GameInfo.PATH_ITEMS);
            final Sequence seq = new ArraySequence();
            for(Node n : path.getChildren()){
                final Path p = (Path) n;
                try{
                    if(p.isContainer()){
                        seq.add(new Tool(p));
                    }
                }catch(IOException ex){
                    Game.LOGGER.warning(ex);
                }
            }
            TOOLS = new Tool[seq.getSize()];
            Collections.copy(seq, TOOLS, 0);
        }
        return TOOLS;
    }
    
    public static Tool getRandomTool(){
        final Tool[] candidates = getTools();
        if(candidates.length==0) return null;
        final int modelIndex = (int) (Math.random() * (candidates.length- 1));
        return candidates[modelIndex];
    }
    
}
