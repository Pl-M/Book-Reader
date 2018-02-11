/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.bookview.textwidget;

import com.sun.javafx.scene.text.HitInfo;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * This class is based on {@link ImageView} widget.
 * It consumes more memory than a class based on {@link Text},
 * but can have better quality. <br>
 * It may have an exception problem with high length of the image.
 */
class ImagePage extends ImageView implements TextImage{

    private final Text textNode; // need this to get position
    private final int textStartOffset;
    private final int textEndOffset;
    private final double fullHeight;
    private final double fullWidth;

    /**
     * @param offset starting position of the text.
     * @param textNode Text element with text.
     */
    ImagePage(final Text textNode, final int offset) {
        super();

        textStartOffset = offset;
        textEndOffset = textNode.getText().length() + offset;

        this.textNode = new Text();
        TextImage.cloneTextNode(textNode, this.textNode);

        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);
        this.setImage(textNode.snapshot(snapshotParameters, null));

        fullHeight = getImage().getHeight();
        fullWidth = getImage().getWidth();
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
        return textNode.getText();
    }
    @Override
    public Node getNode() {
        return this;
    }
    @Override
    public Integer getOffsetByScreenCoordinate(
            final double localToScreenX, final double localToScreenY) {
        // TODO: rewrite using Java 9 API.
        // localToScreenX & localToScreenY should be coordinates relative to screen,
        // e.g. (0,0) is the left top corner of the monitor screen.

        final Bounds bounds = localToScreen(getBoundsInLocal());
        final double x = localToScreenX - bounds.getMinX();
        final double y = localToScreenY - bounds.getMinY();

        final HitInfo hitInfo = textNode.impl_hitTestChar(new Point2D(x, y));
        final int idx = hitInfo.getCharIndex() + getStartOffsetPos();

        return idx;
    }
}

