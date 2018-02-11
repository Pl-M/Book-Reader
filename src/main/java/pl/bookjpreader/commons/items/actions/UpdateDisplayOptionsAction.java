/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.items.DisplayOptions;

public class UpdateDisplayOptionsAction
        extends BaseValueAction<DisplayOptions, DisplayOptions> {
    @Override
    DisplayOptions execute(DisplayOptions newValue) {
        return newValue;
    }
}
