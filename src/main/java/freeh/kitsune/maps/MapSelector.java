

package freeh.kitsune.maps;

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
 * 
 */
public class MapSelector extends WContainer{

    public static final Chars PROPERTY_MAP = new Chars("Map");
    
    private final WTable table = new WTable();

    public MapSelector() {

        setLayout(new BorderLayout());

        table.setRowHeight(25);
        table.setRowModel(new DefaultRowModel(Maps.getAll()));
        table.getColumnModels().add(new DefaultColumnModel(new Chars("Maps"), GameObjectPresenter.INSTANCE));
        addChild(table, BorderConstraint.CENTER);

        table.getRowModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Map p = (Map) table.getRowModel().getElement(selected[0]);
                    getEventManager().sendPropertyEvent(MapSelector.this, PROPERTY_MAP, null, p);
                }
            }
        });

        table.setRowHeight(24);
    }
    
    public void setMap(Map candidate){
        final Map old = getMap();
        if(old==candidate) return;
        
        if(candidate!=null){
            table.getRowModel().setSelectedIndex(new int[]{Maps.getAll().search(candidate)});
        }else{
            table.getRowModel().setSelectedIndex(new int[]{});
        }
        
        getEventManager().sendPropertyEvent(this, PROPERTY_MAP, old, candidate);
    }
    
    public Map getMap(){
        final Object[] elements = table.getRowModel().getSelectedElements();
        if(elements.length>0){
            return (Map) elements[0];
        }else{
            return null;
        }
    }

}
