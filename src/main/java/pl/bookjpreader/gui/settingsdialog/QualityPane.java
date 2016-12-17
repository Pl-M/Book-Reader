/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui.settingsdialog;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import pl.bookjpreader.commons.DisplayOptions;

/*
 * This is the pane to choose borders from both sides.
 */

public class QualityPane extends VBox implements SettingsPane{
    final private DisplayOptions displayOptions;
    final private Spinner<Integer> quality;


    public QualityPane(DisplayOptions displayOptions) {
        super();
        this.displayOptions = displayOptions;
        this.setSpacing(10);

        getChildren().add(new Label("Set quality (0 - low, 2 - high)"));
        quality = new Spinner<>(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 2, displayOptions.getQuality(), 1));
        getChildren().add(quality);

    }
    public void applySettings(){
        displayOptions.setQuality((int)quality.getValue());
    }
}
