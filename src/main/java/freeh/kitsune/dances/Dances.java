

package freeh.kitsune.dances;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import freeh.kitsune.model.Models;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.io.IOException;
import un.api.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class Dances {
    
    private static Sequence ALL = null;
    
    private Dances(){}
    
    public static synchronized Sequence getAll(){
        if(ALL==null){
            final Path path = Paths.resolve(GameInfo.PATH_DANCES);
            ALL = new ArraySequence();
            try {
                Models.search(path, null, ALL, null, null, true);
            } catch (IOException ex) {
                Game.LOGGER.warning(ex);
            }
        }
        return ALL;
    }
    
    public static Dance getRandom(){
        final Sequence candidates = getAll();
        if(candidates.isEmpty()) return null;
        final int index = (int) (Math.random() * (candidates.getSize()- 1));
        return (Dance)candidates.get(index);
    }
}
