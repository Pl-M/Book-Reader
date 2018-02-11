/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.simplewidgets;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import pl.bookjpreader.booksfactory.BookEntity;
import javafx.collections.ObservableList;

public class BookTable extends TableView<BookEntity> {
    /**
     * Contains all books shown in the widget, this param is used
     *  to get currently available books in the table.
     */
    private final ObservableList<BookEntity> obsBooks;

    public BookTable(Set <BookEntity> books) {
        super();
        obsBooks = FXCollections.observableArrayList();
        obsBooks.addAll(books);
        setItems(obsBooks);

        setPrefHeight(1000);
        setBookColumns();

        this.setPlaceholder(new Label("")); // to remove 'no content' message on the table

        this.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE)
               doRemoveSelectedBook();
        });
    }
    private void setBookColumns(){
        TableColumn <BookEntity, String> colFileName = new TableColumn<>("File Name");
        colFileName.prefWidthProperty().bind(widthProperty().multiply(0.85));

        colFileName.setCellValueFactory(
                new PropertyValueFactory<>("fileName"));

        TableColumn <BookEntity, String> colPercent = new TableColumn<>("Position");
        colPercent.prefWidthProperty().bind(widthProperty().multiply(0.15));
        colPercent.setCellValueFactory(cellData -> {
            int percent = (int)cellData.getValue().getPosition();
            return new ReadOnlyObjectWrapper<>(
                    String.valueOf(percent) + "%");
        });

        getColumns().addAll(colFileName, colPercent);

        setRowFactory( tv -> new TableRow<BookEntity>());
        setContextMenu(addTableContextMenu());
    }

    /**
     * Set Popup Menu for the table.
     * @return popup menu.
     */
    private ContextMenu addTableContextMenu(){
        MenuItem removeBook = new MenuItem("Remove book from the library");
        removeBook.setOnAction(ev -> doRemoveSelectedBook());

        return new ContextMenu(removeBook);
    }
    private void doRemoveSelectedBook(){
        obsBooks.remove(getSelectionModel().getSelectedItem());
    }
    public Set<BookEntity> getBooks(){
        return new HashSet<>(obsBooks);
    }
}
