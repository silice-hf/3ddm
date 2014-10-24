

package freeh.kitsune;

import un.api.character.Chars;
import un.api.image.Image;
import un.api.image.Images;
import un.api.logging.Logger;
import un.science.encoding.IOException;
import un.storage.keyvalue.KeyValueStore;
import un.system.path.Path;
import un.system.path.Paths;

/**
 * Store game properties.
 * 
 */
public class GameProperties {
    
    public static final Image ICON_CLOTHE_OFF;
    public static final Image ICON_CLOTHE_ON;
    public static final Image ICON_CONFIG;
    public static final Image ICON_CUM;
    public static final Image ICON_FAVORITE;
    public static final Image ICON_LOCK;
    public static final Image ICON_MODEL;
    public static final Image ICON_MOTION;
    public static final Image ICON_MOVE;
    public static final Image ICON_MUSIC;
    public static final Image ICON_ORBIT;
    public static final Image ICON_PLACE;
    public static final Image ICON_POSE;
    public static final Image ICON_ROTATE;
    public static final Image ICON_STAGE;
    public static final Image ICON_TOY;
    public static final Image ICON_WEAPON;
    static {
        try {
            ICON_CLOTHE_OFF = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/clothe_off.png"));
            ICON_CLOTHE_ON = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/clothe_on.png"));
            ICON_CONFIG = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/config.png"));
            ICON_CUM = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/cum.png"));
            ICON_FAVORITE = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/favorite.png"));
            ICON_LOCK = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/lock.png"));
            ICON_MODEL = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/model.png"));
            ICON_MOTION = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/motion.png"));
            ICON_MOVE = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/move.png"));
            ICON_MUSIC = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/music.png"));
            ICON_ORBIT = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/orbit.png"));
            ICON_PLACE = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/place.png"));
            ICON_POSE = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/pose.png"));
            ICON_ROTATE = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/rotate.png"));
            ICON_STAGE = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/stage.png"));
            ICON_TOY = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/toy.png"));
            ICON_WEAPON = Images.read(Paths.resolve(GameInfo.PATH_RESOURCE+"/game/icons/weapon.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static final GameProperties INSTANCE = new GameProperties();
    
    private final KeyValueStore configProperties;

    private GameProperties() {
        try{
            Path path = Paths.resolve(GameInfo.PATH_RESOURCE+"/config.properties");
            path.createLeaf();
            configProperties = new KeyValueStore(path).load();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public KeyValueStore getProperties() {
        return configProperties;
    }
    
    public Chars getPropertyChars(Chars name, Chars defValue){
        Chars obj = configProperties.getProperty(name);
        if(obj!=null){
            return obj;
        }
        return defValue;
    }
    
    public boolean getPropertyBoolean(Chars name, boolean defValue){
        Chars obj = configProperties.getProperty(name);
        if(obj!=null){
            return !"false".equalsIgnoreCase(obj.trim().toString());
        }
        return defValue;
    }
    
    public int getPropertyInt(Chars name, int defValue){
        Chars obj = configProperties.getProperty(name);
        if(obj!=null){
            return Integer.parseInt(obj.trim().toString());
        }
        return defValue;
    }
    
    public float getPropertyFloat(Chars name, float defValue){
        Chars obj = configProperties.getProperty(name);
        if(obj!=null){
            return Float.parseFloat(obj.trim().toString());
        }
        return defValue;
    }
    
    public double getPropertyDouble(Chars name, double defValue){
        Chars obj = configProperties.getProperty(name);
        if(obj!=null){
            return Double.parseDouble(obj.trim().toString());
        }
        return defValue;
    }
    
    public void setProperty(Chars name, Object value){
        configProperties.setProperty(name, new Chars(value.toString()));
        try {
            configProperties.save();
        } catch (IOException ex) {
            Game.LOGGER.log(ex, Logger.LEVEL_WARNING);
        }
    }
        
}
