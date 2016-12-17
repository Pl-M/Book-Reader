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

public class IndentationsPane extends VBox implements SettingsPane{
    final private DisplayOptions displayOptions;
    final private Spinner<Integer> vertInd;
    final private Spinner<Integer> horInd;
    final private Spinner<Double> lineSpacing;

    public IndentationsPane(DisplayOptions displayOptions) {
        super();
        this.displayOptions = displayOptions;
        this.setSpacing(10);

        getChildren().add(new Label("Indentations from Top and Bottom, %"));
        int vertical = (int)(displayOptions.getVIndentation()*100);
        vertInd = new Spinner<>(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 25, vertical, 1));
        vertInd.setEditable(false);
        vertInd.setPrefWidth(150);
        getChildren().add(vertInd);

        getChildren().add(new Label("Indentations from Left and Right, %"));
        int horizontal = (int)(displayOptions.getHIndentation()*100);
        horInd = new Spinner<>(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 25, horizontal, 1));
        horInd.setEditable(false);
        horInd.setPrefWidth(150);
        getChildren().add(horInd);

        getChildren().add(new Label("Line spacing, lines")); // column, row
        lineSpacing = new Spinner<>(new SpinnerValueFactory
                .DoubleSpinnerValueFactory(0, 1, displayOptions.getLineSpacing(), 0.1));
        lineSpacing.setEditable(false);
        lineSpacing.setPrefWidth(150);
        getChildren().add(lineSpacing);

    }
    public void applySettings(){
        displayOptions
                .setHIndentation((double)horInd.getValue()/100)
                .setVIndentation((double)vertInd.getValue()/100)
                .setLineSpacing(lineSpacing.getValue());
    }
}
