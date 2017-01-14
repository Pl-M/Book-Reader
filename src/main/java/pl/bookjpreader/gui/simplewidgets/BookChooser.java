/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/

/*
 * This class encapsulate dialog to choose book file.
 */

package pl.bookjpreader.gui.simplewidgets;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Set;

import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BookChooser {
    private FileChooser fileChooser;
    private File selectedFile;
    private String encoding;
    private Stage primaryStage;

    public BookChooser(Stage primaryStage) {
        this.primaryStage = primaryStage;
        encoding = null;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        FileChooser.ExtensionFilter encodingFilter =
                new FileChooser.ExtensionFilter("Select encoding", "*.*");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All supported formats", "*.txt", "*.fb2", "*.html", "*.htm"),
                encodingFilter,
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        selectedFile = fileChooser.showOpenDialog(primaryStage);

        // If 'selected encoding' is chosen than show select encoding dialog.
        if (selectedFile != null && fileChooser.getSelectedExtensionFilter().equals(encodingFilter)){
            ChooseEncoding();
        }
    }
    public File getFileName(){
        return selectedFile;
    }
    public String getEncoding(){
        return encoding;
    }
    private void ChooseEncoding(){
        Set<String> charsets = Charset.availableCharsets().keySet();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(encoding, charsets);

        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setTitle("Select Encoding");
        dialog.setHeaderText("Select Encoding");
        dialog.setContentText("Please select the correct encoding:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
            encoding = result.get();
    }
}
