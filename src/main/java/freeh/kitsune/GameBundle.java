

package freeh.kitsune;

import un.api.character.Chars;
import un.api.character.LChars;
import un.science.encoding.IOException;
import un.storage.keyvalue.TranslationStore;
import un.system.path.Paths;

/**
 *
 */
public class GameBundle {
    
    private static final TranslationStore bundle;
    static {
        bundle = new TranslationStore(Paths.resolve(GameInfo.PATH_RESOURCE+"/bundle"));
        try {
            bundle.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static TranslationStore getTextBundle() {
        return bundle;
    }
    
    public static LChars get(Chars key){
        return bundle.getChars(key);
    }
    
}
