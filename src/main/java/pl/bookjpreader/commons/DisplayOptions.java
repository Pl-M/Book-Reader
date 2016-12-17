/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/* This class contains settings needed to display text like Fonts, Colors etc.
 * The feature of these settings is that their change require
 * rebuilding the main scene.
 */

public class DisplayOptions {
    /*
     * @param horizontalBorder: indentations from left and right sides
     * of the window in percents;
     * @param verticalBorder: indentations from top and bottom sides
     * of the window in percents;
     * @param property: use this parameter only to listen for changes,
     * this signal could be triggered by using applyChanges() function.
     */
    final public BooleanProperty property;
    private Color textColor, backgroundColor;
    private Font textFont;
    private double horIndentation, vertIndentation;
    private double lineSpacing;
    private int quality;

    public DisplayOptions() {
        // Define here default options.
        property = new SimpleBooleanProperty();
        textColor = Color.GREY;
        backgroundColor = Color.BLACK;

        textFont = Font.font("Comic Sans MS", 48);
        horIndentation = 0;
        vertIndentation = 0;
        lineSpacing = 0;
        quality = 1;
    }
    public DisplayOptions setTextColor(Color textColor){
        this.textColor = textColor;
        return this;
    }
    public Color getTextColor(){
        return this.textColor;
    }
    public DisplayOptions setBackgroundColor(Color backgroundColor){
        this.backgroundColor = backgroundColor;
        return this;
    }
    public Color getBackgroundColor(){
        return this.backgroundColor;
    }
    public DisplayOptions setTextFont(Font textFont){
        this.textFont = textFont;
        return this;
    }
    public Font getTextFont(){
        return this.textFont;
    }
    public DisplayOptions setHIndentation(double hInd){
        if (hInd < 1 && hInd >= 0)
            this.horIndentation = hInd;
        return this;
    }
    public double getHIndentation(){
        return this.horIndentation;
    }
    public DisplayOptions setVIndentation(double vInd){
        if (vInd < 1 && vInd >= 0)
            this.vertIndentation = vInd;
        return this;
    }
    public double getVIndentation(){
        return this.vertIndentation;
    }
    public void setLineSpacing(double spacing){
        /*@param spacing: distance between lines in heights of the line;
         * e.g. if spacing == 1 -> distance is one line of text.
         */
        if (spacing < 0)
            spacing = 0.0;
        else if (spacing > 1)
            spacing = 1.0;

        this.lineSpacing = spacing;
    }
    public double getLineSpacing(){
        return this.lineSpacing;
    }
    public void setQuality(int quality){
        if (quality > 2)
            quality = 2;
        else if (quality < 0)
            quality = 0;

        this.quality = quality;
    }
    public int getQuality(){
        return quality;
    }
    public void applyChanges(){
        /* Use this method to inform all listeners of the property
         * that display options were changed.
         * It is useful to apply changes not after each element changes but
         * after a number of successive changes.
         */
        property.set(!property.get());
    }
}
