/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui.textwidget;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ImagePage extends ImageView implements TextImage{

    final private String textString; // use this to work with text string
    final private int textStartOffset;
    final private int textEndOffset;
    final private double fullHeight;
    final private double fullWidth;

    public ImagePage(Text text, int offset) {
        /*
         *  @param Offset: starting position of the text.
         *  @param Text: Text element with text.
         */
        super();

        textString = text.getText();
        int textLength = textString.length();
        textStartOffset = offset;
        textEndOffset = textLength + offset;

        /* Remove ending newline symbol if available since all Text widgets
         * go vertically one by one and even if they don't have newlines at the end
         * visually they are displayed as if they do.
         */
        int end = textLength;
        if (textString.charAt(textLength - 1) == '\n')
            end -= 1;
        text.setText(textString.substring(0, end));


        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);
        this.setImage(text.snapshot(snapshotParameters, null));

        fullHeight = getImage().getHeight();
        fullWidth = getImage().getWidth();

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
        return textString;
    }
}

