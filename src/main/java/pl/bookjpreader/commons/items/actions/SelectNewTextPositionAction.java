/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.CurrentBook;

/**
 * This action works if text position is manually changed by user
 * (e.g. by choosing percent value in the dialog).
 */
public class SelectNewTextPositionAction extends BaseValueAction<Double, Double> {
    @Override
    Double execute(Double newValue) {
        ProgramRegistry.INSTANCE.getForClass(CurrentBook.class)
                .setBookPosition(newValue);
        return newValue;
    }
}
