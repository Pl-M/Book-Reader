/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.bookview.textwidget;

import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * This interface defines functions which are used by {@link ImagesContainer}.
 * Each {@link Node} added to it should implement this interface.
 */
public interface TextImage {

    static void cloneTextNode(final Text source, final Text target) {
        /* Remove ending newline symbol if available since all Text widgets
         * go vertically one by one and even if they don't have newlines at the end
         * visually they are displayed as if they do.
         */
        final String text = source.getText();
        int end = text.length();
        //if (textString.endsWith(System.getProperty("line.separator")))
        if (text.endsWith("\n"))
            end -= 1;
        target.setText(text.substring(0, end));

        target.setFill(source.getFill());
        target.setFontSmoothingType(source.getFontSmoothingType());
        target.setFont(source.getFont());
        target.setTextAlignment(source.getTextAlignment());
        target.setWrappingWidth(source.getWrappingWidth());
        target.setLineSpacing(source.getLineSpacing());
        target.setSmooth(source.isSmooth());
    }

    int getStartOffsetPos();
    int getEndOffsetPos();
    double getImageHeight();
    double getImageWidth();
    /**
     *  Use this function to get text since text in the widget may
     * lack a newline symbol.
     */
    String getImageText();
    Node getNode();
    Integer getOffsetByScreenCoordinate(
            final double localToScreenX, final double localToScreenY);
}
