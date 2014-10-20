

package freeh.kitsune.model.clothes;

import freeh.kitsune.model.Model;
import un.api.character.Chars;
import un.api.image.Image;

/**
 * clothe state, exemple : base, open, torn apart, disable, ...
 */
public abstract class ClotheState {
    
    private final Chars name;
    private final Image icon;

    public ClotheState(Chars name, Image icon) {
        this.name = name;
        this.icon = icon;
    }

    public Chars getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
    }
    
    public void update(TextureSet textures){
        // todo
    }
    
    public abstract void install(Model model);
    
    public abstract void uninstall(Model model);
    
}
