package freeh.kitsune.audios;

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
 * Music selection pane.
 */
public class MusicSelector extends WContainer {

    public static final Chars PROPERTY_MUSIC = new Chars("Music");
    
    private final WTable table = new WTable();

    public MusicSelector() {
        setLayout(new BorderLayout());

        table.setRowHeight(25);
        table.setRowModel(new DefaultRowModel(Musics.getAll()));
        table.getColumnModels().add(new DefaultColumnModel(new Chars("Musics"), GameObjectPresenter.INSTANCE));
        addChild(table, BorderConstraint.CENTER);

        table.getRowModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Music p = (Music) table.getRowModel().getElement(selected[0]);
                    getEventManager().sendPropertyEvent(MusicSelector.this, PROPERTY_MUSIC, null, p);
                }
            }
        });

        table.setRowHeight(24);
    }
    
    public void setMusic(Music candidate){
        final Music old = getMusic();
        if(old==candidate) return;
        
        if(candidate!=null){
            table.getRowModel().setSelectedIndex(new int[]{Musics.getAll().search(candidate)});
        }else{
            table.getRowModel().setSelectedIndex(new int[]{});
        }
        
        getEventManager().sendPropertyEvent(this, PROPERTY_MUSIC, old, candidate);
    }
    
    public Music getMusic(){
        final Object[] elements = table.getRowModel().getSelectedElements();
        if(elements.length>0){
            return (Music) elements[0];
        }else{
            return null;
        }
    }

}
