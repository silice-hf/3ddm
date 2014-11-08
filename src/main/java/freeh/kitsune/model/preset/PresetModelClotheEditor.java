

package freeh.kitsune.model.preset;

import freeh.kitsune.model.Model;
import freeh.kitsune.model.ModelSelector;
import un.api.character.Chars;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.tree.Node;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.ui.model.ColumnModel;
import un.engine.ui.model.DefaultColumnModel;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.ObjectPresenter;
import un.engine.ui.model.RowModel;
import un.engine.ui.model.TableModel;
import un.engine.ui.model.TreeRowPath;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.WSwitch;
import un.engine.ui.widget.WTable;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;

/**
 *
 */
public class PresetModelClotheEditor extends WContainer{
    
    public static final Chars PROPERTY_MODEL = ModelSelector.PROPERTY_MODEL;
    
    private final WTable table = new WTable();
    private Model model;

    public PresetModelClotheEditor() {
        super(new BorderLayout());
        
        table.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        table.setRowHeight(28);
        addChild(table, BorderConstraint.CENTER);
        
        getStyle().getSelfRule().setProperties(new Chars(
                "background          : none\n" +
                "margin              : [6,6,6,6]\n" +
                "border-margin       : [5,5,5,5]\n" +
                "border-radius       : [8,10,8,8]\n" +
                "border-brush        : plainbrush(1,'round')\n" +
                "border-brush-paint  : colorfill($back-aFF)\n" +
                "border-fill-paint   : lineargradientfill('%',0,0,1,1,0,$back-a33,0.3,$back-a88,1,$back-a33)\n"));
        
    }
    
    public void setModel(final Model model){
        if(this.model==model) return;
        final Model old = this.model;
        
        this.model = model;
        
        if(model==null){
            table.setModel(new TableModel(new DefaultRowModel(),new ColumnModel[]{}));
        }else{
            final Node[] nodes = model.getNode().getChildren();
            final Sequence possibleClothes = new ArraySequence(nodes);
            final RowModel rowModel = new DefaultRowModel(possibleClothes);
            final ColumnModel isClotheCol = new IsClotheColumn();
            final TableModel tableModel = new TableModel(rowModel, new ColumnModel[]{
                new NameColumn(),isClotheCol});
            table.setModel(tableModel);
        }
        
        getEventManager().sendPropertyEvent(this, PROPERTY_MODEL, old, model);
    }

    public Model getModel() {
        return model;
    }
    
    public static class NameColumn extends DefaultColumnModel{

        public NameColumn() {
            super(Chars.EMPTY,new NamePresenter());
        }

        private static class NamePresenter implements ObjectPresenter{

            public Widget createWidget(Object candidate) {
                if(candidate instanceof TreeRowPath) candidate = ((TreeRowPath)candidate).getLeaf();
                if(candidate instanceof GLNode){
                    final WLabel lbl = new WLabel();
                    lbl.setText(((GLNode)candidate).getName() );
                    return lbl;
                }else{
                    return new WSpace(new Extent(2));
                }
            }
        }
    }
    
    private class IsClotheColumn extends DefaultColumnModel{

        public IsClotheColumn() {
            super(Chars.EMPTY,new IsClothePresenter());
        }

    }
    
    private class IsClothePresenter implements ObjectPresenter{

        public Widget createWidget(Object candidate) {
            if(candidate instanceof TreeRowPath){
                candidate = ((TreeRowPath)candidate).getLeaf();
            }
            if(candidate instanceof GLNode){
                final GLNode node = (GLNode) candidate;
                
                final WSwitch state = new WSwitch(new Chars("clothe"));
                state.setId(new Chars("state"));
                state.setCheck(PresetModelClothe.isClothe(node));
                state.addEventListener(PropertyEvent.class, new EventListener() {
                    public void receiveEvent(Event event) {
                        final PropertyEvent pe = (PropertyEvent) event;
                        if(WSwitch.PROPERTY_CHECK.equals(pe.getPropertyName())){
                            PresetModelClothe.setClothe(node, !PresetModelClothe.isClothe(node));
                            state.setCheck(PresetModelClothe.isClothe(node));
                            ((PresetModel)model).saveClotheMap();
                            ((PresetModel)model).updateClothes();
                        }
                    }
                });
                
                return state;
            }else{
                return new WSpace(new Extent(2));
            }
        }

    }
    
    
}
