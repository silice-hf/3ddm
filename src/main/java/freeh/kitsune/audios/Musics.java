

package freeh.kitsune.audios;

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
public class Musics {
    
    private static Sequence ALL = null;
    
    private Musics(){}
    
    public static synchronized Sequence getAll(){
        if(ALL==null){
            final Path path = Paths.resolve(GameInfo.PATH_MUSICS);
            ALL = new ArraySequence();
            try {
                Models.search(path, null, null, null, ALL, true);
            } catch (IOException ex) {
                Game.LOGGER.warning(ex);
            }
        }
        return ALL;
    }
    
    public static Music getRandom(){
        final Sequence candidates = getAll();
        if(candidates.isEmpty()) return null;
        final int index = (int) (Math.random() * (candidates.getSize()- 1));
        return (Music) candidates.get(index);
    }
}
