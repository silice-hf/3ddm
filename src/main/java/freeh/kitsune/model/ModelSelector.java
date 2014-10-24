

package freeh.kitsune.model;

import freeh.kitsune.model.custom.CustomModel;
import freeh.kitsune.model.custom.CustomModelSelector;
import freeh.kitsune.model.preset.PresetModelSelector;
import freeh.kitsune.model.preset.PresetModel;
import freeh.kitsune.GameBundle;
import freeh.kitsune.GameProperties;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyBinding;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.GridLayout;
import un.engine.ui.model.CheckGroup;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WSwitch;
import un.engine.ui.widget.WTable;
import un.engine.ui.widget.Widget;

/**
 *
 */
public class ModelSelector extends WContainer{

    public static final Chars PROPERTY_MODEL = new Chars("Model");
        
    private final WContainer center = new WContainer(new BorderLayout());
    private final WContainer right = new WContainer(new BorderLayout());
        
    private final WSwitch presetButton = new WSwitch(GameBundle.get(new Chars("model.preset")), null, new EventListener() {
        public void receiveEvent(Event event) {
            center.removeChild(presetPane);
            center.removeChild(customPane);
            center.addChild(presetPane, BorderConstraint.CENTER);
        }
    });
    private final WSwitch customButton = new WSwitch(GameBundle.get(new Chars("model.custom")), null, new EventListener() {
        public void receiveEvent(Event event) {
            center.removeChild(presetPane);
            center.removeChild(customPane);
            center.addChild(customPane, BorderConstraint.CENTER);
        }
    });    
    private final WSwitch rotateButton = new WSwitch(null, GameProperties.ICON_ROTATE, new EventListener() {
        public void receiveEvent(Event event) {
            
        }
    });
    
    private final WTable posesTable = new WTable();
    private final PresetModelSelector presetPane = new PresetModelSelector();
    private final CustomModelSelector customPane = new CustomModelSelector();
    
    private Model model = null;
    
    public ModelSelector() {
        super(new BorderLayout());
        addChild(center, BorderConstraint.CENTER);
        addChild(right, BorderConstraint.RIGHT);
                
        PropertyBinding.bidirectional(this, PROPERTY_MODEL, presetPane, ModelSelector.PROPERTY_MODEL);
        PropertyBinding.bidirectional(this, PROPERTY_MODEL, customPane, ModelSelector.PROPERTY_MODEL);
        
        posesTable.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        
        right.addChild(posesTable, BorderConstraint.CENTER);
        right.addChild(rotateButton, BorderConstraint.BOTTOM);
        right.getStyle().getSelfRule().setProperties(new Chars(
                "margin     : [5,5,5,5]\n"+
                "background : none"));
        
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        
        customButton.setId(new Chars("state"));
        presetButton.setId(new Chars("state"));
        rotateButton.setId(new Chars("state"));
        
        final WContainer topPane = new WContainer(new GridLayout(1,2,0,0));
        topPane.addChild(customButton);
        topPane.addChild(presetButton);
        topPane.getStyle().getSelfRule().setProperties(new Chars(
                "margin     : [5,5,5,5]\n"+
                "background : none"));
        
        final CheckGroup group = new CheckGroup();
        group.add(customButton);
        group.add(presetButton);
        
        center.addChild(topPane,BorderConstraint.TOP);
        center.getStyle().getSelfRule().setProperties(new Chars(
                "margin     : [5,5,5,5]\n"+
                "background : none"));
        
        
        presetButton.setCheck(true);
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(final Model model){
        if(this.model == model) return;
        
        final Model old = this.model;
        this.model = model;
        
        if(model instanceof CustomModel){
            customButton.setCheck(true);
            customPane.setModel((CustomModel) model);
        }else{
            presetButton.setCheck(true);
            presetPane.setModel((PresetModel) model);
        }
        
        getEventManager().sendPropertyEvent(this, PROPERTY_MODEL, old, model);
    }
    
    
    
}
