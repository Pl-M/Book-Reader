/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.dialogs.settingsdialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.DisplayOptions;

/**
 * This is the pane to choose font for the text.
 */
public class FontPane extends GridPane implements SettingsPane{
    private final DisplayOptions displayOptions;
    private final Text sampleText;
    private final ColorPicker fontColor;
    private final ColorPicker backgroundColor;
    private final Spinner<Integer> fontSize;
    private final ComboBox<String> font;
    private ImageView sampleImage;

    public FontPane(DisplayOptions displayOptions) {
        super();
        this.displayOptions = displayOptions;
        setHgap(10);
        setVgap(10);

        final ProgramRegistry registry = ProgramRegistry.INSTANCE;

        add(new Label(registry.getResourceFor("FontPane.Color.name")), 0, 0); // column, row
        fontColor = new ColorPicker(displayOptions.getTextColor());
        add(fontColor, 1, 0);
        fontColor.setOnAction( ev -> updateText());
        fontColor.setPrefWidth(150);

        add(new Label(registry.getResourceFor("FontPane.BackgroundColor.name")), 0, 1); // column, row
        backgroundColor = new ColorPicker(displayOptions.getBackgroundColor());
        add(backgroundColor, 1, 1);
        backgroundColor.setOnAction( ev -> updateText());
        backgroundColor.setPrefWidth(150);

        ObservableList<String> availableFonts = FXCollections.observableArrayList(javafx.scene.text.Font.getFamilies());
        font = new ComboBox<>(availableFonts);
        font.getSelectionModel().select(displayOptions.getTextFont().getFamily());

        font.setPrefWidth(150);
        add(font, 0, 2);
        font.setOnAction(ev -> updateText());

        fontSize = new Spinner<>();
        fontSize.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(20, 100, (int)displayOptions.getTextFont().getSize(), 2));
        fontSize.setPrefWidth(150);
        fontSize.setEditable(false);
        add(fontSize, 1, 2);
        fontSize.valueProperty().addListener(ev -> updateText());

        sampleImage = null;
        sampleText = new Text();
        sampleText.setText("Text");
        updateText();

    }
    private void updateText(){
        sampleText.setFont(new Font(font.getValue(), fontSize.getValue()));
        sampleText.setFill(fontColor.getValue());

        if (sampleImage != null) {
            getChildren().remove(sampleImage);
        }
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(backgroundColor.getValue());
        sampleImage = new ImageView(sampleText.snapshot(snapshotParameters, null));
        add(sampleImage, 0, 3, 2, 2);
        setHalignment(sampleImage, HPos.CENTER);
    }
    @Override
    public void applySettings(){
        displayOptions
            .setTextColor(fontColor.getValue())
            .setTextFont(new Font(font.getValue(), fontSize.getValue()))
            .setBackgroundColor(backgroundColor.getValue());
    }
}
