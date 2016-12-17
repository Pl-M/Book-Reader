/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.bookjpreader.commons.DisplayOptions;
import pl.bookjpreader.gui.settingsdialog.FontPane;
import pl.bookjpreader.gui.settingsdialog.IndentationsPane;
import pl.bookjpreader.gui.settingsdialog.QualityPane;
import pl.bookjpreader.gui.settingsdialog.SettingsPane;

/*
 * The dialog to change user preferences like font, color, etc.
 */

public class SettingsDialog extends Stage{
    final private TreeView<String> settingsList;

    final private Map<String, Node> panels;
    final private DisplayOptions displayOptions;

    final private FontPane selectFontPane;
    final private IndentationsPane selectIndPane;
    final private QualityPane qualityPane;

    public SettingsDialog(Stage primaryStage, DisplayOptions displayOptions) {
        this.displayOptions = displayOptions;

        initOwner(primaryStage);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.WINDOW_MODAL);
        setResizable(false);

        BorderPane mainContainer = new BorderPane();

        // Width and Height of this dialog.
        int dialogWidth = 600;
        int dialogHeight = 400;

        setScene(new Scene(mainContainer, dialogWidth, dialogHeight));

        BorderPane rightContainer = new BorderPane();
        rightContainer.setPadding(new Insets(10, 10, 10, 10));
        mainContainer.setCenter(rightContainer);

        HBox buttonContainer = new HBox(50);
        buttonContainer.setPadding(new Insets(5, 5, 5, 5));
        buttonContainer.setAlignment(Pos.CENTER);
        mainContainer.setBottom(buttonContainer);

        selectFontPane = new FontPane(displayOptions);
        selectIndPane = new IndentationsPane(displayOptions);
        qualityPane = new QualityPane(displayOptions);

        panels = new HashMap<>();
        panels.put("Font", selectFontPane);
        panels.put("Indentations", selectIndPane);
        panels.put("Quality", qualityPane);
        //panels.put("Keys", selectedKeys);

        TreeItem<String> descriptions = new TreeItem<String>("General Settings");
        descriptions.setExpanded(true);
        for (String item:panels.keySet())
            descriptions.getChildren().add(new TreeItem<String>(item));

        settingsList = new TreeView<String>(descriptions);
        mainContainer.setLeft(settingsList);

        settingsList.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldValue, newValue) -> {
                if (newValue != null && panels.containsKey(newValue.getValue()))
                    rightContainer.setCenter(panels.get(newValue.getValue()));
                else
                    rightContainer.getChildren().clear();
            });

        Button okButton = new Button("OK");
        okButton.setMinWidth(100);
        Button cancelButton = new Button("Cancel");
        cancelButton.setMinWidth(100);
        buttonContainer.getChildren().addAll(okButton, cancelButton);

        okButton.setOnAction(ev -> onOKClicked());
        cancelButton.setOnAction(ev -> onCancelClicked());

        // Center dialog.
        setX(primaryStage.getX() + primaryStage.getWidth()/2 - dialogWidth/2);
        setY(primaryStage.getY() + primaryStage.getHeight()/2 - dialogHeight/2);
        }

    private void onOKClicked(){
        /*
         * Get all settings from panes and set new options.
         */
        for (Node pane:panels.values())
            ((SettingsPane)pane).applySettings();

        // Update displaySettings.
        displayOptions.applyChanges();

        close();
    }
    private void onCancelClicked(){
        close();
    }
}