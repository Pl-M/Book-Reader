/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pl.bookjpreader.commons.ProgramConfigurator;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.actions.UpdateDisplayOptionsAction;
import pl.bookjpreader.commons.items.actions.SaveAction;
import pl.bookjpreader.gui.bookview.BookView;
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

        // Set initial position and size.
        final double initialWidth = 800;
        final double initialHeight = 600;

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - initialWidth) / 2);
        primaryStage.setY((screenBounds.getHeight() - initialHeight) / 2);
        primaryStage.setWidth(initialWidth);
        primaryStage.setHeight(initialHeight);

        // FullScreen option.
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        // Save settings before exiting.
        primaryStage.setOnCloseRequest(e -> {
            registry.submitAction(SaveAction.class);
            registry.dispose();
            Platform.exit();
        });

        createPrimaryScene(primaryStage, registry);

        primaryStage.show();
    }

    private void createPrimaryScene(final Stage stage, final ProgramRegistry registry) {

        final AnchorPane mainPane = new AnchorPane();

        final Color backgroundColor =
                registry.getForClass(DisplayOptions.class).getBackgroundColor();
        mainPane.setBackground(new Background(
                new BackgroundFill(backgroundColor, null, null)));
        registry.getForClass(UpdateDisplayOptionsAction.class)
                .addListenerAfter(opts -> mainPane.setBackground(
                        new Background(new BackgroundFill(
                                opts.getBackgroundColor(), null, null))));

        final TopPaneController topPaneController = new TopPaneController(stage);
        final BookViewController bookViewcontroller = new BookViewController();
        final TopPane topPane = topPaneController.getView();
        final BookView bookPane = bookViewcontroller.getView();

        mainPane.getChildren().addAll(bookPane, topPane);

        final Scene primaryScene = new Scene(mainPane);
        primaryScene.getStylesheets().add("styles.css");
        stage.setScene(primaryScene);

        // Hide mouse cursor and top pane depending on the position of the cursor.
        primaryScene.setOnMouseMoved(ev -> {
            final boolean isInBottom = ev.getSceneY() > primaryScene.getHeight()*9/10;

            ObservableList<Node> availableNodes = mainPane.getChildren();
            if (isInBottom) {
                // Hide topPane and Mouse cursor.
                availableNodes.remove(topPane);
                primaryScene.setCursor(Cursor.NONE);
            } else {
                // Show topPane and Mouse cursor.
                if (!availableNodes.contains(topPane)) {
                    availableNodes.add(topPane);
                }
                primaryScene.setCursor(Cursor.DEFAULT);
            }
        });
    }
}
