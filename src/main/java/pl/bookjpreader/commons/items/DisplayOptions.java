/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons.items;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * This class contains settings needed to display text like Fonts, Colors etc.
 * The feature of these settings is that their change require
 * rebuilding the main scene.
 */
public class DisplayOptions implements RegistryElement {
    private Color textColor = Color.GREY;
    private Color backgroundColor  = Color.BLACK;
    private Font textFont = Font.font("Comic Sans MS", 48);
    /**
     * Indentations from left and right sides of the window in percents.
     */
    private double horIndentation = 0;
    /**
     * Indentations from top and bottom sides of the window in percents.
     */
    private double vertIndentation = 0;
    private double lineSpacing = 0;
    private int quality = 1;

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
    /**
     * @param spacing distance between lines in heights of the line;
     * e.g. if {@code spacing == 1} then distance is one line of text.
     */
    public void setLineSpacing(double spacing){
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

}
