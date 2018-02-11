/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.bookview.textwidget;

import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.text.Text;

import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;

/**
 * This class is based on {@link Text} widget.
 * It consumes less memory than a class based on {@link javafx.scene.image.ImageView},
 * but has slightly worse quality.
 * It uses Cache hints but as documentation says it isn't mandatory.
 * Also it demands to reinitialize all text parameters.
 * It lacks an exception problem with high length of the image.
 */
class TextPage extends Text implements TextImage{

    final private int textStartOffset;
    final private int textEndOffset;
    final private double fullHeight;
    final private double fullWidth;

    /**
     * @param quality if {@code true} sets hint to quality, otherwise to speed.
     */
    TextPage(final Text textNode, final int offset, final boolean quality) {
        super();

        textStartOffset = offset;
        textEndOffset = textNode.getText().length() + offset;

        TextImage.cloneTextNode(textNode, this);

        fullHeight = this.getLayoutBounds().getHeight();
        fullWidth = this.getLayoutBounds().getWidth();

        setCache(true);
        if (quality)
            this.setCacheHint(CacheHint.QUALITY);
        else
            this.setCacheHint(CacheHint.SPEED);
    }

    @Override
    public int getStartOffsetPos(){
        return textStartOffset;
    }
    @Override
    public int getEndOffsetPos(){
        return textEndOffset;
    }
    @Override
    public double getImageHeight(){
        return fullHeight;
    }
    @Override
    public double getImageWidth(){
        return fullWidth;
    }

    @Override
    public String getImageText(){
        return this.getText();
    }
    @Override
    public Node getNode() {
        return this;
    }

    public Integer getOffsetByScreenCoordinate(final double localToScreenX, final double localToScreenY) {
        // TODO: rewrite using Java 9 API.
        // localToScreenX & localToScreenY should be coordinates relative to screen,
        // e.g. (0,0) is the left top corner of the monitor screen.

        Integer idx = (Integer) queryAccessibleAttribute(OFFSET_AT_POINT,
                new Point2D(localToScreenX, localToScreenY));
        if (idx != null) {
            idx += getStartOffsetPos();
        }
        return idx;
    }

}
