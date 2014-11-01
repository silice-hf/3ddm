

package freeh.kitsune.model.preset;

import freeh.kitsune.Game;
import freeh.kitsune.GameInfo;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.Models;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.io.IOException;
import un.api.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class PresetModels {
    
    private static Sequence PRESETS = null;
    
    public static synchronized Sequence getAll(){
        if(PRESETS==null){
            final Path path = Paths.resolve(GameInfo.PATH_PRESETS);
            PRESETS = new ArraySequence();
            try {
                Models.search(path, PRESETS, null, null, null, true);
            } catch (IOException ex) {
                Game.LOGGER.warning(ex);
            }
        }
        return PRESETS;
    }
    
    public static Model getRandomPreset(){
        final Sequence candidates = getAll();
        if(candidates.isEmpty()) return null;
        final int index = (int) (Math.random() * (candidates.getSize()- 1));
        return (Model)candidates.get(index);
    }
        
}
