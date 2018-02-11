/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.gui;

import javafx.scene.Node;

public interface ViewController <T extends Node> {
    T getView();
}
