

package freeh.kitsune.model.preset;

import freeh.kitsune.Game;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.ModelSelector;
import freeh.kitsune.model.Models;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.api.logging.Logger;
import un.api.predicate.ClassPredicate;
import un.api.predicate.Predicate;
import un.api.tree.Node;
import un.engine.ui.component.path.PathPresenters;
import un.engine.ui.component.path.PathView;
import un.engine.ui.component.path.WPreviewView;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.engine.ui.style.StyleRule;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.Widget;
import un.science.encoding.IOException;
import un.science.geometry.Extent;
import un.system.path.Path;
import un.system.path.VirtualFolder;

/**
 *
 */
public class PresetModelSelector extends WContainer{
    
    public static final Chars PROPERTY_MODEL = ModelSelector.PROPERTY_MODEL;
    
    private final WPreviewView ptree = new WPreviewView();
    private final WButton refreshPreview = new WButton(new Chars("Reset thumbnails"), new EventListener() {

        @Override
        public void receiveEvent(Event event) {
            try {
                PathPresenters.deleteThumbnails();
            } catch (IOException ex) {
                Game.LOGGER.log(ex,Logger.LEVEL_WARNING);
            }
        }
    });

    private Model model;
    
    public PresetModelSelector() {
        getStyle().getSelfRule().setProperties(new Chars(
                "background : none\n"+
                "margin     : [2,2,2,2]\n"));
        
        final StyleRule rule = new StyleRule();
        rule.setFilter(new ClassPredicate(WContainer.class));
        rule.setProperties(new Chars("background : none\nenv-background : none"));
        ptree.getStyle().getRules().add(rule);
        //setTrans(refreshPreview);
        
        setBestExtent(new Extent(440, 400));
        
        ptree.setBlockSize(new Extent(128,128));
        ptree.setCacheThumbs(true);
        setLayout(new FormLayout());
        ((FormLayout)getLayout()).setRowSize(0, FormLayout.SIZE_EXPAND);
        ((FormLayout)getLayout()).setColumnSize(1, FormLayout.SIZE_EXPAND);
        addChild(ptree, new FormConstraint(0,0,2,1));
        addChild(refreshPreview, new FormConstraint(0,1,1,1));
        
        ptree.addEventListener(PropertyEvent.class, new EventListener() {
            public void receiveEvent(Event event) {
                if( ((PropertyEvent)event).getPropertyName().equals(PathView.PROPERTY_SELECTED_PATHS) ){
                    Path[] p = ptree.getSelectedPath();
                    if(p != null && p.length>0){
                        final Sequence seq = new ArraySequence(ptree.getViewRoot().getChildren());
                        final int index = seq.search(p[0]);
                        final Model model = (Model) PresetModels.getAll().get(index);
                        setModel(model);
                    }
                }
            }
        });
        
        update();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        if(this.model == model) return;
        final Model old = this.model;
        this.model = model;
        getEventManager().sendPropertyEvent(this, PROPERTY_MODEL, old, model);
    }

    private void update() {
        
        final Sequence presets = PresetModels.getAll();
        final Sequence paths = new ArraySequence();
        for(int i=0,n=presets.getSize();i<n;i++){
            final PresetModel pm = (PresetModel) presets.get(i);
            paths.add(pm.getPath());
        }

        //update the block view
        final Path p = new VirtualFolder("presets");
        p.addChildren(paths);
        ptree.setViewRoot(p);
        
    }
    
    private static void setTrans(Widget candidate){
        if(candidate instanceof WContainer){
            candidate.getStyle().getSelfRule().setProperties(new Chars(
                "background : none\n"));
        }
        
        for(Node n : candidate.getChildren()){
            if(n instanceof WContainer){
                setTrans((Widget) n);
            }
        }
    }
    
}
