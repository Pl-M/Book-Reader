/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.CurrentBook;

/**
 * This action works if text position is changed by the program (e.g. by scrolling).
 * So that other parts of the program can update values.
 */
public class UpdateTextPositionAction extends BaseValueAction<Double, Double> {
    @Override
    Double execute(Double newValue) {
        ProgramRegistry.INSTANCE.getForClass(CurrentBook.class)
                .setBookPosition(newValue);
        return newValue;
    }
}
