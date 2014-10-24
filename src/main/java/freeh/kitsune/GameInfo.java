

package freeh.kitsune;

import un.api.image.Image;
import un.api.image.Images;
import un.science.encoding.IOException;
import un.system.path.Path;
import un.system.path.Paths;

/**
 *
 */
public final class GameInfo {

    public static final String NAME = "Kitsune";
    public static final String PATH_RESOURCE = "file>./resources";
    public static final String PATH_MUSICS = PATH_RESOURCE+"/game/musics";
    public static final String PATH_MAPS = PATH_RESOURCE+"/game/maps";
    public static final String PATH_TOYS = PATH_RESOURCE+"/game/toys";
    public static final String PATH_ITEMS = PATH_RESOURCE+"/game/items";
    public static final String PATH_ICONS = PATH_RESOURCE+"/game/icons";
    public static final String PATH_TOOLS = PATH_RESOURCE+"/game/tools";
    public static final String PATH_IMAGES = PATH_RESOURCE+"/game/images";
    public static final String PATH_DANCES = PATH_RESOURCE+"/game/dances";
    public static final String PATH_POSES = PATH_RESOURCE+"/game/poses";
    public static final String PATH_CLOTHES = PATH_RESOURCE+"/game/models/clothes";
    public static final String PATH_PRESETS = PATH_RESOURCE+"/game/models/presets";
    
    public static final Image SMALL_CROSSHAIR;
    public static final Image BIG_CROSSHAIR;
    public static final Image MISSING;
    static {
        try {
            SMALL_CROSSHAIR = Images.read(Paths.resolve(PATH_RESOURCE+"/crosshair.gif"));
            BIG_CROSSHAIR = Images.read(Paths.resolve(PATH_RESOURCE+"/crosshair2.gif"));
            MISSING = Images.read(Paths.resolve(PATH_ICONS+"/missing.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private GameInfo() {}
 
    public static Path replaceSuffix(Path path, String suffix){
        String name = path.getName();
        final int index = name.indexOf('.');
        if(index>=0){
            name = name.substring(0,index) +"."+suffix;
        }else{
            name = name +"."+suffix;
        }
        return path.getParent().resolve(name);
    }
    
}
