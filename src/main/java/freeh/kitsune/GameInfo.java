

package freeh.kitsune;

import un.api.image.Image;
import un.api.image.Images;
import un.science.encoding.IOException;
import un.system.path.Paths;

/**
 *
 */
public final class GameInfo {

    public static final String NAME = "Kitsune";
    public static final String PATH_MUSICS = "file>./resources/game/musics";
    public static final String PATH_MAPS = "file>./resources/game/maps";
    public static final String PATH_TOYS = "file>./resources/game/toys";
    public static final String PATH_ITEMS = "file>./resources/game/items";
    public static final String PATH_TOOLS = "file>./resources/game/tools";
    public static final String PATH_IMAGES = "file>./resources/game/images";
    public static final String PATH_DANCES = "file>./resources/game/dances";
    public static final String PATH_POSES = "file>./resources/game/poses";
    public static final String PATH_CLOTHES = "file>./resources/game/models/clothes";
    public static final String PATH_PRESETS = "file>./resources/game/models/presets";
    
    public static final Image SMALL_CROSSHAIR;
    public static final Image BIG_CROSSHAIR;
    static {
        try {
            SMALL_CROSSHAIR = Images.read(Paths.resolve("file>./resources/crosshair.gif"));
            BIG_CROSSHAIR = Images.read(Paths.resolve("file>./resources/crosshair2.gif"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private GameInfo() {}
    
}
