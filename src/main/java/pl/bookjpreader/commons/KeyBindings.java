/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class KeyBindings {
    final public KeyCodeCombination fullScreenCombination;
    final public KeyCodeCombination enterEditMode;
    final public KeyCodeCombination gotoBegin;
    final public KeyCodeCombination gotoEnd;

    public KeyBindings(Builder builder) {
        fullScreenCombination = builder.fullScreenCombination;
        enterEditMode = builder.enterEditMode;
        gotoBegin = builder.gotoBegin;
        gotoEnd = builder.gotoEnd;
    }

    public static class Builder{
        private KeyCodeCombination fullScreenCombination =
                new KeyCodeCombination(KeyCode.F11);
        private KeyCodeCombination enterEditMode =
                new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        private KeyCodeCombination gotoBegin =
                new KeyCodeCombination(KeyCode.HOME, KeyCombination.CONTROL_DOWN);
        private KeyCodeCombination gotoEnd =
                new KeyCodeCombination(KeyCode.END, KeyCombination.CONTROL_DOWN);

        public void setFullScreen(KeyCodeCombination code){
            fullScreenCombination = code;
        }
        public void setEnterEditMode(KeyCodeCombination code){
            enterEditMode = code;
        }
        public void setGotoBegin(KeyCodeCombination code){
            gotoBegin = code;
        }
        public void setGotoEnd(KeyCodeCombination code){
            gotoEnd = code;
        }
        public KeyBindings build(){
            return new KeyBindings(this);
        }
    }
}
