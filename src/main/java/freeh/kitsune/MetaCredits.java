

package freeh.kitsune;

import un.api.character.Chars;
import un.api.image.Image;
import un.api.tree.NamedNode;
import un.api.tree.Node;
import un.system.path.Path;

/**
 * Model author credits informations.
 */
public class MetaCredits extends MetaObject {
    
    private final Chars KEY_NAME = new Chars("name");
    private final Chars KEY_AVATAR = new Chars("avatar");
    private final Chars KEY_README = new Chars("readme");
    private final Chars KEY_URL = new Chars("url");
    
    public MetaCredits(NamedNode node, Path path) {
        super(node,path);
    }
    
    /**
     * @return author name if defined, empty chars otherwise
     */
    public Chars getName(){
        return getPathValueChars(KEY_NAME, Chars.EMPTY);
    }
    
    /**
     * @return readme info, license, usage conditions ... if defined, null otherwise
     */
    public Path getReadme(){
        Path path = getPathValuePath(KEY_README);
        if(path==null){
            //search for a file named readme or rules
            final Path parent = getMetaPath().getParent();
            for(Node n : parent.getChildren()){
                final Path p = (Path) n;
                final String name = p.getName().toLowerCase();
                if(name.contains("readme") || name.contains("read me") || name.contains("read-me") 
                   || name.contains("license") || name.contains("rules")){
                    return p;
                }
            }
        }
        return path;
    }
    
    /**
     * @return url info, can be author blog, download...
     */
    public Chars getUrl(){
        return getPathValueChars(KEY_URL, Chars.EMPTY);
    }
    
    /**
     * 
     * @return author avatar, null if not set
     */
    public Image getAvatar(){
        return getPathValueImage(KEY_AVATAR);
    }
    
}
