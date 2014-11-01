

package freeh.kitsune;

import un.api.character.CharEncodings;
import un.api.character.Chars;
import un.api.event.AbstractEventSource;
import un.api.event.PropertyEvent;
import un.api.image.Image;
import un.api.image.Images;
import un.api.tree.DefaultNamedNode;
import un.api.tree.NamedNode;
import un.api.tree.Nodes;
import un.api.io.ByteOutputStream;
import un.api.io.IOException;
import un.storage.binding.json.JSONUtilities;
import un.api.path.Path;

/**
 * Autosaving object informations in json.
 * Stores common credits and preview informations.
 * 
 */
public class MetaObject extends AbstractEventSource {
    
    private final Chars KEY_TITLE = new Chars("title");
    private final Chars KEY_PREVIEW = new Chars("preview");
    private final Chars KEY_CREDITS = new Chars("credits");
    
    private NamedNode meta;
    private final Path path;

    public MetaObject() {
        this(new DefaultNamedNode(true));
    }
    
    public MetaObject(NamedNode meta) {
        this.meta = meta;
        this.path = null;
    }
    
    public MetaObject(NamedNode meta, Path path) {
        this.meta = meta;
        this.path = path;
    }
    
    public MetaObject(Path metaPath) {
        path = metaPath;
    }

    public Path getMetaPath() {
        return path;
    }

    public synchronized NamedNode getMeta() {
        if(meta==null){
            meta = new DefaultNamedNode(true);
            if(path!=null){
                try{
                    if(path.createLeaf()){
                        //file does not exist, create empty meta
                        final ByteOutputStream bs = path.createOutputStream();
                        bs.write(new Chars("{}").toBytes(CharEncodings.UTF_8));
                        bs.close();
                    }
                    meta = JSONUtilities.readAsTree(path);
                }catch(IOException ex){
                    Game.LOGGER.warning(new Chars("Meta json : fail to load ").concat(path.toChars()),ex);
                }
            }
        }
        return meta;
    }
    
    /**
     * @return object name if defined, empty chars otherwise
     */
    public Chars getTitle(){
        return getPathValueChars(KEY_TITLE, Chars.EMPTY);
    }
    
    /**
     * 
     * @return object preview image, null if not set.
     */
    public Image getPreview(){
        Image img = getPathValueImage(KEY_PREVIEW);
        if(img == null){
            img = GameInfo.MISSING;
        }
        return img;
    }

    public MetaCredits getCredits() {
        final NamedNode creditsNode = Nodes.getPathNode(getMeta(), KEY_CREDITS, true);
        return new MetaCredits(creditsNode, path);
    }
    
    protected Boolean getPathValueBoolean(Chars path, Boolean defaultValue){
        return getPathValueBoolean(getMeta(), path, defaultValue);
    }
    
    protected Number getPathValueNumber(Chars path, Number defaultValue){
        return getPathValueNumber(getMeta(), path, defaultValue);
    }
    
    protected Chars getPathValueChars(Chars path, Chars defaultValue){
        return getPathValueChars(getMeta(), path, defaultValue);
    }
    
    protected Path getPathValuePath(Chars path){
        return getPathValuePath(getMeta(), path, this.path.getParent());
    }
    
    protected Image getPathValueImage(Chars path){
        return getPathValueImage(getMeta(), path, this.path.getParent());
    }

    @Override
    public Class[] getEventClasses() {
        return new Class[]{PropertyEvent.class};
    }

    public static Boolean getPathValueBoolean(NamedNode root, Chars path, Boolean defaultValue){
        final NamedNode node = Nodes.getPathNode(root, path, true);
        Object val = node.getValue();
        if(val!=null && !(val instanceof Boolean)){
            Game.LOGGER.warning(new Chars("Node value is not a boolean : "+val));
            return defaultValue;
        }
        
        if(val==null){
            node.setValue(defaultValue);
            val = defaultValue;
        }
        return (Boolean) val;
    }
    
    public static Number getPathValueNumber(NamedNode root, Chars path, Number defaultValue){
        final NamedNode node = Nodes.getPathNode(root, path, true);
        Object val = node.getValue();
        if(val!=null && !(val instanceof Number)){
            Game.LOGGER.warning(new Chars("Node value is not a Number : "+val));
            return defaultValue;
        }
        
        if(val==null){
            node.setValue(defaultValue);
            val = defaultValue;
        }
        return (Number) val;
    }
    
    public static Chars getPathValueChars(NamedNode root, Chars path, Chars defaultValue){
        final NamedNode node = Nodes.getPathNode(root, path, true);
        Object val = node.getValue();
        if(val!=null && !(val instanceof Chars)){
            Game.LOGGER.warning(new Chars("Node value is not a Chars : "+val));
            return defaultValue;
        }
        
        if(val==null){
            node.setValue(defaultValue);
            val = defaultValue;
        }
        return (Chars) val;
    }
    
    public static Path getPathValuePath(NamedNode root, Chars path, Path folderPath){
        final Chars pathText = getPathValueChars(root,path, Chars.EMPTY);
        if(!pathText.isEmpty()){
            return folderPath.resolve(pathText.toString());
        }
        return null;
    }
    
    public static Image getPathValueImage(NamedNode root, Chars path, Path folderPath){
        final Chars imgPathText = getPathValueChars(root,path, Chars.EMPTY);
        if(!imgPathText.isEmpty()){
            final Path imgpath = folderPath.resolve(imgPathText.toString());
            try{
                return Images.read(imgpath);
            }catch(IOException ex){
                Game.LOGGER.warning(new Chars("Loading image : fail to load ").concat(imgpath.toChars()),ex);
            }
        }
        return null;
    }
    
}
