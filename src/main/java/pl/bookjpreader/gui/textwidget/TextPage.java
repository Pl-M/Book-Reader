/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui.textwidget;

import javafx.scene.CacheHint;
import javafx.scene.text.Text;

/*
 * This class is based on Text widget.
 * It consumes less memory than a class based on ImageView,
 * but has slightly worse quality.
 * It uses Cache hints but as javadocs says it isn't mandatory.
 * Also it demands to reinitialize all text parameters.
 * It lacks an exception problem with high length of the image.
 */

public class TextPage extends Text implements TextImage{

    final private int textStartOffset;
    final private int textEndOffset;
    final private double fullHeight;
    final private double fullWidth;
    final private String textString;

    public TextPage(Text text, int offset, boolean quality) {
        /*
         * @param quality: if true set hint to quality, otherwise to speed.
         */
        super();
        textString = text.getText();
        int textLength = textString.length();

        textStartOffset = offset;
        textEndOffset = text.getText().length() + offset;

        /* Remove ending newline symbol if available since all Text widgets
         * go vertically one by one and even if they don't have newlines at the end
         * visually they are displayed as if they do.
         */
        int end = textLength;
        //if (textString.endsWith(System.getProperty("line.separator")))
        if (textString.endsWith("\n"))
            end -= 1;
        setText(textString.substring(0, end));


        // Make copy of Text parameters.
        setFill(text.getFill());
        setFontSmoothingType(text.getFontSmoothingType()); // to smooth font
        setFont(text.getFont());
        setTextAlignment(text.getTextAlignment());
        setWrappingWidth(text.getWrappingWidth());
        setLineSpacing(text.getLineSpacing());
        setSmooth(text.isSmooth());

        fullHeight = this.getLayoutBounds().getHeight();
        fullWidth = this.getLayoutBounds().getWidth();

        // Make quality settings.
        setCache(true);
        if (quality)
            this.setCacheHint(CacheHint.QUALITY);
        else
            this.setCacheHint(CacheHint.SPEED);
    }

    public int getStartOffsetPos(){
        return textStartOffset;
    }
    public int getEndOffsetPos(){
        return textEndOffset;
    }
    public double getImageHeight(){
        return fullHeight;
    }
    public double getImageWidth(){
        return fullWidth;
    }
    public String getImageText(){
        /* Use this function to get text since text in the widget may
         * lack a newline symbol.
         */
        return textString;
    }
}
