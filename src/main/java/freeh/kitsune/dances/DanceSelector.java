package freeh.kitsune.dances;

import freeh.kitsune.ui.GameObjectPresenter;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.engine.ui.model.DefaultColumnModel;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.RowModel;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WTable;

/**
 * Dances config pane.
 */
public class DanceSelector extends WContainer {

    public static final Chars PROPERTY_DANCE = new Chars("Dance");
    
    private final WTable table = new WTable();

    public DanceSelector() {

        setLayout(new BorderLayout());

        table.setRowModel(new DefaultRowModel(Dances.getAll()));
        table.getColumnModels().add(new DefaultColumnModel(new Chars("Dances"), GameObjectPresenter.INSTANCE));
        
        addChild(table, BorderConstraint.CENTER);

        table.getRowModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Dance p = (Dance) table.getRowModel().getElement(selected[0]);
                    getEventManager().sendPropertyEvent(DanceSelector.this, PROPERTY_DANCE, null, p);
                }
            }
        });

        table.setRowHeight(24);
    }

    public void setDance(Dance candidate){
        final Dance old = getDance();
        if(old==candidate) return;
        
        if(candidate!=null){
            table.getRowModel().setSelectedIndex(new int[]{Dances.getAll().search(candidate)});
        }else{
            table.getRowModel().setSelectedIndex(new int[]{});
        }
        getEventManager().sendPropertyEvent(this, PROPERTY_DANCE, old, candidate);
    }
    
    public Dance getDance(){
        final Object[] elements = table.getRowModel().getSelectedElements();
        if(elements.length>0){
            return (Dance) elements[0];
        }else{
            return null;
        }
    }
    
}
