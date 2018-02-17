/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

import pl.bookjpreader.commons.filesys.BookShelfFileHandler;
import pl.bookjpreader.commons.filesys.SettingsFileHandler;
import pl.bookjpreader.commons.items.BookShelf;
import pl.bookjpreader.commons.items.CurrentBook;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.MinorOptions;
import pl.bookjpreader.commons.items.actions.LoadAction;
import pl.bookjpreader.commons.items.actions.OpenNewBookAction;
import pl.bookjpreader.commons.items.actions.SaveAction;
import pl.bookjpreader.commons.items.actions.SelectNewScrollSpeedAction;
import pl.bookjpreader.commons.items.actions.SelectNewTextPositionAction;
import pl.bookjpreader.commons.items.actions.StopAnimationAction;
import pl.bookjpreader.commons.items.actions.UpdateDisplayOptionsAction;
import pl.bookjpreader.commons.items.actions.UpdateTextPositionAction;


/**
 * This class started in the beginning of the program and initializes registry and
 * all settings.
 * Other parts of the program can get settings from this class.
 */
public class ProgramConfigurator {
    private static String I18N_RESOURCE = "i18n.messages";

    public ProgramConfigurator() {
        final ProgramRegistry pr = ProgramRegistry.INSTANCE;

        // Book elements.
        pr.register(new BookShelf());
        pr.register(new CurrentBook());

        // Options
        pr.register(new DisplayOptions());
        pr.register(new MinorOptions());

        // Other
        pr.register(new ThreadUtils());

        // FileHandlers
        setFileHandlers(pr);

        // Actions
        pr.register(new SaveAction());
        pr.register(new LoadAction());
        pr.register(new OpenNewBookAction());
        pr.register(new SelectNewTextPositionAction());
        pr.register(new UpdateTextPositionAction());
        pr.register(new UpdateDisplayOptionsAction());
        pr.register(new SelectNewScrollSpeedAction());
        pr.register(new StopAnimationAction());

        // Set error handler
        setErrorHandler();

        // Localization.
        ResourceBundle messages =
                ResourceBundle.getBundle(I18N_RESOURCE, Locale.getDefault());
        pr.registerResource(messages);


        // Load settings.
        pr.submitAction(LoadAction.class);

    }

    private void setErrorHandler() {
        ErrorHandler.setErrorHandler((message, throwable) -> {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error dialog");

            alert.setHeaderText(message);
            alert.setContentText(throwable.getClass().getName());

            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            final String stackTraceText = sw.toString();

            final TextArea stackTraceNode = new TextArea(stackTraceText);
            alert.getDialogPane().setExpandableContent(stackTraceNode);
            alert.showAndWait();
        });
    }

    private void setFileHandlers(ProgramRegistry pr){
        Path dir = getSettingsDir();

        Path settingsPath;
        Path booksPath;

        if (dir != null){
            settingsPath = Paths.get(dir.toString(), FileNames.SETTINGS.toString());
            booksPath = Paths.get(dir.toString(), FileNames.BOOKS.toString());
        }
        else {
            settingsPath = null;
            booksPath = null;
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Settings error");
            alert.setContentText("There is an error in locating directory for settings file, "
                    + "settings won't be saved.");
            alert.showAndWait();
        }

        pr.register(new SettingsFileHandler(settingsPath));
        pr.register(new BookShelfFileHandler(booksPath));


    }

    /**
     * Get position of the running jar file
     * to place there settings file.
     */
    private Path getSettingsDir(){
        Path filePath = null;
        try {
            filePath = Paths.get(ProgramConfigurator.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            ErrorHandler.handle(e);
        }

        if (filePath != null)
            return filePath.getParent();
        else
            return null;
    }

}

enum FileNames{
    SETTINGS("brsettings.ini"),
    BOOKS("brbooks.xml");

    private String name;

    FileNames(String name){
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
