package freeh.kitsune.poses;

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
 * Animation config pane.
 */
public class PoseSelector extends WContainer {

    public static final Chars PROPERTY_POSE = new Chars("Pose");
    
    private final WTable table = new WTable();

    public PoseSelector() {
        getStyle().getSelfRule().setProperty(new Chars("margin"), new Chars("[6,6,6,6]"));

        setLayout(new BorderLayout());


        table.setModel(new TableModel(new DefaultRowModel(Poses.getAll()), new ColumnModel[]{
            new DefaultColumnModel(new Chars("Poses"), null)
        }));
        addChild(table, BorderConstraint.CENTER);

        table.getModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Pose p = (Pose) table.getModel().getRowModel().getElement(selected[0]);
                    getEventManager().sendPropertyEvent(PoseSelector.this, PROPERTY_POSE, null, p);
                }
            }
        });

        table.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        table.setRowHeight(24);
    }

    public void setPose(Pose candidate){
        final Pose old = getPose();
        if(old==candidate) return;
        
        if(candidate!=null){
            table.getModel().getRowModel().setSelectedIndex(new int[]{Poses.getAll().search(candidate)});
        }else{
            table.getModel().getRowModel().setSelectedIndex(new int[]{});
        }
        getEventManager().sendPropertyEvent(this, PROPERTY_POSE, old, candidate);
    }
    
    public Pose getPose(){
        final Object[] elements = table.getModel().getRowModel().getSelectedElements();
        if(elements.length>0){
            return (Pose) elements[0];
        }else{
            return null;
        }
    }

}
