

package freeh.kitsune.items;

import freeh.kitsune.MetaObject;
import freeh.kitsune.model.preset.PresetModel;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.Models;
import un.api.character.Chars;
import un.api.collection.Sequence;
import un.api.io.IOException;
import un.api.path.Path;

/**
 *
 */
public class Item extends MetaObject{
    
    private static final Chars PATH_NAME = new Chars("name");
    
    private final Path modelPath;
    
    public Item(Path path) throws IOException {
        super(path.resolve("meta.json"));
        final Sequence models = Models.searchModels(path);
        if(models.isEmpty()) throw new IOException("No model in given path");
        
        modelPath = (Path) models.get(0);
        
    }
    
    public Chars getName(){
        return getPathValueChars(PATH_NAME, Chars.EMPTY);
    }
        
    public Model createModel(){
        return new PresetModel(modelPath);
    }
    
    public Chars toChars() {
        return getName();
    }
}
