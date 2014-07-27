package deskmate.config;

import deskmate.View;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.GridLayout;
import un.engine.ui.model.ColumnModel;
import un.engine.ui.model.DefaultColumnModel;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.RowModel;
import un.engine.ui.model.TableModel;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WTable;
import un.system.path.Path;

/**
 * Animation config pane.
 */
public class AnimationConfigPane extends WContainer {

    private final View view;
    private final WTable anims = new WTable();

    private final EventListener viewListener = new EventListener() {

        @Override
        public void receiveEvent(Class eventClass, Event event) {
            PropertyEvent pe = (PropertyEvent) event;
            if (View.PROPERTY_ANIM.equals(pe.getPropertyName())) {
                update();
            }
        }
    };

    public AnimationConfigPane(final View view) {
        this.view = view;
        this.view.addEventListener(PropertyEvent.class, viewListener);
        getStyle().getSelfRule().setProperty(new Chars("margin"), new Chars("[6,6,6,6]"));

        setLayout(new GridLayout(1, 2));

        WContainer left = new WContainer(new BorderLayout());
        WContainer right = new WContainer(new BorderLayout());
        addChild(left);
        addChild(right);

        anims.setModel(new TableModel(new DefaultRowModel(view.allAnimations), new ColumnModel[]{
            new DefaultColumnModel(new Chars("Animations"), null)
        }));
        left.addChild(anims, BorderConstraint.CENTER);

        anims.getModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Path p = (Path) anims.getModel().getRowModel().getElement(selected[0]);
                    view.changeAnimation(p);
                }
            }
        });

        update();
    }

    private void update() {
        //update the anim list
        if (view.currentAnimationPath != null) {
            anims.getModel().getRowModel().setSelectedIndex(new int[]{view.allAnimations.search(view.currentAnimationPath)});
        } else {
            anims.getModel().getRowModel().setSelectedIndex(new int[]{});
        }
    }

}
