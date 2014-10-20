

package freeh.kitsune.tools;

import freeh.kitsune.GameInfo;
import un.api.collection.ArraySequence;
import un.api.collection.Collections;
import un.api.collection.Sequence;
import un.api.tree.Node;
import un.science.encoding.IOException;
import un.system.path.Path;
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
                    throw new RuntimeException(ex);
                }
            }
            TOOLS = new Tool[seq.getSize()];
            Collections.copy(seq, TOOLS, 0);
        }
        return TOOLS;
    }
    
    public static Tool getRandomTool(){
        final Tool[] items = getTools();
        final int modelIndex = (int) (Math.random() * (items.length- 1));
        return items[modelIndex];
    }
    
}