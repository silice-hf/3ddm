

package freeh.kitsune;

import un.api.character.CharEncodings;
import un.api.character.Chars;
import un.api.event.AbstractEventSource;
import un.api.event.PropertyEvent;
import un.api.tree.DefaultNamedNode;
import un.api.tree.NamedNode;
import un.api.tree.Nodes;
import un.science.encoding.ByteOutputStream;
import un.science.encoding.IOException;
import un.storage.binding.json.JSONUtilities;
import un.system.path.Path;

/**
 * Autosaving object in json.
 * 
 */
public class MetaObject extends AbstractEventSource {
    
    protected final NamedNode meta;

    public MetaObject() {
        this(new DefaultNamedNode(true));
    }
    
    public MetaObject(NamedNode meta) {
        this.meta = meta;
    }
    
    public MetaObject(Path metaPath) throws IOException {
        if(metaPath.createLeaf()){
            //file does not exist, create empty meta
            final ByteOutputStream bs = metaPath.createOutputStream();
            bs.write(new Chars("{}").toBytes(CharEncodings.UTF_8));
            bs.close();
        }
        meta = JSONUtilities.readAsTree(metaPath);
    }
    
    protected Boolean getPathValueBoolean(Chars path, Boolean defaultValue){
        final NamedNode node = Nodes.getPathNode(meta, path, true);
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
        final NamedNode node = Nodes.getPathNode(meta, path, true);
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
        final NamedNode node = Nodes.getPathNode(meta, path, true);
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
