

package freeh.kitsune.model.custom;

import freeh.kitsune.model.custom.clothe.DressSelector;
import freeh.kitsune.model.custom.body.HairSelector;
import freeh.kitsune.GameBundle;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.GridLayout;
import un.engine.ui.io.RSReader;
import un.engine.ui.model.CheckGroup;
import un.engine.ui.style.WStyle;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WSwitch;
import un.engine.ui.widget.WTabContainer;

/**
 *
 */
public class CustomModelSelector extends AbstractCustomSelector{
        
    
    private final WSwitch meshButton = new WSwitch(GameBundle.get(new Chars("model.mesh")), new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            mainPane.removeChildren();
            mainPane.addChild(meshTabs, BorderConstraint.CENTER);
        }
    });
    private final WSwitch clotheButton = new WSwitch(GameBundle.get(new Chars("model.clothe")), new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            mainPane.removeChildren();
            mainPane.addChild(clotheTabs, BorderConstraint.CENTER);
        }
    });
    
    private final WTabContainer meshTabs = new WTabContainer();
    private final WTabContainer clotheTabs = new WTabContainer();
    private final WContainer mainPane = new WContainer(new BorderLayout());
    
    private final HairSelector hairPane;
    private final DressSelector dressPane;
    
    public CustomModelSelector() {
        setLayout(new BorderLayout());
                
        meshButton.setId(new Chars("state"));
        clotheButton.setId(new Chars("state"));
        
        final WContainer topPane = new WContainer(new GridLayout(1,2,0,0));
        topPane.addChild(meshButton);
        topPane.addChild(clotheButton);
        topPane.getStyle().getSelfRule().setProperties(new Chars(
                "margin     : [5,5,5,5];\n"+
                "background : none;"));
        
        final CheckGroup group = new CheckGroup();
        group.add(meshButton);
        group.add(clotheButton);
        
        meshTabs.setTabPosition(WTabContainer.TAB_POSITION_LEFT);
        clotheTabs.setTabPosition(WTabContainer.TAB_POSITION_LEFT);
        
        hairPane = new HairSelector();
        dressPane = new DressSelector();
        
        meshTabs.addTab(hairPane, new WLabel(new Chars("hair")));
        clotheTabs.addTab(dressPane, new WLabel(new Chars("dress")));
                
        final Chars text = new Chars(
                "(\"Class\"='un.engine.ui.widget.WTabContainer$WTabBar':class) {\n" +
                "    (\"TabPosition\"=0) {\n" +
                "        margin     : [5,5,0,5];\n" +
                "        background : none;\n" +
                "    }\n" +
                "    (\"TabPosition\"=1) {\n" +
                "        margin     : [0,5,5,5];\n" +
                "        background : none;\n" +
                "    }\n" +
                "    (\"TabPosition\"=2) {\n" +
                "        margin     : [5,0,5,5];\n" +
                "        background : none;\n" +
                "    }\n" +
                "    (\"TabPosition\"=3) {\n" +
                "        margin     : [5,5,5,0];\n" +
                "        background : none;\n" +
                "    }\n" +
                "}");
        final WStyle style = RSReader.readStyle(text);
        meshTabs.getStyle().getRules().addAll(style.getRules());
        
        
        addChild(topPane,BorderConstraint.TOP);
        addChild(mainPane,BorderConstraint.CENTER);
        
        
        meshButton.setCheck(true);
    }
        
}
