

package freeh.kitsune.model.clothes;

import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;

/**
 * just a group of alternative textures for a clothe.
 */
public class TextureSet {
    
    private final Chars name;
    private final Sequence textures = new ArraySequence();

    public TextureSet(Chars name, Sequence textures) {
        this.name = name;
        if(textures!=null) this.textures.addAll(textures);
    }

    public Chars getName() {
        return name;
    }

    public Sequence getTextures() {
        return textures;
    }

}
