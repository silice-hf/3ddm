

package freeh.kitsune.items;

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
public class Items {
    
    private static Item[] ITEMS = null;
    
    private Items(){}
    
    public static synchronized Item[] getItems(){
        if(ITEMS==null){
            final Path path = Paths.resolve(GameInfo.PATH_ITEMS);
            final Sequence seq = new ArraySequence();
            for(Node n : path.getChildren()){
                final Path p = (Path) n;
                try{
                    if(p.isContainer()){
                        seq.add(new Item(p));
                    }
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            ITEMS = new Item[seq.getSize()];
            Collections.copy(seq, ITEMS, 0);
        }
        return ITEMS;
    }
    
    public static Item getRandomItem(){
        final Item[] candidates = getItems();
        if(candidates.length==0) return null;
        final int modelIndex = (int) (Math.random() * (candidates.length- 1));
        return candidates[modelIndex];
    }
    
}
