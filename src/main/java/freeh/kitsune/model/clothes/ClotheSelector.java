

package freeh.kitsune.model.clothes;

import freeh.kitsune.model.Model;
import freeh.kitsune.model.ModelSelector;
import freeh.kitsune.model.PresetModel;
import un.api.character.Chars;
import un.api.collection.Sequence;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.GridLayout;
import un.engine.ui.model.CheckGroup;
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
import un.storage.binding.xml.dom.DomNode;

/**
 *
 */
public class ClotheSelector extends WContainer{
    
    public static final Chars PROPERTY_MODEL = ModelSelector.PROPERTY_MODEL;
    
    private final WTable table = new WTable();
    private Model model;

    public ClotheSelector() {
        super(new BorderLayout());
        
        getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        table.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        table.setRowHeight(34);
        addChild(table, BorderConstraint.CENTER);
        
    }
    
    public void setModel(final PresetModel model){
        if(this.model==model) return;
        final Model old = this.model;
        
        this.model = model;
        
        if(model==null){
            table.setModel(new TableModel(new DefaultRowModel(),new ColumnModel[]{}));
        }else{
            final Sequence clothes = model.getClothes();
            final RowModel rowModel = new DefaultRowModel(clothes);
            final ColumnModel visibleCol = new StateColumn();
            final TableModel tableModel = new TableModel(rowModel, new ColumnModel[]{
                new NameColumn(),visibleCol});
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
                if(candidate instanceof DomNode){
                    final WLabel lbl = new WLabel();
                    lbl.setText(((Clothe)candidate).getName() );
                    return lbl;
                }else{
                    return new WSpace(new Extent(2));
                }
            }
        }
    }
    
    public static class StateColumn extends DefaultColumnModel{

        public StateColumn() {
            super(Chars.EMPTY,new VisiblePresenter());
        }

        private static class VisiblePresenter implements ObjectPresenter{

            public Widget createWidget(Object candidate) {
                if(candidate instanceof TreeRowPath){
                    candidate = ((TreeRowPath)candidate).getLeaf();
                }
                if(candidate instanceof Clothe){
                    final Clothe clothe = (Clothe) candidate;
                    final Sequence states = clothe.getStates();
                    final ClotheState currentState = clothe.getState();
                    
                    final WContainer container = new WContainer(new GridLayout(1, -1, 5, 0));
                    container.getStyle().getSelfRule().setProperties(new Chars(
                            "background : none\n"+
                            "margin     : [5,5,5,5]"));
                    final CheckGroup group = new CheckGroup();
                    for(int i=0,n=states.getSize();i<n;i++){
                        final ClotheState state = (ClotheState) states.get(i);
                        final WSwitch button = new WSwitch(null, state.getIcon(), new EventListener() {
                            public void receiveEvent(Event event) {
                                clothe.setState(state);
                            }
                        });
                        group.add(button);
                        button.setCheck(state == currentState);
                        //special style
                        button.setId(new Chars("state"));
                        container.addChild(button);
                    }
                    
                    return container;
                }else{
                    return new WSpace(new Extent(2));
                }
            }

        }

    }
    
    
}
