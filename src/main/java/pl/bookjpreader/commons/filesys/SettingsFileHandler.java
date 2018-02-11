/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons.filesys;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pl.bookjpreader.commons.ErrorHandler;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.MinorOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * This class saves/loads program settings to/from a property file.
 */
public class SettingsFileHandler implements FileHandler{

    private final Path filePath;

    /**
     * @param filePath: where to save/ from where to load options.
     */
    public SettingsFileHandler(Path filePath) {
        this.filePath = filePath;
    }
    /**
     * Save settings to the file.
     */
    @Override
    public void save(){
        if (filePath == null){
            return;
        }

        DisplayOptions dOpts = ProgramRegistry.INSTANCE.getForClass(DisplayOptions.class);
        MinorOptions mOpts = ProgramRegistry.INSTANCE.getForClass(MinorOptions.class);

        Properties fileProperties = new Properties();
        fileProperties.setProperty(Headers.TEXT_FONT.toString(),
                dOpts.getTextFont().getFamily());
        fileProperties.setProperty(Headers.FONT_SIZE.toString(),
                String.valueOf(dOpts.getTextFont().getSize()));
        fileProperties.setProperty(Headers.TEXT_COLOR.toString(),
                colorToString(dOpts.getTextColor()));
        fileProperties.setProperty(Headers.BACKGROUND_COLOR.toString(),
                colorToString(dOpts.getBackgroundColor()));
        fileProperties.setProperty(Headers.VERTICAL_INDENT.toString(),
                String.valueOf(dOpts.getVIndentation()));
        fileProperties.setProperty(Headers.HORIZONTAL_INDENT.toString(),
                String.valueOf(dOpts.getHIndentation()));
        fileProperties.setProperty(Headers.LINE_SPACING.toString(),
                String.valueOf(dOpts.getLineSpacing()));
        fileProperties.setProperty(Headers.QUALITY.toString(),
                String.valueOf(dOpts.getQuality()));
        fileProperties.setProperty(Headers.SCROLL_SPEED.toString(),
                String.valueOf(mOpts.getScrollSpeed()));

        try{
            fileProperties.store(Files.newOutputStream(filePath), "BOOKREADER SETTINGS");
        }
        catch (IOException e){
            ErrorHandler.handle("Error while saving settings file", e);
        }
    }
    /**
     * Load settings from file.
     */
    @Override
    public void load(){
        if (filePath == null){
            // Use default options.
            return;
        }

        Properties fileProperties = new Properties();
        try{
            fileProperties.load(Files.newInputStream(filePath));
        }
        catch (IOException e){
            ErrorHandler.handle("Error while reading settings file.", e);
            // Use default options.
            return;
        }

        // Load display options.
        DisplayOptions dOpts = ProgramRegistry.INSTANCE.getForClass(DisplayOptions.class);
        MinorOptions mOpts = ProgramRegistry.INSTANCE.getForClass(MinorOptions.class);

        String property;
        property = fileProperties.getProperty(Headers.SCROLL_SPEED.toString());
        if (property != null)
            mOpts.setScrollSpeed(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.HORIZONTAL_INDENT.toString());
        if (property != null)
            dOpts.setHIndentation(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.QUALITY.toString());
        if (property != null)
            dOpts.setQuality(Integer.valueOf(property));

        property = fileProperties.getProperty(Headers.VERTICAL_INDENT.toString());
        if (property != null)
            dOpts.setVIndentation(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.LINE_SPACING.toString());
        if (property != null)
            dOpts.setLineSpacing(Double.valueOf(property));

        property = fileProperties.getProperty(Headers.BACKGROUND_COLOR.toString());
        if (property != null)
            dOpts.setBackgroundColor(Color.valueOf(property));

        property = fileProperties.getProperty(Headers.TEXT_COLOR.toString());
        if (property != null)
            dOpts.setTextColor(Color.valueOf(property));

        String propertyFont = fileProperties.getProperty(Headers.TEXT_FONT.toString());
        String propertyFontSize = fileProperties.getProperty(Headers.FONT_SIZE.toString());
        if (propertyFont != null && propertyFontSize != null)
            dOpts.setTextFont(new Font(propertyFont, Double.valueOf(propertyFontSize)));

    }
    /**
     * Convert Color to string format to make it possible to
     * convert it back to Color later.
     */
    private String colorToString(Color color){
        return String.format( "rgb(%s,%s,%s)",
            (int)(color.getRed() * 255 ),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255 ));
    }

    enum Headers {
         // This values corresponds to headers in settings file.
        CURRENT_BOOK("CURRENT BOOK"),
        CURRENT_BOOK_POSITION("CURRENT BOOK POSITION"),
        TEXT_FONT("TEXT FONT"),
        FONT_SIZE("FONT SIZE"),
        TEXT_COLOR("TEXT COLOR"),
        BACKGROUND_COLOR ("BACKGROUND COLOR"),
        VERTICAL_INDENT ("VERTICAL INDENTATION"),
        HORIZONTAL_INDENT ("HORIZONTAL INDENTATION"),
        LINE_SPACING ("LINE SPACING"),
        QUALITY ("QUALITY"),
        SCROLL_SPEED ("SCROLL SPEED");

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

