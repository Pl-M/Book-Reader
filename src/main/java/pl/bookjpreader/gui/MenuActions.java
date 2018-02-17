/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.gui;


import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import pl.bookjpreader.booksfactory.BookEntity;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.BookShelf;
import pl.bookjpreader.commons.items.CurrentBook;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.actions.SelectNewTextPositionAction;
import pl.bookjpreader.commons.items.actions.OpenNewBookAction;
import pl.bookjpreader.commons.items.actions.StopAnimationAction;
import pl.bookjpreader.gui.dialogs.BookShelfDialog;
import pl.bookjpreader.gui.dialogs.SettingsDialog;
import pl.bookjpreader.gui.simplewidgets.BookChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public enum MenuActions {
    OPEN_FILE {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.OPEN_FILE.name");
        }

        @Override
        protected KeyCodeCombination getAccelerator() {
            return new KeyCodeCombination(KeyCode.O,
                    KeyCombination.CONTROL_DOWN);
        }

        /**
         * Selects a new file in the dialog and opens it.
         */
        @Override
        protected void execute(final Stage stage) {
            final ProgramRegistry registry = ProgramRegistry.INSTANCE;

            registry.submitAction(StopAnimationAction.class);

            BookChooser bookChooser = new BookChooser(stage);

            final File fileName = bookChooser.getFileName();
            if (fileName != null){
                final BookEntity book;
                if (bookChooser.getEncoding() == null) {
                    book = new BookEntity(fileName.toPath());
                } else {
                    book = new BookEntity(fileName.toPath(),
                            bookChooser.getEncoding());
                }
                registry.submitAction(OpenNewBookAction.class, book);
            }
        }
    },
    OPEN_BOOKSHELF {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.OPEN_BOOKSHELF.name");
        }
        @Override
        protected boolean addSeparator() {
            return true;
        }

        @Override
        protected KeyCodeCombination getAccelerator() {
            return new KeyCodeCombination(KeyCode.B,
                    KeyCombination.CONTROL_DOWN);
        }
        @Override
        protected void execute(final Stage stage) {
            final ProgramRegistry registry = ProgramRegistry.INSTANCE;

            registry.submitAction(StopAnimationAction.class);
            new BookShelfDialog(stage, registry.getForClass(
                    BookShelf.class)).showAndWait();
        }
    },
    SETTINGS {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.SETTINGS.name");
        }
        @Override
        protected void execute(final Stage stage) {
            final ProgramRegistry registry = ProgramRegistry.INSTANCE;

            registry.submitAction(StopAnimationAction.class);
            new SettingsDialog(stage, registry.getForClass(
                    DisplayOptions.class)).showAndWait();
        }
    },
    SEARCH_TEXT {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.SEARCH_TEXT.name");
        }

        @Override
        protected KeyCodeCombination getAccelerator() {
            return new KeyCodeCombination(KeyCode.F,
                    KeyCombination.CONTROL_DOWN);
        }

        @Override
        protected void execute(final Stage stage) {
            final ProgramRegistry registry = ProgramRegistry.INSTANCE;

            registry.submitAction(StopAnimationAction.class);

            final TextInputDialog dialog = new TextInputDialog();
            dialog.initOwner(stage);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle("Search Dialog");
            dialog.setHeaderText("Input text to search");

            dialog.showAndWait().ifPresent(pattern -> {
                if (pattern.length() < 3) return;

                Double foundPercentPos =
                        registry.getForClass(CurrentBook.class).searchText(pattern);

                if (foundPercentPos == null){
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.initOwner(stage);
                    info.setTitle("Nothing was found");
                    info.initStyle(StageStyle.UTILITY);

                    info.setContentText("Nothing was found");
                    info.showAndWait();
                } else{
                    registry.submitAction(SelectNewTextPositionAction.class,
                            foundPercentPos);
                }
            });
        }
    },
    FULLSCREEN {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.FULLSCREEN.name");
        }
        @Override
        protected MenuItem getView(Stage stage) {
            final CheckMenuItem menuItem = new CheckMenuItem(toString());
            menuItem.setOnAction(ev -> execute(stage));
            menuItem.setAccelerator(getAccelerator());
            stage.fullScreenProperty().addListener((obs, oldValue, newValue) ->
                    menuItem.setSelected(newValue));
            return menuItem;
        }
        @Override
        protected boolean addSeparator() {
            return true;
        }
        @Override
        protected KeyCodeCombination getAccelerator() {
            return new KeyCodeCombination(KeyCode.F11);
        }
        @Override
        protected void execute(final Stage stage) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    },
    CLOSE_PROGRAM {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.CLOSE_PROGRAM.name");
        }
        @Override
        protected KeyCodeCombination getAccelerator() {
            return new KeyCodeCombination(KeyCode.X,
                    KeyCombination.CONTROL_DOWN);
        }
        @Override
        protected void execute(final Stage stage) {
            /*
             * Fire a closing event so that event handler can
             * catch it and save settings.
             */
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    },
    ABOUT {
        @Override
        public String toString() {
            return ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.ABOUT.name");
        }

        @Override
        protected void execute(final Stage stage) {
            ProgramRegistry.INSTANCE.submitAction(StopAnimationAction.class);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.initOwner(stage);
            info.setTitle(ProgramRegistry.INSTANCE
                    .getResourceFor("MenuAction.ABOUT.name"));
            info.initStyle(StageStyle.UTILITY);

            info.setContentText(
                    ProgramRegistry.INSTANCE
                            .getResourceFor("MenuAction.ABOUT.description") +
                    "\n\u00a9" + "Pavel_M-v, 2016-2018"
            );
            info.showAndWait();
        }
    };

    public static List<MenuItem> getMenuItems(Stage stage) {
        List<MenuItem> menuItems = new ArrayList<>();

        for (MenuActions action :values()){
            menuItems.add(action.getView(stage));
            if (action.addSeparator()) {
                menuItems.add(new SeparatorMenuItem());
            }
        }
        return menuItems;
    }

    protected MenuItem getView (Stage stage) {
        final MenuItem menuItem = new MenuItem(toString());
        menuItem.setOnAction(ev -> execute(stage));
        menuItem.setAccelerator(getAccelerator());
        return menuItem;
    }

    protected boolean addSeparator() {
        return false;
    }

    protected KeyCodeCombination getAccelerator() {
        return null;
    }

    protected abstract void execute(Stage stage);
}
