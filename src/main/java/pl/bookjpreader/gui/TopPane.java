/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui;

import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import pl.bookjpreader.booksfactory.BookFile;
import pl.bookjpreader.commons.ProgramSettings;
import pl.bookjpreader.gui.simplewidgets.BookChooser;
import pl.bookjpreader.gui.simplewidgets.ClockWidget;

public class TopPane extends BorderPane{

    final private ProgramSettings settings;
    final private Stage primaryStage;

    final private CheckMenuItem checkFullScreen;
    final private MenuItem openFileItem;
    final private MenuItem openBookShelf;
    final private MenuItem searchTextItem;
    final private MenuItem closeProgramItem;

    private String searchString = ""; // To save last search string.

    public TopPane(ProgramSettings settings, Stage primaryStage){

        this.settings = settings;
        this.primaryStage = primaryStage;
        checkFullScreen = new CheckMenuItem("FullScreen Mode");
        openFileItem = new MenuItem("Open new File");
        openBookShelf = new MenuItem("Book Shelf");
        closeProgramItem = new MenuItem("Exit");
        searchTextItem = new MenuItem("Search");

        setBackground(new Background(new BackgroundFill(
                Color.WHITE, null, null)));

        HBox leftBox = new HBox();
        leftBox.setSpacing(8);

        leftBox.getChildren().add(getMenuButton());
        setLeft(leftBox);

        HBox centerBox = new HBox();
        centerBox.setSpacing(8);
        //centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(getSpeedSelector());
        centerBox.getChildren().add(getPercentWidget());
        centerBox.setAlignment(Pos.CENTER);
        setCenter(centerBox);

        setRight(new ClockWidget(24));

        setKeyBindings();
    }

    public void setKeyBindings(){
        KeyCodeCombination fullScreenCombination = new KeyCodeCombination(KeyCode.F11);
        checkFullScreen.setAccelerator(fullScreenCombination);

        KeyCodeCombination openFileCombination = new KeyCodeCombination(KeyCode.O,
                KeyCombination.CONTROL_DOWN);
        openFileItem.setAccelerator(openFileCombination);

        KeyCodeCombination openBookShelfCombination = new KeyCodeCombination(KeyCode.B,
                KeyCombination.CONTROL_DOWN);
        openBookShelf.setAccelerator(openBookShelfCombination);

        KeyCodeCombination searchTextCombination = new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN);
        searchTextItem.setAccelerator(searchTextCombination);

        KeyCodeCombination closeProgramCombination = new KeyCodeCombination(KeyCode.X,
                KeyCombination.CONTROL_DOWN);
        closeProgramItem.setAccelerator(closeProgramCombination);
    }

    public void disableShortcuts(boolean value){
        /*
         * This function is used to make shortcuts inactive.
         */
        checkFullScreen.setDisable(value);
        openFileItem.setDisable(value);
        openBookShelf.setDisable(value);
        searchTextItem.setDisable(value);
    }

    private MenuButton getMenuButton(){
        final MenuButton prefButton = new MenuButton("Preferences");
        prefButton.setStyle( // make it semiround
                "-fx-background-radius: 0 0 10 0;"
                +"-fx-border-color: transparent;"
                );

        prefButton.setOnMouseClicked(ev -> {
            if (ev.getButton().equals(MouseButton.SECONDARY))
                doCurrentBookInfo();
        });
        // Set tooltip to show current's book filename.
        Tooltip fileNameTooltip = new Tooltip();
        fileNameTooltip.setOnShowing(ev ->
            fileNameTooltip.setText(settings.bookShelf.getCurrentBookFilePath()));
        prefButton.setTooltip(fileNameTooltip);

        prefButton.getItems().add(openFileItem);
        openFileItem.setOnAction(ev -> doOpenNewFile());

        prefButton.getItems().add(openBookShelf);
        openBookShelf.setOnAction(ev -> doOpenBookShelf());

        prefButton.getItems().add(new SeparatorMenuItem());

        MenuItem mIt = new MenuItem("Settings");
        prefButton.getItems().add(mIt);
        mIt.setOnAction(ev -> doOpenSettings());

        prefButton.getItems().add(searchTextItem);
        searchTextItem.setOnAction(ev -> doOpenSearchDialog());

        prefButton.getItems().add(checkFullScreen);

        checkFullScreen.setOnAction(ev -> doFullScreenMode());
        primaryStage.fullScreenProperty().addListener((obs, oldValue, newValue) ->
        checkFullScreen.setSelected(newValue));

        prefButton.getItems().add(new SeparatorMenuItem());

        prefButton.getItems().add(closeProgramItem);
        closeProgramItem.setOnAction(ev -> doCloseProgram());

        mIt = new MenuItem("About");
        prefButton.getItems().add(mIt);
        mIt.setOnAction(ev -> doAbout());

        prefButton.setFocusTraversable(false);
        return prefButton;
    }
    private Spinner<Double> getSpeedSelector(){
        final Spinner<Double> speedSelector = new Spinner<>();
        double currentSpeed = settings.minorOptions.getScrollSpeed();
        speedSelector.setValueFactory(new SpinnerValueFactory
                .DoubleSpinnerValueFactory(0.1, 10, currentSpeed, 0.2));
        speedSelector.setMaxWidth(80);
        speedSelector.setEditable(true);

        speedSelector.valueProperty().addListener((obs, oldValue, newValue) -> {
            settings.minorOptions.setScrollSpeed(newValue.doubleValue());
        });
        // Add tooltip.
        Tooltip speedSelectorTooltip = new Tooltip();
        speedSelectorTooltip.setText("scrolling speed");
        speedSelector.setTooltip(speedSelectorTooltip);

        speedSelector.setFocusTraversable(false);
        return speedSelector;
    }

    private String formatPercent(double percent){
        return Double.toString((double)Math.round(percent * 10)/10);
    }
    private Label getPercentWidget(){
        final Label positionText = new Label();
        positionText.setFont(new Font(24));


        if (settings.bookShelf.currentBook.get() != null){
            double percent = settings.bookShelf.currentBookPosition.get();
            positionText.setText(formatPercent(percent) + "%");
        }

        positionText.setOnMouseEntered(ev -> positionText.setTextFill(Color.GRAY));
        positionText.setOnMouseExited(ev -> positionText.setTextFill(Color.BLACK));
        positionText.setOnMouseClicked(ev-> doChangePosition());

        // Automatically update percents.
        settings.bookShelf.currentBookPosition.addListener(
                (obs, oldPos, newPos) -> {
                    if (newPos == null)
                        positionText.setText("");
                    else
                        positionText.setText(formatPercent(newPos) + "%");
        });
        return positionText;
    }
    private void doChangePosition(){
        if (settings.bookShelf.currentBook.get() == null)
            return;
        settings.bookShelf.stopAnimation();

        double currentPercent = settings.bookShelf.currentBookPosition.get();

        TextInputDialog dialog = new TextInputDialog(
                String.valueOf(formatPercent(currentPercent)));
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Position Dialog");
        dialog.setHeaderText("Where do you want to move?");
        dialog.setContentText("Please enter new position (%):");

        Optional<String> dialogResult = dialog.showAndWait();
        double percent = -1;
        try{
            String input = dialogResult.get();
            percent = Double.parseDouble(input.replace(",", "."));
        }
        catch (Exception e){
            // String is empty or contains unparsable text.
        }
        if (percent >= 0 && percent <= 100)
            settings.bookShelf.setPercentPosition(percent);
    }

    private void doOpenNewFile(){
        /*
         * Select a new file in the dialog and open it.
         */
        settings.bookShelf.stopAnimation();

        BookChooser bookChooser = new BookChooser(primaryStage);
        if (bookChooser.getFileName() != null){
            if (bookChooser.getEncoding() == null)
                settings.bookShelf.openNewBook(new BookFile(
                        bookChooser.getFileName().toPath()));
            else
                settings.bookShelf.openNewBook(new BookFile(
                        bookChooser.getFileName().toPath(),
                        bookChooser.getEncoding()));
        }
    }

    private void doOpenSettings(){
        settings.bookShelf.stopAnimation();

        new SettingsDialog(primaryStage, settings.displayOptions).showAndWait();
    }

    private void doOpenSearchDialog(){
        settings.bookShelf.stopAnimation();

        TextInputDialog dialog = new TextInputDialog(searchString);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Search Dialog");
        dialog.setHeaderText("Input text to search");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            searchString = result.get();
            if (searchString.length() < 3)
                return;

            double newPos;
            try{
                double currentPos = settings.bookShelf.currentBookPosition.get();
                newPos = settings.bookShelf.currentBook.get()
                        .getReader()
                        .searchText(searchString, currentPos);
            }
            catch (Exception e){
                newPos = -1;
            }

            if (newPos == -1){
                // Show message that nothing found
                Alert info = new Alert(AlertType.INFORMATION);
                info.initOwner(primaryStage);
                info.setTitle("Nothing found");
                info.initStyle(StageStyle.UTILITY);

                info.setContentText("Nothing found");
                info.showAndWait();
            }
            else{
                settings.bookShelf.setPercentPosition(newPos);
            }
        }

    }
    private void doOpenBookShelf(){
        settings.bookShelf.stopAnimation();
        new BookShelfDialog(primaryStage, settings.bookShelf).showAndWait();
    }
    private void doCloseProgram(){
        /*
         * Fire a closing event so that event handler can
         * catch it and save settings.
         */
        primaryStage.fireEvent(new WindowEvent(primaryStage,
                        WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    private void doFullScreenMode(){
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }
    private void doAbout(){
        settings.bookShelf.stopAnimation();

        Alert info = new Alert(AlertType.INFORMATION);
        info.initOwner(primaryStage);
        info.setTitle("About");
        info.initStyle(StageStyle.UTILITY);

        info.setContentText(
                "This is the book reader program,\n"
                +"\u00a9 Pavel_M-v, 2016"
                );
        info.showAndWait();
    }
    private void doCurrentBookInfo(){
        Alert info = new Alert(AlertType.INFORMATION);
        info.initOwner(primaryStage);
        info.setTitle("Current Book");
        info.initStyle(StageStyle.UTILITY);

        info.setContentText("Current Book: " + settings.bookShelf.getCurrentBookFilePath());
        info.showAndWait();

    }
}
