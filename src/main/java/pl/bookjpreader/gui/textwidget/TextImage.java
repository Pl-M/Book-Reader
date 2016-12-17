/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui.textwidget;

/*
 * This interface defines functions which are used by ImagesContainer.
 * Each Node added to it should implement this interface.
 */
public interface TextImage {

    public int getStartOffsetPos();
    public int getEndOffsetPos();
    public double getImageHeight();
    public double getImageWidth();
    public String getImageText();
}
