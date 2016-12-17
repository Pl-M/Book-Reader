/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/*
 * This class contains options which don't require to reload graphic.
 *
 */

public class MinorOptions {

    final public DoubleProperty scrollSpeed;

    public MinorOptions() {
        scrollSpeed = new SimpleDoubleProperty();
        scrollSpeed.set(1);
    }
}
