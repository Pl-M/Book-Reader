/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.bookview.textwidget;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.VBox;


public final class ImagesContainer extends VBox {

    public ImagesContainer() {
        super();
        setLineSpacing(100.0);
    }
    /**
     * This function sets distance between elements in the box.
     * It is needed if line spacing is changed for {@link javafx.scene.text.Text} elements.
     * @param spacing space between elements in the container in pixels.
     */
    public void setLineSpacing(double spacing){
        this.setSpacing(spacing);
    }

    public void addBottomImage(TextImage ti){
        if (ti != null)
            getChildren().add(ti.getNode());
    }
    /**
     * Adds top {@link TextImage} element and adapt position
     * to remain as it was.
     */
    public void addTopImage(TextImage ti){
        if (ti != null){
            getChildren().add(0, ti.getNode());
            setTranslateY(getTranslateY() - ti.getImageHeight() - getSpacing());
        }
    }
    public void removeBottomImage(){
        int size = getChildren().size();
        if (size < 1)
            return;
        getChildren().remove(size-1);
    }
    /**
     * Removes first {@link TextImage} element and adapts position
     * to remain as it was.
     */
    public void removeTopImage(){
        if (getChildren().isEmpty())
            return;

        double newPos = getTranslateY() + getTopImageHeight();
        getChildren().remove(0);
        setTranslateY(newPos);
    }

    public Double getTopImageHeight(){
        if (getChildren().isEmpty())
            return null;

        TextImage ti = (TextImage)getChildren().get(0);
        return ti.getImageHeight() + getSpacing();
    }
    public Double getBottomImageHeight(){
        if (getChildren().isEmpty())
            return null;

        TextImage ti = (TextImage)getChildren().get(getChildren().size() - 1);
        return ti.getImageHeight() + getSpacing();
    }

    public double getContainerHeight(){
        double height = 0;
        for (Object ti: getChildren())
            height += ((TextImage)ti).getImageHeight() + getSpacing();
        height -= getSpacing(); // there is no spacing at the top and bottom
        return height;
    }

    public Integer getStartOffset(){
        if (getChildren().isEmpty())
            return null;
        TextImage ti = (TextImage)getChildren().get(0);
        return ti.getStartOffsetPos();
    }
    public Integer getEndOffset(){
        if (getChildren().isEmpty())
            return null;
        TextImage ti = (TextImage)getChildren().get(getChildren().size() - 1);
        return ti.getEndOffsetPos();
    }

    public void clear(){
        getChildren().clear();
        setTranslateY(0);
    }

    public Integer getVisibleStartOffset(){
        final Bounds localToScreen = localToScreen(getBoundsInLocal());
        final Bounds localToScene = localToScene(getBoundsInLocal());

        final double localToScreenX = localToScreen.getMinX() - localToScene.getMinX();
        final double localToScreenY = localToScreen.getMinY() - localToScene.getMinY();
        return findOffsetByCoordinate(localToScreenX, localToScreenY);
    }
    /**
     * @param region contains coordinates (relative to screen)
     * of selection.
     */
    public String getTextByCoordinates(Bounds region) {
        String result = null;
        final Integer firstContainerOffset = getStartOffset();

        final Integer startOffset = findOffsetByCoordinate(region.getMinX(), region.getMinY());
        final Integer endOffset = findOffsetByCoordinate(region.getMaxX(), region.getMaxY());
        if (startOffset != null && endOffset != null) {
            result = getText().substring(startOffset - firstContainerOffset,
                    endOffset - firstContainerOffset);
        }
        return result;
    }

    private String getText(){
        StringBuilder s = new StringBuilder();
        for (Object o: getChildren()){
            TextImage ti = (TextImage)o;
            final String text = ti.getImageText();
            s.append(text);
            if (!text.endsWith("\n")) {
                s.append("\n");
            }
        }
        return s.toString();
    }
    private Integer findOffsetByCoordinate(final double localToScreenX, final double localToScreenY) {
        // localToScreenX & localToScreenY should be coordinates relative to screen,
        // e.g. (0,0) is the left top corner of the monitor screen.

        if (getChildren().isEmpty()) {
            return null;
        }

        TextImage textImageContainingY = null;
        // Find Node which contains given Y coordinate.
        for (Node node: getChildren()){
            final double nodeLocalToScreenY = node
                    .localToScreen(node.getBoundsInLocal()).getMaxY();
            if (nodeLocalToScreenY > localToScreenY) {
                textImageContainingY = (TextImage) node;
                break;
            }
        }
        Integer symbolOffset = null;
        if (textImageContainingY != null) {
            symbolOffset = textImageContainingY
                    .getOffsetByScreenCoordinate(localToScreenX, localToScreenY);
        }
        return symbolOffset;
    }

}
