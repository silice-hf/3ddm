package deskmate.config;

import deskmate.View;
import un.api.character.Chars;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.FormConstraint;
import un.engine.ui.layout.FormLayout;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WTabContainer;
import un.science.encoding.IOException;
import un.api.image.Images;
import un.system.path.Paths;

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
        tabs.setTabPosition(WTabContainer.TAB_POSITION_LEFT);

        tabs.addTab(new ModelConfigPane(view), createHeader("Model","mod>/style/model.png"));
        tabs.addTab(new AnimationConfigPane(view), createHeader("Animation","mod>/style/video.png"));
        tabs.addTab(new MusicConfigPane(view),createHeader("Music","mod>/style/audio.png"));
        tabs.addTab(new GraphicConfigPane(view), createHeader("Graphic","mod>/style/config.png"));
        tabs.setActiveTab(0);
    }

    private WContainer createHeader(String title, String image){
        WContainer container = new WContainer(new FormLayout());
        container.getStyle().getSelfRule().setProperty(new Chars("background"), new Chars("none"));
        container.getStyle().getSelfRule().setProperty(new Chars("margin"), new Chars("[5,5,5,5]"));
        
        final WLabel lbl = new WLabel(new Chars(title));
        try {
            lbl.setImage(Images.read(Paths.resolve(image)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        container.addChild(lbl, new FormConstraint(0, 0));
        
        return container;
    }
    
}
