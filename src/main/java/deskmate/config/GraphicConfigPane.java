package deskmate.config;

import deskmate.View;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.engine.ui.component.WButtonBar;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.FormConstraint;
import un.engine.ui.layout.FormLayout;
import un.engine.ui.model.NumberSpinnerModel;
import un.engine.ui.widget.WCheck;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WSpinner;
import un.engine.ui.widget.menu.WMenuButton;

/**
 * Configuration panel
 */
public class GraphicConfigPane extends WContainer {

    private final View view;

    private final WCheck depthOfField = new WCheck(new Chars("Depth of field"));
    private final WCheck bloom = new WCheck(new Chars("Bloom"));
    private final WSpinner bloomValue = new WSpinner(new NumberSpinnerModel(), 0.10d);
    private final WCheck ground = new WCheck(new Chars("Ground"));
    private final WMenuButton apply = new WMenuButton(new Chars("Apply"), new EventListener() {
        @Override
        public void receiveEvent(Class eventClass, Event event) {
            updateView();
        }
    });

    public GraphicConfigPane(View view) {
        super(new BorderLayout());
        this.view = view;

        bloom.setCheck(true);
        depthOfField.setCheck(true);
        ground.setCheck(true);

        WContainer center = new WContainer(new FormLayout(FormLayout.VALIGN_TOP));
        center.addChild(ground, new FormConstraint(0, 0));
        center.addChild(bloom, new FormConstraint(1, 0));
        center.addChild(bloomValue, new FormConstraint(1, 1));
        center.addChild(depthOfField, new FormConstraint(2, 0));
        addChild(center, BorderConstraint.CENTER);

        WButtonBar bottom = new WButtonBar();
        bottom.addChild(apply, new FormConstraint(0, 0));
        addChild(bottom, BorderConstraint.BOTTOM);
    }

    private void updateView() {
        float val = ((Number) bloomValue.getValue()).floatValue();
        view.updatePipeline(bloom.isCheck(), val, depthOfField.isCheck(), ground.isCheck());
    }

}
