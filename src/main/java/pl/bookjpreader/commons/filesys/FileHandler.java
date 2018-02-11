/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.filesys;

import pl.bookjpreader.commons.items.RegistryElement;


public interface FileHandler extends RegistryElement {
    void save();
    void load();
}
