/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/*
 * This class saves/loads program settings to/from a property file.
 */

public class SettingsFile {

    private final Properties fileProperties;
    private final DisplayOptions displayOptions;
    private final Path filePath;

    private SettingsFile(DisplayOptions displayOptions, Path filePath) {
        /*
         * @param displayOptions: options to save/load; this class
         * doesn't return a new value it changes displayOptions;
         * @param filePath: where to save/ from where to load options.
         */
        this.displayOptions = displayOptions;
        this.filePath = filePath;
        fileProperties = new Properties();
    }
    public static void load(DisplayOptions displayOptions, Path filePath){
        SettingsFile file = new SettingsFile(displayOptions, filePath);
        file.load();
    }
    public static void save(DisplayOptions displayOptions, Path filePath){
        SettingsFile file = new SettingsFile(displayOptions, filePath);
        file.save();
    }

    private void save(){
        /*
         * Save settings to the file.
         */

        fileProperties.setProperty(Headers.TEXT_FONT.toString(),
                displayOptions.getTextFont().getFamily().toString());
        fileProperties.setProperty(Headers.FONT_SIZE.toString(),
                String.valueOf(displayOptions.getTextFont().getSize()));
        fileProperties.setProperty(Headers.TEXT_COLOR.toString(),
                colorToString(displayOptions.getTextColor()));
        fileProperties.setProperty(Headers.BACKGROUND_COLOR.toString(),
                colorToString(displayOptions.getBackgroundColor()));
        fileProperties.setProperty(Headers.VERTICAL_INDENT.toString(),
                String.valueOf(displayOptions.getVIndentation()));
        fileProperties.setProperty(Headers.HORIZONTAL_INDENT.toString(),
                String.valueOf(displayOptions.getHIndentation()));
        fileProperties.setProperty(Headers.LINE_SPACING.toString(),
                String.valueOf(displayOptions.getLineSpacing()));

        try{
            fileProperties.store(Files.newOutputStream(filePath), "BOOKREADER SETTINGS");
        }
        catch (IOException e){
            System.out.println("Error while saving settings file");
        }
    }

    private void load(){
        /*
         * Load settings from file.
         */
        if (filePath == null){
            // Use default options.
            return;
        }

        try{
            fileProperties.load(Files.newInputStream(filePath));
        }
        catch (IOException e){
            System.out.println("Error while reading settings file. " + e);
            // Use default options.
            return;
        }

        // Load display options.
        String property;
        property = fileProperties.getProperty(Headers.HORIZONTAL_INDENT.toString());
        if (property != null)
            displayOptions.setHIndentation(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.VERTICAL_INDENT.toString());
        if (property != null)
            displayOptions.setVIndentation(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.LINE_SPACING.toString());
        if (property != null)
            displayOptions.setLineSpacing(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.BACKGROUND_COLOR.toString());
        if (property != null)
            displayOptions.setBackgroundColor(Color.valueOf(property));

        property = fileProperties.getProperty(Headers.TEXT_COLOR.toString());
        if (property != null)
            displayOptions.setTextColor(Color.valueOf(property));

        String propertyFont = fileProperties.getProperty(Headers.TEXT_FONT.toString());
        String propertyFontSize = fileProperties.getProperty(Headers.FONT_SIZE.toString());
        if (propertyFont != null && propertyFontSize != null)
            displayOptions.setTextFont(new Font(propertyFont, Double.valueOf(propertyFontSize)));

    }
    private String colorToString(Color color){
        /* Convert Color to string format to make it possible to
         * convert it back to Color later.
         */
        return String.format( "rgb(%s,%s,%s)",
            (int)(color.getRed() * 255 ),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255 ));
    }

    enum Headers {
        /*
         * This values corresponds to headers in settings file.
         */
        CURRENT_BOOK("CURRENT BOOK"),
        CURRENT_BOOK_POSITION("CURRENT BOOK POSITION"),
        TEXT_FONT("TEXT FONT"),
        FONT_SIZE("FONT SIZE"),
        TEXT_COLOR("TEXT COLOR"),
        BACKGROUND_COLOR ("BACKGROUND COLOR"),
        VERTICAL_INDENT ("VERTICAL INDENTATION"),
        HORIZONTAL_INDENT ("HORIZONTAL INDENTATION"),
        LINE_SPACING ("LINE SPACING");
        private final String name;

        Headers(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }
}

