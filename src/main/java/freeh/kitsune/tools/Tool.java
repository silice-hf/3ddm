package freeh.kitsune.tools;

import freeh.kitsune.MetaObject;
import freeh.kitsune.model.PresetModel;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.Models;
import un.api.character.Chars;
import un.api.collection.Dictionary;
import un.api.collection.Sequence;
import un.api.image.Image;
import un.science.encoding.IOException;
import un.system.path.Path;

/**
 *
 */
public class Tool extends MetaObject {

    private static final Chars PATH_NAME = new Chars("name");
    private static final Chars PATH_CONTROL = new Chars("control");
    private static final Chars PATH_SOUND = new Chars("sound");

    private final Path modelPath;
    private ToolControl control = null;

    public Tool(Path path) throws IOException {
        super(path.resolve("meta.json"));
        final Sequence models = Models.searchModels(path);
        if (models.isEmpty()) {
            throw new IOException("No model in given path");
        }
        modelPath = (Path) models.get(0);
    }

    public Chars getName() {
        return getPathValueChars(PATH_NAME, Chars.EMPTY);
    }
    
    public Path getSound() {
        final Chars soundStr = getPathValueChars(PATH_SOUND, Chars.EMPTY);
        if(!soundStr.isEmpty()){
            return getMetaPath().getParent().resolve(soundStr.toString());
        }
        return null;
    }

    public Model createModel() {
        return new PresetModel(modelPath);
    }

    public Image getIcon() {
        return null;
    }

    /**
     *
     * @return Dictionnary : Item > number(int)
     */
    public Dictionary getRequieredItems() {
        return null;
    }

    public synchronized ToolControl getControl() {
        if (control == null) {
            try {
                control = (ToolControl) Class.forName(
                        getPathValueChars(PATH_CONTROL, null).toString()
                ).newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return control;
    }

}
