/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui.textwidget;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ImagesContainer extends VBox {

    public ImagesContainer() {
        super();
        setLineSpacing(100.0);
    }
    public void setLineSpacing(double spacing){
        /*
         * This function sets distance between elements in the box.
         * It is needed if line spacing is changed for Text elements.
         * @param spacing: space between elements in the container in pixels.
         */
        this.setSpacing(spacing);
    }

    public void addBottomImage(TextImage ti){
        if (ti != null)
            getChildren().add((Node)ti);
    }
    public void addTopImage(TextImage ti){
        /*
         * Add top TextImage element and adapt position
         * to remain as it was.
         */
        if (ti != null){
            getChildren().add(0, (Node)ti);
            setTranslateY(getTranslateY() - ti.getImageHeight() - getSpacing());
        }
    }
    public void removeBottomImage(){
        int size = getChildren().size();
        if (size < 1)
            return;
        getChildren().remove(size-1);
    }
    public void removeTopImage(){
        /*
         * Remove first TextImage element and adapt position
         * to remain as it was.
         */
        if (getChildren().isEmpty())
            return;

        double newPos = getTranslateY() + getTopImageHeight();
        getChildren().remove(0);
        setTranslateY(newPos);
    }

    public double getTopImageHeight(){
        if (getChildren().isEmpty())
            return -1;

        TextImage ti = (TextImage)getChildren().get(0);
        return ti.getImageHeight() + getSpacing();
    }
    public double getBottomImageHeight(){
        int size = getChildren().size();

        if (size == 0)
            return -1;

        TextImage ti = (TextImage)getChildren().get(size - 1);
        return ti.getImageHeight() + getSpacing();
    }

    public double getContainerHeight(){
        double height = 0;
        for (Object ti: getChildren())
            height += ((TextImage)ti).getImageHeight() + getSpacing();
        height -= getSpacing(); // there is no spacing at the top and bottom
        return height;
    }

    public int getStartOffset(){
        if (getChildren().isEmpty())
            return -1;
        TextImage ti = (TextImage)getChildren().get(0);
        return ti.getStartOffsetPos();
    }
    public int getEndOffset(){
        int size = getChildren().size();
        if (size < 1)
            return -1;
        TextImage ti = (TextImage)getChildren().get(size - 1);
        return ti.getEndOffsetPos();
    }
    public int getVisibleStartOffset(){
        /*
         * This function returns position of the first appearing symbol.
         * It is not precise and position may differ from what is seen on the screen.
         * Is used to save cursor before exiting or changing the book.
         */
        if (getChildren().isEmpty())
            return -1;

        // Find the first Text element in visible zone.
        String text = "";
        double visibleHeight = 0;
        double height = 0;
        int offset = 0;

        // Find TextImage which contains first visible line.
        double h = getTranslateY();

        for (Object o: getChildren()){
            TextImage textImage = (TextImage)o;
            h += textImage.getImageHeight();
            if (h > 0){
                text = textImage.getImageText();
                offset = textImage.getStartOffsetPos();
                visibleHeight = h;
                height = textImage.getImageHeight();
                break;
            }
        }
        // Find an approximate position.
        int charPos = (int)((height - visibleHeight)/height*text.length());
        // Try to find the first space.
        int spacePos = text.lastIndexOf(" ", charPos);
        if (spacePos != -1)
            charPos = spacePos;

        return charPos + offset;
    }

    public void clear(){
        getChildren().clear();
        setTranslateY(0);
    }
    public String getText(){
        String s = "";
        for (Object o: getChildren()){
            TextImage ti = (TextImage)o;
            s += ti.getImageText();
        }
        return s;
    }

    @Override
    public String toString(){
        String output = " size:" + getChildren().size()
                + " height:" + this.getContainerHeight()
                + " posY:" + getTranslateY() + "; ";

        for (Object o: getChildren()){
            TextImage ti = (TextImage)o;
            output += String.format("start:%s, end:%s;",
                    ti.getStartOffsetPos(), ti.getEndOffsetPos());
        }
        return output;
    }
}
