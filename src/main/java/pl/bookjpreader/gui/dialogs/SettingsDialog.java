/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.dialogs;

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
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.actions.UpdateDisplayOptionsAction;
import pl.bookjpreader.gui.dialogs.settingsdialog.FontPane;
import pl.bookjpreader.gui.dialogs.settingsdialog.IndentationsPane;
import pl.bookjpreader.gui.dialogs.settingsdialog.QualityPane;
import pl.bookjpreader.gui.dialogs.settingsdialog.SettingsPane;

/**
 * This dialog is used to change user preferences like font, color, etc.
 */
public class SettingsDialog extends Stage{

    final private Map<String, Node> panels;

    public SettingsDialog(final Stage primaryStage, final DisplayOptions displayOptions) {
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

        final ProgramRegistry registry = ProgramRegistry.INSTANCE;

        panels = new HashMap<>();
        panels.put(registry.getResourceFor("SettingsDialog.FontPane.name"),
                new FontPane(displayOptions));
        panels.put(registry.getResourceFor("SettingsDialog.IndentationsPane.name"),
                new IndentationsPane(displayOptions));
        panels.put(registry.getResourceFor("SettingsDialog.QualityPane.name"),
                new QualityPane(displayOptions));
        //panels.put("Keys", selectedKeys);

        TreeItem<String> descriptions = new TreeItem<>(
                registry.getResourceFor("SettingsDialog.Settings.name"));
        descriptions.setExpanded(true);
        for (String item:panels.keySet())
            descriptions.getChildren().add(new TreeItem<>(item));

        final TreeView<String> settingsList = new TreeView<>(descriptions);
        mainContainer.setLeft(settingsList);

        settingsList.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldValue, newValue) -> {
                if (newValue != null && panels.containsKey(newValue.getValue()))
                    rightContainer.setCenter(panels.get(newValue.getValue()));
                else
                    rightContainer.getChildren().clear();
            });

        Button okButton = new Button(registry.getResourceFor("Dialog.OKButton.name"));
        okButton.setMinWidth(100);
        Button cancelButton = new Button(registry.getResourceFor("Dialog.CancelButton.name"));
        cancelButton.setMinWidth(100);
        buttonContainer.getChildren().addAll(okButton, cancelButton);

        okButton.setOnAction(ev -> onOKClicked(displayOptions));
        cancelButton.setOnAction(ev -> onCancelClicked());

        // Center dialog.
        setX(primaryStage.getX() + primaryStage.getWidth()/2 - dialogWidth/2);
        setY(primaryStage.getY() + primaryStage.getHeight()/2 - dialogHeight/2);
        }

    /**
     * Get all settings from panes and set new options.
     */
    private void onOKClicked(DisplayOptions displayOptions){
        for (Node pane:panels.values())
            ((SettingsPane)pane).applySettings();

        ProgramRegistry.INSTANCE.getForClass(UpdateDisplayOptionsAction.class)
                .fire(displayOptions);

        close();
    }
    private void onCancelClicked(){
        close();
    }
}