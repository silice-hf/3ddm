package freeh.kitsune.audios;

import freeh.kitsune.ui.GameObjectPresenter;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.engine.ui.model.ColumnModel;
import un.engine.ui.model.DefaultColumnModel;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.RowModel;
import un.engine.ui.model.TableModel;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WTable;
import un.engine.ui.widget.Widget;

/**
 * Music selection pane.
 */
public class MusicSelector extends WContainer {

    public static final Chars PROPERTY_MUSIC = new Chars("Music");
    
    private final WTable table = new WTable();

    public MusicSelector() {
        getStyle().getSelfRule().setProperty(new Chars("margin"), new Chars("[6,6,6,6]"));

        setLayout(new BorderLayout());

        table.setRowHeight(25);
        table.setModel(new TableModel(
                new DefaultRowModel(Musics.getAll()), 
                new ColumnModel[]{new DefaultColumnModel(new Chars("Musics"), GameObjectPresenter.INSTANCE)}));
        addChild(table, BorderConstraint.CENTER);

        table.getModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Music p = (Music) table.getModel().getRowModel().getElement(selected[0]);
                    getEventManager().sendPropertyEvent(MusicSelector.this, PROPERTY_MUSIC, null, p);
                }
            }
        });

        table.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        table.setRowHeight(24);
    }
    
    public void setMusic(Music candidate){
        final Music old = getMusic();
        if(old==candidate) return;
        
        if(candidate!=null){
            table.getModel().getRowModel().setSelectedIndex(new int[]{Musics.getAll().search(candidate)});
        }else{
            table.getModel().getRowModel().setSelectedIndex(new int[]{});
        }
        
        getEventManager().sendPropertyEvent(this, PROPERTY_MUSIC, old, candidate);
    }
    
    public Music getMusic(){
        final Object[] elements = table.getModel().getRowModel().getSelectedElements();
        if(elements.length>0){
            return (Music) elements[0];
        }else{
            return null;
        }
    }

}
