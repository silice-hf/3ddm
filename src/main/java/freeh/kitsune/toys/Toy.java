

package freeh.kitsune.toys;

import freeh.kitsune.MetaObject;
import freeh.kitsune.model.preset.PresetModel;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.Models;
import un.api.character.Chars;
import un.api.collection.Sequence;
import un.science.encoding.IOException;
import un.system.path.Path;

/**
 *
 */
public class Toy extends MetaObject{

    private static final Chars PATH_NAME = new Chars("name");
    private static final Chars PATH_SIMPLE = new Chars("simple");
    
    private final PresetModel model;
    
    public Toy(Path path) throws IOException {
        super(path.resolve("meta.json"));
        
        final Sequence models = Models.searchModels(path);
        if(models.isEmpty()) throw new IOException("No model in given path");
        
        model = (PresetModel) models.get(0);
        
    }
    
    public Chars getName(){
        return getPathValueChars(PATH_NAME, Chars.EMPTY);
    }
    
    public boolean isSimple(){
        return getPathValueBoolean(PATH_SIMPLE, Boolean.TRUE);
    }
    
    public Model createModel(){
        return model;
    }
    
}
