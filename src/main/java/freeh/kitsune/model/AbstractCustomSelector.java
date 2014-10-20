
package freeh.kitsune.model;

import static freeh.kitsune.model.PresetModelSelector.PROPERTY_MODEL;
import un.engine.ui.widget.WContainer;

/**
 *
 */
public class AbstractCustomSelector extends WContainer{
        
    protected CustomModel model;
    
    public AbstractCustomSelector(){
    }
    
    public AbstractCustomSelector(CustomModel model){
        setModel(model);
    }

    public CustomModel getModel() {
        return model;
    }

    public void setModel(Model model) {
        if(this.model == model || !(model instanceof CustomModel)) return;
        final Model old = this.model;
        this.model = (CustomModel) model;
        getEventManager().sendPropertyEvent(this, PROPERTY_MODEL, old, model);
    }
    
}
