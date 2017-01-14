/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/*
 * This class contains options which don't require to reload graphic.
 *
 */

public class MinorOptions {
    final public BooleanProperty scrollSpeedProperty;

    private double scrollSpeed;

    public MinorOptions() {
        scrollSpeedProperty = new SimpleBooleanProperty();
        scrollSpeed = 1; // default value
    }
    public void setScrollSpeed(double speed){
        if (speed > 10 || speed < 0.1)
            speed = 1;
        scrollSpeed = speed;

        // Inform listeners that value was changed.
        scrollSpeedProperty.set(!scrollSpeedProperty.get());
    }
    public double getScrollSpeed(){
        return scrollSpeed;
    }
}
