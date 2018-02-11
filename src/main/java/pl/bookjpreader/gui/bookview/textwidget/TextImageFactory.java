/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.bookview.textwidget;

import javafx.scene.text.Text;
import java.util.concurrent.atomic.AtomicReference;

import static pl.bookjpreader.commons.ThreadUtils.submitToFXThreadAndWait;

/**
 * The only aim of this class is to choose which widget to create based on given quality.
 * The widget to create must be a Node widget implementing TextImage interface.
 */
public class TextImageFactory {
    /**
     * @param quality if true creates widget which output higher quality.
     * If height of the Text is too high it can lead to
     * exception while creating an image.
     */
    public static TextImage build (Text text, int offset, int quality) {
        final AtomicReference<TextImage> ti = new AtomicReference<>();

        if (quality == 0) {
            ti.set(new TextPage(text, offset, false));
        } else if (quality == 1 || text.getLayoutBounds().getHeight() > 800) {
            ti.set(new TextPage(text, offset, true));
        } else {
            // snapshot(...) function of ImageView demands FX-thread.
            submitToFXThreadAndWait(() -> ti.set(new ImagePage(text, offset)));
        }

        return ti.get();
    }
}
