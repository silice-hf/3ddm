package deskmate.config;

import deskmate.View;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.GridLayout;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.RowModel;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WList;
import un.engine.ui.widget.WScrollContainer;
import un.system.path.Path;

/**
 * Audio config pane.
 */
public class MusicConfigPane extends WContainer {

    private final View view;
    private final WList musics = new WList();

    private final EventListener viewListener = new EventListener() {

        @Override
        public void receiveEvent(Class eventClass, Event event) {
            PropertyEvent pe = (PropertyEvent) event;
            if (View.PROPERTY_AUDIO.equals(pe.getPropertyName())) {
                update();
            }
        }
    };

    public MusicConfigPane(final View view) {
        this.view = view;
        this.view.addEventListener(PropertyEvent.class, viewListener);

        setLayout(new GridLayout(1, 2));

        WContainer left = new WContainer(new BorderLayout());
        WContainer right = new WContainer(new BorderLayout());
        addChild(left);
        addChild(right);

        WScrollContainer scrollMusics = new WScrollContainer();
        scrollMusics.addChild(musics);
        musics.setModel(new DefaultRowModel(view.allAudios));
        left.addChild(scrollMusics, BorderConstraint.CENTER);

        musics.getModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Path p = (Path) musics.getModel().getElement(selected[0]);
                    view.changeAudio(p);
                }
            }
        });

        update();
    }

    private void update() {
        //update the anim list
        if (view.currentAudioPath != null) {
            musics.getModel().setSelectedIndex(new int[]{view.allAudios.search(view.currentAudioPath)});
        } else {
            musics.getModel().setSelectedIndex(new int[]{});
        }
    }

}
