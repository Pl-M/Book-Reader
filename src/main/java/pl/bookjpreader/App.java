/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

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
import javafx.stage.Screen;
import javafx.stage.Stage;
import pl.bookjpreader.commons.ProgramConfigurator;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.actions.UpdateDisplayOptionsAction;
import pl.bookjpreader.commons.items.actions.SaveAction;
import pl.bookjpreader.gui.bookview.BookViewController;
import pl.bookjpreader.gui.toppane.TopPane;
import pl.bookjpreader.gui.toppane.TopPaneController;


public class App extends Application {

    public static void main( String[] args ){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load and initialize settings.
        new ProgramConfigurator();
        final ProgramRegistry registry = ProgramRegistry.INSTANCE;

        primaryStage.setTitle("BookJP Reader");

        // Set initial position and dimensions.
        double initialWidth = 800;
        double initialHeight = 600;
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - initialWidth) / 2);
        primaryStage.setY((screenBounds.getHeight() - initialHeight) / 2);
        // Enter FullScreen mode on pressing F11.
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        // Save settings before exiting.
        primaryStage.setOnCloseRequest(e -> {
            registry.getForClass(SaveAction.class).fire();
            registry.dispose();
            Platform.exit();
        });

        final AnchorPane mainPane = new AnchorPane();
        configureMainPaneBackground(mainPane);

        final Scene primaryScene = new Scene(mainPane, initialWidth, initialHeight);
        primaryScene.getStylesheets().add("styles.css");

        primaryStage.setScene(primaryScene);

        final TopPaneController topPaneController =
                new TopPaneController(primaryStage);
        final BookViewController bookViewcontroller =
                new BookViewController();
        mainPane.getChildren().addAll(
                bookViewcontroller.getView(),
                topPaneController.getView());

        // Hide mouse cursor and top pane depending on the position of the cursor.
        primaryScene.setOnMouseMoved(ev -> {
            final boolean isInBottom = ev.getSceneY() > primaryScene.getHeight()*9/10;
            final TopPane pane = topPaneController.getView();

            if (isInBottom) { // Hide topPane and Mouse cursor.
                mainPane.getChildren().remove(pane);
                primaryScene.setCursor(Cursor.NONE);
            } else { // Show topPane and Mouse cursor.
                if (!mainPane.getChildren().contains(pane)) {
                    mainPane.getChildren().add(pane);
                }
                primaryScene.setCursor(Cursor.DEFAULT);
            }
        });

        primaryStage.show();
    }

    private void configureMainPaneBackground(final AnchorPane mainPane) {
        final DisplayOptions displayOptions = ProgramRegistry.INSTANCE
                .getForClass(DisplayOptions.class);

        mainPane.setBackground(new Background(new BackgroundFill(
                displayOptions.getBackgroundColor(), null, null)));

        ProgramRegistry.INSTANCE.getForClass(UpdateDisplayOptionsAction.class)
                .addListenerAfter(opts -> mainPane.setBackground(
                        new Background(new BackgroundFill(
                                opts.getBackgroundColor(), null, null))));
    }
}
