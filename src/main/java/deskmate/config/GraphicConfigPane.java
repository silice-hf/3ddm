package deskmate.config;

import deskmate.Config;
import deskmate.DataLoader;
import deskmate.View;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.engine.ui.component.WColorChooser;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.FormConstraint;
import un.engine.ui.layout.FormLayout;
import un.engine.ui.model.NumberSpinnerModel;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WCheck;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WSpinner;

/**
 * Configuration panel
 */
public class GraphicConfigPane extends WContainer {

    private final View view;

    private final WCheck ground             = new WCheck(new Chars("Ground"));
    private final WCheck depthOfField       = new WCheck(new Chars("Depth of field"));
    private final WCheck bloom              = new WCheck(new Chars("Bloom"));
    private final WSpinner bloomValue       = new WSpinner(new NumberSpinnerModel(Double.class,0,null,null,1), Config.BLOOM_RATIO);
    private final WCheck toonShadesState    = new WCheck(new Chars("Toon shade"));
    private final WSpinner toonShadesNb     = new WSpinner(new NumberSpinnerModel(Double.class,0,null,null,1), Config.TOON_NBSHADE);
    private final WCheck toonBorderState    = new WCheck(new Chars("Toon border"));
    private final WSpinner toonBorderWidth  = new WSpinner(new NumberSpinnerModel(Double.class,0,null,null,1), Config.BORDER_WIDTH);
    private final WColorChooser toonBorderColor = new WColorChooser(Config.BORDER_COLOR);
    private final WButton apply             = new WButton(new Chars("Apply"), new EventListener() {
        @Override
        public void receiveEvent(Class eventClass, Event event) {
            updateView();
        }
    });

    public GraphicConfigPane(View view) {
        super(new BorderLayout());
        getStyle().getSelfRule().setProperty(new Chars("margin"), new Chars("[6,6,6,6]"));
        this.view = view;

        bloom.setCheck(true);
        depthOfField.setCheck(true);
        ground.setCheck(true);

        WContainer center = new WContainer();
        FormLayout layout = new FormLayout();
        center.setLayout(layout);
        layout.setRowSize(6, FormLayout.SIZE_EXPAND);
        
        center.addChild(ground,          new FormConstraint(0, 0));
        center.addChild(depthOfField,    new FormConstraint(0, 1));
        center.addChild(bloom,           new FormConstraint(0, 2));        
        center.addChild(bloomValue,      new FormConstraint(1, 2));
        center.addChild(toonShadesState, new FormConstraint(0, 3));
        center.addChild(toonShadesNb,    new FormConstraint(1, 3));
        center.addChild(toonBorderState, new FormConstraint(0, 4));
        center.addChild(toonBorderWidth, new FormConstraint(1, 4));        
        center.addChild(toonBorderColor, new FormConstraint(0, 5,2,1));   
        
        center.addChild(apply, new FormConstraint(0, 7));
        
        addChild(center, BorderConstraint.CENTER);
    }

    private void updateView() {
        Config.BLOOM_RATIO = ((Number) bloomValue.getValue()).floatValue();
        Config.TOON_NBSHADE = toonShadesState.isCheck() ? ((Number)toonShadesNb.getValue()).intValue() : -1;
        Config.BORDER_COLOR = toonBorderColor.getColor();
        Config.BORDER_WIDTH = toonBorderState.isCheck() ? ((Number)toonBorderWidth.getValue()).floatValue() : 0;
        view.updatePipeline(bloom.isCheck(), Config.BLOOM_RATIO, depthOfField.isCheck(), ground.isCheck());
        view.currentModel.accept(DataLoader.SET_BORDER_COLOR, Config.BORDER_COLOR);
        view.currentModel.accept(DataLoader.SET_BORDER_WIDTH, Config.BORDER_WIDTH);
        view.currentModel.accept(DataLoader.SET_TOON_NBSHADE, Config.TOON_NBSHADE);
    }

}
