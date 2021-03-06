

package freeh.kitsune.maps;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.tree.Node;
import un.api.io.IOException;
import un.api.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class Maps {
    
    private static Sequence ALL = null;
    
    private Maps(){}
    
    public static synchronized Sequence getAll(){
        if(ALL==null){
            final Path path = Paths.resolve(GameInfo.PATH_MAPS);
            ALL = new ArraySequence();
            try {
                final Node[] nodes = path.getChildren();
                for(Node n : nodes){
                    if(((Path)n).isContainer()){
                        ALL.add(new Map((Path)n));
                    }
                }
            } catch (IOException ex) {
                Game.LOGGER.warning(ex);
            }
        }
        return ALL;
    }
    
    public static Map getRandom(){
        final Sequence candidates = getAll();
        if(candidates.isEmpty()) return null;
        final int index = (int) (Math.random() * (candidates.getSize()- 1));
        return (Map) candidates.get(index);
    }
}
