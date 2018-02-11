/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.MinorOptions;

/**
 * This action works if scroll speed is changed by user.
 */
public class SelectNewScrollSpeedAction extends BaseValueAction<Double, Double> {
    @Override
    Double execute(Double newValue) {
        ProgramRegistry.INSTANCE.getForClass(MinorOptions.class)
                .setScrollSpeed(newValue);
        return newValue;
    }
}
