package deskmate.config;

import deskmate.View;
import un.api.character.Chars;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WTabContainer;

/**
 * Configure the visible model,animation and audio.
 */
public class SceneConfigPane extends WContainer {

    private WTabContainer tabs = new WTabContainer();
    private final View view;

    public SceneConfigPane(View view) {
        this.view = view;
        setLayout(new BorderLayout());
        addChild(tabs, BorderConstraint.CENTER);

        tabs.addChild(new ModelConfigPane(view), new WLabel(new Chars("Model")));
        tabs.addChild(new AnimationConfigPane(view), new WLabel(new Chars("Animation")));
        tabs.addChild(new MusicConfigPane(view), new WLabel(new Chars("Music")));
        tabs.setActiveTab(0);
    }

}
