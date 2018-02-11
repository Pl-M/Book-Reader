/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons.items;

/**
 * This class contains options which don't require to reload graphic.
 */
public class MinorOptions implements RegistryElement {
    private double scrollSpeed = 1;

    public MinorOptions setScrollSpeed(double speed){
        if (speed > 10 || speed < 0.1)
            speed = 1;
        scrollSpeed = speed;
        return this;
    }
    public double getScrollSpeed(){
        return scrollSpeed;
    }
}
