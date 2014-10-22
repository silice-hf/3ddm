

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
import un.science.encoding.ByteOutputStream;
import un.science.encoding.IOException;
import un.storage.binding.json.JSONUtilities;
import un.system.path.Path;

/**
 * Autosaving object informations in json.
 * Stores common credits and preview informations.
 * 
 */
public class MetaObject extends AbstractEventSource {
    
    private final Chars KEY_PREVIEW = new Chars("preview");
    private final Chars KEY_AUTHOR = new Chars("author");
    private final Chars KEY_README = new Chars("readme");
    
    private NamedNode meta;
    private final Path path;

    public MetaObject() {
        this(new DefaultNamedNode(true));
    }
    
    public MetaObject(NamedNode meta) {
        this.meta = meta;
        this.path = null;
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
     * 
     * @return preview image, null if not set.
     */
    public Image getPreview(){
        final Chars imgPathText = getPathValueChars(KEY_PREVIEW, Chars.EMPTY);
        if(!imgPathText.isEmpty()){
            Path imgpath = path.getParent().resolve(imgPathText.toString());
            try{
                return Images.read(imgpath);
            }catch(IOException ex){
                Game.LOGGER.warning(new Chars("Preview image : fail to load ").concat(imgpath.toChars()),ex);
            }
        }
        return null;
    }
    
    /**
     * @return author name if defined, empty chars otherwise
     */
    public Chars getAuthor(){
        return getPathValueChars(KEY_AUTHOR, Chars.EMPTY);
    }
    
    /**
     * @return readme info, license, usage conditions ... if defined, empty chars otherwise
     */
    public Chars getReadme(){
        return getPathValueChars(KEY_README, Chars.EMPTY);
    }
    
    protected Boolean getPathValueBoolean(Chars path, Boolean defaultValue){
        final NamedNode node = Nodes.getPathNode(getMeta(), path, true);
        Object val = node.getValue();
        if(val!=null && !(val instanceof Boolean)){
            throw new RuntimeException("Node value is not a boolean : "+val);
        }
        
        if(val==null){
            node.setValue(defaultValue);
            val = defaultValue;
        }
        return (Boolean) val;
    }
    
    protected Number getPathValueNumber(Chars path, Number defaultValue){
        final NamedNode node = Nodes.getPathNode(getMeta(), path, true);
        Object val = node.getValue();
        if(val!=null && !(val instanceof Number)){
            throw new RuntimeException("Node value is not a Number : "+val);
        }
        
        if(val==null){
            node.setValue(defaultValue);
            val = defaultValue;
        }
        return (Number) val;
    }
    
    protected Chars getPathValueChars(Chars path, Chars defaultValue){
        final NamedNode node = Nodes.getPathNode(getMeta(), path, true);
        Object val = node.getValue();
        if(val!=null && !(val instanceof Chars)){
            throw new RuntimeException("Node value is not a Chars : "+val);
        }
        
        if(val==null){
            node.setValue(defaultValue);
            val = defaultValue;
        }
        return (Chars) val;
    }

    @Override
    public Class[] getEventClasses() {
        return new Class[]{PropertyEvent.class};
    }

    
}
