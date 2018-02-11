/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.gui.bookview;


class TextImageParametersBuilder {
    public enum Direction{
        PREVIOUS, NEXT, END, BEGIN
    }
    private int startOffset;
    private Direction direction = Direction.NEXT;
    private double height;
    private double width;
    private double lineSpacing;


    double getLineSpacing() {
        return lineSpacing;
    }
    TextImageParametersBuilder setLineSpacing(final double lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    int getStartOffset() {
        return startOffset;
    }
    TextImageParametersBuilder setStartOffset(int startOffset) {
        this.startOffset = startOffset;
        return this;
    }

    Direction getDirection() {
        return direction;
    }
    TextImageParametersBuilder setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    double getHeight() {
        return height;
    }
    TextImageParametersBuilder setHeight(double height) {
        this.height = height;
        return this;
    }

    double getWidth() {
        return width;
    }
    TextImageParametersBuilder setWidth(double width) {
        this.width = width;
        return this;
    }
}
