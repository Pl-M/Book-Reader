/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.filesys.FileHandler;

public class LoadAction extends BaseSimpleAction {
    @Override
    void execute() {
        ProgramRegistry.INSTANCE.getForSuperClass(FileHandler.class)
                .forEach(FileHandler::load);
    }
}
