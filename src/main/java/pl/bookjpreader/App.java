/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/

package pl.bookjpreader;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pl.bookjpreader.commons.ProgramSettings;
import pl.bookjpreader.gui.BookView;
import pl.bookjpreader.gui.TopPane;


public class App extends Application {
    ProgramSettings settings;
    Scene primaryScene;
    AnchorPane mainBorder;
    TopPane topPane;
    BookView bookWidget;

    public static void main( String[] args ){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load and initialize settings.
        settings = ProgramSettings.getInstance();

        primaryStage.setTitle("BookJP Reader");

        mainBorder = new AnchorPane();
        mainBorder.setBackground(new Background(new BackgroundFill(
                Color.BLACK, null, null)));

        // Set initial position and dimensions.
        double initialWidth = 800;
        double initialHeight = 600;
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - initialWidth) / 2);
        primaryStage.setY((screenBounds.getHeight() - initialHeight) / 2);

        primaryScene = new Scene(mainBorder, initialWidth, initialHeight);
        primaryStage.setScene(primaryScene);
        primaryStage.show();

        topPane = new TopPane(settings, primaryStage);
        bookWidget = new BookView(settings);

        mainBorder.getChildren().addAll(bookWidget.getPane(), topPane);
        // Allow children to grow and set their position.
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setLeftAnchor(topPane, 0.0);
        AnchorPane.setRightAnchor(topPane, 0.0);

        AnchorPane.setTopAnchor(bookWidget.getPane(), 0.0);
        AnchorPane.setLeftAnchor(bookWidget.getPane(), 0.0);
        AnchorPane.setRightAnchor(bookWidget.getPane(), 0.0);
        AnchorPane.setBottomAnchor(bookWidget.getPane(), 0.0);


        // Enter fullscreen mode on pressing F11.
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        // Hide mouse cursor and top pane depending on the mode and position
        // of the cursor.
        primaryScene.setOnMouseMoved(ev -> {
            boolean isInBottom = ev.getSceneY() > primaryScene.getHeight()*9/10;
            hideTopPane(bookWidget.editMode.get() || isInBottom);
            hideMouseCursor(!bookWidget.editMode.get() && isInBottom);
        });
        bookWidget.editMode.addListener(ev -> {
            hideTopPane(bookWidget.editMode.get());
            topPane.disableShortcuts(bookWidget.editMode.get());
            hideMouseCursor(false);
        });

        // Change background.
        settings.displayOptions.property.addListener(ev -> onBackgroundChange());
        // Save settings before exiting.
        primaryStage.setOnCloseRequest(e -> {
            settings.save();
            Platform.exit();
            });
        onBackgroundChange();
    }
    public void onBackgroundChange(){
        mainBorder.setBackground(new Background(new BackgroundFill(
                settings.displayOptions.getBackgroundColor(), null, null)));
    }
    public void hideTopPane(boolean doHide){
        if (doHide){
            // Hide topPane.
            mainBorder.getChildren().remove(topPane);
            primaryScene.setCursor(Cursor.NONE);
        }
        else{
            // Show topPane.
            if (!mainBorder.getChildren().contains(topPane))
                mainBorder.getChildren().add(topPane);
            primaryScene.setCursor(Cursor.DEFAULT);
        }
    }
    public void hideMouseCursor(boolean doHide){
        if (doHide){
            // Hide Mouse cursor.
            primaryScene.setCursor(Cursor.NONE);
        }
        else{
            // Show Mouse cursor.
            primaryScene.setCursor(Cursor.DEFAULT);
        }
    }
}
