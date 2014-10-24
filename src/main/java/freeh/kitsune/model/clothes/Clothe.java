

package freeh.kitsune.model.clothes;

import freeh.kitsune.MetaObject;
import freeh.kitsune.model.Model;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;

/**
 *
 */
public abstract class Clothe extends MetaObject{
    
    private final Sequence states = new ArraySequence();
    private final Sequence textureSets = new ArraySequence();

    protected ClotheState state;
    private TextureSet textureSet;
    private Model model;
    
    public Clothe() {
    }

    public abstract Chars getName();
    
    public ClotheState getState() {
        return state;
    }

    public void setState(ClotheState state) {
        if(this.state==state) return;
        
        //unload previous state
        if(this.state!=null && model != null){
            this.state.uninstall(model);
        }
        this.state = state;
        if(this.state!=null && model != null){
            this.state.install(model);
        }
    }

    public TextureSet getTextureSet() {
        return textureSet;
    }

    public void setTextureSet(TextureSet textureSet) {
        this.textureSet = textureSet;
    }

    public Sequence getStates() {
        return states;
    }

    public Sequence getTextureSets() {
        return textureSets;
    }

    public void install(Model model){
        this.model = model;
        if(state!=null){
            state.install(model);
        }
    }
    
    public void uninstall(Model model){
        if(state!=null){
            state.uninstall(model);
        }
        this.model = null;
    }
    
}
