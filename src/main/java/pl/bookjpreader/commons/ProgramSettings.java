/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*
 * This class contains all settings of the program.
 * It is a singleton.
 * Other parts of the program can get settings from this class,
 * swap settings through this class,
 * listen for updating.
 */


public class ProgramSettings {
    private static ProgramSettings _instance = null;

    public final BookShelf bookShelf;
    public final DisplayOptions displayOptions;
    public final MinorOptions minorOptions;
    public final ObjectProperty<KeyBindings> keyBindings;

    private final String SETTINGS_FILENAME = "brsettings.ini";
    private final Path SETTINGS_FILEPATH;
    private final String BOOKS_FILENAME = "brbooks.xml";
    private final Path BOOKS_FILEPATH;

    private ProgramSettings() {
        displayOptions = new DisplayOptions();
        minorOptions = new MinorOptions();

        keyBindings = new SimpleObjectProperty<>();
        keyBindings.set(new KeyBindings.Builder().build());

        bookShelf = new BookShelf();

        Path dir = getSettingsDir();

        if (dir != null){
            SETTINGS_FILEPATH = Paths.get(dir.toString(), SETTINGS_FILENAME);
            BOOKS_FILEPATH = Paths.get(dir.toString(), BOOKS_FILENAME);
        }
        else{
            SETTINGS_FILEPATH = null;
            BOOKS_FILEPATH = null;
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Settings error");
            alert.setContentText("There is an error in locating directory for settings file, "
                                + "settings won't be saved.");
            alert.showAndWait();
        }

        // Load settings.
        load();
    }
    public static synchronized ProgramSettings getInstance() {
        // Use this method to get instance of this class.
        if (_instance == null)
            _instance = new ProgramSettings();
        return _instance;
    }
    private Path getSettingsDir(){
        /* Get position of the running jar file
         * to place there settings file.
         */
        Path filePath = null;
        try {
            filePath = Paths.get(ProgramSettings.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (filePath != null)
            return filePath.getParent();
        else
            return null;
    }

    public void save(){
        /*
         * Save All settings to files.
         */
        if (SETTINGS_FILEPATH != null)
            SettingsFile.save(displayOptions, SETTINGS_FILEPATH);

        if (BOOKS_FILEPATH != null)
            BookShelfFile.save(bookShelf, BOOKS_FILEPATH);
    }
    private void load(){
        /*
         * Load settings from files.
         */
        if (SETTINGS_FILEPATH != null)
            SettingsFile.load(displayOptions, SETTINGS_FILEPATH);

        if (BOOKS_FILEPATH != null)
            BookShelfFile.load(bookShelf, BOOKS_FILEPATH);
    }
}
