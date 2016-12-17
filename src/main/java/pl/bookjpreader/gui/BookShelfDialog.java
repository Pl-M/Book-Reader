/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui;


import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.bookjpreader.booksfactory.BookFile;
import pl.bookjpreader.commons.BookShelf;
import pl.bookjpreader.gui.simplewidgets.BookTable;

/*
 * The dialog to change user preferences like font, color, etc.
 */

public class BookShelfDialog extends Stage{

    final private BookShelf bookShelf;

    BookTable bookTable;

    public BookShelfDialog(Stage primaryStage, BookShelf bookShelf) {
        this.bookShelf = bookShelf;

        initOwner(primaryStage);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.WINDOW_MODAL);
        setResizable(false);

        BorderPane mainContainer = new BorderPane();

        // Width and Height of this dialog.
        int dialogWidth = 600;
        int dialogHeight = 400;

        setScene(new Scene(mainContainer, dialogWidth, dialogHeight));

        bookTable = new BookTable(bookShelf.getBooks());

        mainContainer.setCenter(bookTable);

        HBox buttonBox = new HBox(50);
        buttonBox.setPadding(new Insets(5, 5, 5, 5));
        buttonBox.setAlignment(Pos.CENTER);
        mainContainer.setBottom(buttonBox);

        Button okButton = new Button("OK");
        okButton.setMinWidth(100);
        buttonBox.getChildren().add(okButton);
        okButton.setOnAction(ev -> onOKClicked());

        Button cancelButton = new Button("Cancel");
        cancelButton.setMinWidth(100);
        buttonBox.getChildren().add(cancelButton);
        cancelButton.setOnAction(ev -> onCancelClicked());

        // Center dialog.
        setX(primaryStage.getX() + primaryStage.getWidth()/2 - dialogWidth/2);
        setY(primaryStage.getY() + primaryStage.getHeight()/2 - dialogHeight/2);

        bookTable.setOnMouseClicked(ev -> {
            if (ev.getButton().equals(MouseButton.PRIMARY)){
                if(ev.getClickCount() == 2)
                    onOKClicked();
            }
        });
    }

    private void onOKClicked(){
        // Remove books.
        Set<BookFile> books = bookShelf.getBooks();
        books.removeAll(bookTable.getBooks());
        for (BookFile book : books){
            bookShelf.removeBook(book);
        }
        // Open selected book.
        BookFile book = bookTable.getSelectionModel().getSelectedItem();
        if (book != null)
            bookShelf.openNewBook(book);
        close();
    }
    private void onCancelClicked(){

        close();
    }
}