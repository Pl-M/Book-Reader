/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.dialogs.settingsdialog;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.DisplayOptions;

/**
 * This is the pane to choose quality.
 */
public class QualityPane extends VBox implements SettingsPane{
    final private DisplayOptions displayOptions;
    final private Spinner<Integer> quality;

    public QualityPane(DisplayOptions displayOptions) {
        super();
        this.displayOptions = displayOptions;
        this.setSpacing(10);

        final ProgramRegistry registry = ProgramRegistry.INSTANCE;
        getChildren().add(new Label(registry.getResourceFor("QualityPane.description")));
        quality = new Spinner<>(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 2, displayOptions.getQuality(), 1));
        getChildren().add(quality);

    }
    @Override
    public void applySettings(){
        displayOptions.setQuality(quality.getValue());
    }
}
