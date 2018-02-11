/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.toppane;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.gui.simplewidgets.ClockWidget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class TopPane extends BorderPane{

    private static final Function<Double, String> PERCENT_TO_STRING_CONVERTER =
            percent -> Double.toString((double)Math.round(percent * 10)/10) + "%";

    private static final Function<String, Double> STRING_TO_PERCENT_CONVERTER =
            str -> {
                Double percent = null;
                try{
                    Double value = Double.parseDouble(
                            str.replaceAll("%", "").replace(",", "."));
                    if (value >= 0 && value <= 100){
                        percent = value;
                    }
                }
                catch (Exception e){
                    // String is empty or contains unparsable text.
                }
                return percent;
            };

    private Consumer<Double> percentActionHandler = null;
    private Consumer<Double> speedActionHandler = null;
    private Runnable stopAnimationActionHandler = null;

    private final Spinner<Double> speedWidget;
    private final Label percentWidget;
    private final MenuButton menuWidget;

    TopPane(final Stage stage){
        getStyleClass().add("top-pane");
        setBackground(new Background(new BackgroundFill(
                Color.WHITE, null, null)));

        HBox leftBox = new HBox();
        leftBox.setSpacing(8);
        setLeft(leftBox);

        HBox centerBox = new HBox();
        centerBox.setSpacing(8);
        centerBox.setAlignment(Pos.CENTER);
        setCenter(centerBox);

        setRight(new ClockWidget(24));

        menuWidget = createMenuButton();
        speedWidget = createSpeedWidget();
        percentWidget = createPercentWidget(stage);

        leftBox.getChildren().add(menuWidget);
        centerBox.getChildren().add(speedWidget);
        centerBox.getChildren().add(percentWidget);
    }

    void setPercentActionHandler(final Consumer<Double> percentActionHandler) {
        this.percentActionHandler = percentActionHandler;
    }
    void setSpeedActionHandler(final Consumer<Double> speedWidgetConsumer) {
        this.speedActionHandler = speedWidgetConsumer;
    }
    void setStopAnimationActionHandler(final Runnable stopAnimationActionHandler) {
        this.stopAnimationActionHandler = stopAnimationActionHandler;
    }

    void setScrollSpeedValue(double newValue) {
        speedWidget.setValueFactory(new SpinnerValueFactory
                .DoubleSpinnerValueFactory(0.1, 10, newValue, 0.2));
    }
    void setPercentValue(double newValue) {
        percentWidget.setText(PERCENT_TO_STRING_CONVERTER.apply(newValue));
    }
    void setMenuItems(final List<MenuItem> items) {
        menuWidget.getItems().setAll(items);
    }

    private MenuButton createMenuButton() {
        final String text = ProgramRegistry.INSTANCE
                .getResourceFor("TopPane.MenuButton.text");
        MenuButton button = new MenuButton(text);
        button.getStyleClass().add("menu-button");
        button.setOnMouseClicked(el -> stopAnimationActionHandler.run());
        button.setFocusTraversable(false);

        return button;
    }

    private Spinner<Double> createSpeedWidget() {
        final Spinner<Double> speedWidget = new Spinner<>();

        speedWidget.setMaxWidth(80);
        speedWidget.setEditable(false);

        speedWidget.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (speedActionHandler != null) {
                speedActionHandler.accept(newValue);
            }
        });

        // Add tooltip.
        Tooltip speedSelectorTooltip = new Tooltip();
        speedSelectorTooltip.setText(ProgramRegistry.INSTANCE
                .getResourceFor("TopPane.SpeedSelector.text"));
        speedWidget.setTooltip(speedSelectorTooltip);
        speedWidget.setFocusTraversable(false);

        return speedWidget;
    }

    private Label createPercentWidget(final Stage stage) {
        Label widget = new Label();

        final ProgramRegistry registry = ProgramRegistry.INSTANCE;

        widget.setFont(new Font(24));
        widget.setOnMouseEntered(ev -> widget.setTextFill(Color.GRAY));
        widget.setOnMouseExited(ev -> widget.setTextFill(Color.BLACK));
        widget.setOnMouseClicked(ev -> {
            if (stopAnimationActionHandler != null) {
                stopAnimationActionHandler.run();
            }
            TextInputDialog dialog = new TextInputDialog(widget.getText());
            dialog.initOwner(stage);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle(registry.getResourceFor("TopPane.PercentWidget.Dialog.title"));
            dialog.setHeaderText(registry.getResourceFor("TopPane.PercentWidget.Dialog.headerText"));
            dialog.setContentText(registry.getResourceFor("TopPane.PercentWidget.Dialog.contentText"));

            dialog.showAndWait().ifPresent(value -> {
                final Double percent = STRING_TO_PERCENT_CONVERTER.apply(value);
                if (percent != null && percentActionHandler != null) {
                    percentActionHandler.accept(percent);
                }
            });
        });

        return widget;
    }

}
