/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.gui.bookview;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


abstract class SelectionPane extends AnchorPane {
    private Rectangle selection = null;

    SelectionPane() {
        super();

        setOnMousePressed(ev -> {
            if (ev.getButton() == MouseButton.SECONDARY) {
                onBeforeStartSelection();
                selection = createNewRectangle();
                getChildren().add(selection);
                selection.setX(ev.getX());
                selection.setY(ev.getY());
            }
        });
        setOnMouseReleased(ev -> {
            if (selection != null) {
                onCreateSelection(localToScreen(selection.getBoundsInLocal()));
                getChildren().remove(selection);
                selection = null;
            }
        });

        setOnMouseDragged(ev -> {
            if (selection != null) {
                final double startY = selection.getY();
                final double endY = ev.getY();

                selection.setWidth(getWidth());
                selection.setHeight(Math.abs(startY - endY));
                selection.setX(0);
                selection.setY(Math.min(startY, endY));
            }
        });
    }

    protected void onBeforeStartSelection() {
        // no-op
    }
    protected abstract void onCreateSelection (Bounds bounds);

    private Rectangle createNewRectangle() {
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.setArcWidth(5);
        rect.setArcHeight(5);
        rect.setFill(Color.YELLOWGREEN);
        return rect;
    }
}
