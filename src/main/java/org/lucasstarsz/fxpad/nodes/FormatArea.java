package org.lucasstarsz.fxpad.nodes;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.fxmisc.richtext.StyleClassedTextArea;

public class FormatArea extends StyleClassedTextArea {

    protected Rectangle gutterRect = new Rectangle();

    public FormatArea() {
        super();
        bindRect();
    }

    private void bindRect() {
        gutterRect.heightProperty().bind(this.heightProperty());
        gutterRect.getStyleClass().add("lineno");
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        try {
            ObservableList<Node> children = getChildren();
            if (!(children.get(0) == gutterRect)) {
                children.add(0, gutterRect);
            }

            int index = visibleParToAllParIndex(0);
            double wd = getParagraphGraphic(index).prefWidth(10);
            gutterRect.setWidth(wd);
//            gutterRect.setHeight(getHeight() + 100);
        } catch (Exception ignored) {
        }
    }
}
