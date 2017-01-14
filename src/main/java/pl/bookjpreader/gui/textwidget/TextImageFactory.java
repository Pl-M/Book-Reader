/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui.textwidget;

import javafx.scene.text.Text;
/*
 * The only aim of this class is to choose which widget to create based on given quality.
 * The widget to create must be a Node widget implementing TextImage interface.
 */

public class TextImageFactory {

    public static TextImage build (Text text, int offset, int quality) {
        /* @param quality: if true creates widget which output higher quality.
         * If height of the Text is too high it can lead to
         * exception while creating an image.
         */
        if (quality == 0)
            return new TextPage(text, offset, false);
        else if (quality == 1 || text.getLayoutBounds().getHeight() > 800)
            return new TextPage(text, offset, true);
        else
            return new ImagePage(text, offset);
    }
}
