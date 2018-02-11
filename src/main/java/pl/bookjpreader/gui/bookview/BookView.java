/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.gui.bookview;


import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import pl.bookjpreader.commons.ErrorHandler;
import pl.bookjpreader.gui.bookview.textwidget.ImagesContainer;
import pl.bookjpreader.gui.bookview.textwidget.TextImage;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * The main window to show text.
 */
public final class BookView extends SelectionPane {
    private static final int MIN_OFFSET = 0;
    /**
     * Contains text as textImages.
     */
    private final ImagesContainer textImagesContainer = new ImagesContainer();
    /**
     * Any animations except scrolling.
     */
    private final TranslateTransition moveAnimation = new TranslateTransition();
    /**
    * Only scrolling animation.
    */
    private final TranslateTransition scrollAnimation = new TranslateTransition();

    private final Function<TextImageParametersBuilder, TextImage> textImageSupplier;
    private final Function<TextImageParametersBuilder,Future<TextImage>> textImageAsyncSupplier;

    private Consumer<Integer> textPositionUpdateHandler = null;
    private Consumer<String> textSelectionActionHandler;

    private double lineSpacing;
    private double scrollSpeed = 1;
    /**
     * The height of one rendered line of text, is used for animations;
     */
    private double oneLineHeight = 20;

    /**
     * The width of the visible part of the screen.
     */
    private double visibleWidth;
    private double visibleHeight;
    /**
     * Indentation (0.0-1.0) from left and right.
     */
    private double horizontalIndentation;
    /**
     * Indentation (0.0-1.0) from top and bottom.
     */
    private double vertIndentation;

    /**
     * Indicates that {@link BookView#redraw} is scheduled to run.
     */
    private AtomicBoolean redrawScheduled = new AtomicBoolean(false);

    BookView(final Function<TextImageParametersBuilder, TextImage> textImageSupplier,
                    final Function<TextImageParametersBuilder,Future<TextImage>> textImageAsyncSupplier) {
        super();
        this.textImageSupplier = textImageSupplier;
        this.textImageAsyncSupplier = textImageAsyncSupplier;

        // Allow widget to grow and set its position.
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);

        initWidgets();
        setKeyBindings();

        widthProperty().addListener(ev -> redraw());
        heightProperty().addListener(ev -> redraw());
    }

    void setScrollSpeed(double speed) {
        scrollSpeed = speed;
        Animation.Status status = scrollAnimation.getStatus();
        if (status == Animation.Status.RUNNING || status == Animation.Status.PAUSED){
            scrollAnimation.stop();
            doNewAnimationScroll();
        }
    }
    void setSingleLineHeight(double height) {
        oneLineHeight = height;
    }
    void setLineSpacing(double lineSpacing) {
        this.lineSpacing = lineSpacing;
        textImagesContainer.setLineSpacing(lineSpacing);
    }

    void setTextPositionUpdateHandler(final Consumer<Integer> handler) {
        this.textPositionUpdateHandler = handler;
    }
    void setTextSelectionActionHandler(final Consumer<String> handler) {
        this.textSelectionActionHandler = handler;
    }
    /**
     * Initializes parameters connected with size settings like
     * screen width, height, wrapping etc.
     * This method should be launched wherever settings are changed.
     * @param borderWidth border left/right size in percent parts;
     * @param borderHeight border top/bottom size in percent parts;
     */
    void setBorders(final double borderWidth, final double borderHeight) {
        horizontalIndentation = borderWidth;
        vertIndentation = borderHeight;
    }

    void stopAnimation() {
        moveAnimation.stop();
        scrollAnimation.stop();
    }
    /**
     * Redraw currently visible scene.
     * This function can be used, for example, if size
     * of the window is changed.
     */
    void redraw() {
        if (textImagesContainer.getStartOffset() != null) {
            redraw(textImagesContainer.getStartOffset());
        }
    }
    /**
     * @param topOffset corresponds to the top visible element.
     * This function executes with small delay to prevent multiple
     * executions in short time for example during size changes.
     */
    void redraw(final int topOffset) {
        if (redrawScheduled.getAndSet(true)) {
            return;
        }
        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished( event -> {
            stopAnimation();
            textImagesContainer.clear();

            updateViewChanges();
            increaseBottom(topOffset, visibleHeight);

            double diff = visibleHeight - textImagesContainer.getContainerHeight();
            if (diff > 0) { // Bottom is reached.
                increaseTop(topOffset, diff);
            }
            textImagesContainer.requestFocus();
            redrawScheduled.set(false);
        } );
        delay.play();

    }

    @Override
    protected void onBeforeStartSelection() {
        stopAnimation();
    }
    @Override
    protected void onCreateSelection(final Bounds bounds) {
        if (textSelectionActionHandler != null) {
            textSelectionActionHandler.accept(
                    textImagesContainer.getTextByCoordinates(bounds));
        }
    }

    private void initWidgets(){
        getChildren().add(textImagesContainer);
        textImagesContainer.setFocusTraversable(true);
        AnchorPane.setTopAnchor(textImagesContainer, 0.0);
        AnchorPane.setBottomAnchor(textImagesContainer, 0.0);
        AnchorPane.setLeftAnchor(textImagesContainer, 0.0);
        AnchorPane.setRightAnchor(textImagesContainer, 0.0);

        moveAnimation.setNode(textImagesContainer);
        moveAnimation.setInterpolator(Interpolator.LINEAR);

        scrollAnimation.setNode(textImagesContainer);
        scrollAnimation.setInterpolator(Interpolator.LINEAR);
    }
    private void setKeyBindings(){
        // Start/Stop scrolling by pressing mouse.
        textImagesContainer.setOnMouseClicked(ev -> {
            if (ev.getButton() == MouseButton.PRIMARY) {
                pauseOrResumeScrolling();
            }
        });

        textImagesContainer.setOnScroll(ev ->{
            if (ev.getDeltaY() > 0)
                doAnimationMove(2*oneLineHeight, 0.1);
            else
                doAnimationMove(-2*oneLineHeight, 0.1);
         });

        final KeyCodeCombination moveToBegin = new KeyCodeCombination(KeyCode.HOME,
                KeyCombination.CONTROL_DOWN);
        final KeyCodeCombination moveToEnd = new KeyCodeCombination(KeyCode.END,
                KeyCombination.CONTROL_DOWN);

        textImagesContainer.setOnKeyPressed(keyEvent -> {
            if (moveToBegin.match(keyEvent))
                moveToBegin();
            else if (moveToEnd.match(keyEvent))
                moveToEnd();
            else if (keyEvent.getCode() == KeyCode.SPACE)
                pauseOrResumeScrolling();
            else if (keyEvent.getCode() == KeyCode.UP)
                doAnimationMove(2*oneLineHeight, 0.1);
            else if (keyEvent.getCode() == KeyCode.DOWN)
                doAnimationMove(-2*oneLineHeight, 0.1);
            else if (keyEvent.getCode() == KeyCode.PAGE_UP)
                doAnimationMove(visibleHeight, 0.3);
            else if (keyEvent.getCode() == KeyCode.PAGE_DOWN)
                doAnimationMove(- visibleHeight, 0.3);
        });
    }

    private void pauseOrResumeScrolling() {
        textImagesContainer.requestFocus();
        Animation.Status status = scrollAnimation.getStatus();
        if (status == Animation.Status.RUNNING) {
            scrollAnimation.pause();
        } else if (status == Animation.Status.PAUSED) {
            scrollAnimation.play();
        } else if (status == Animation.Status.STOPPED) {
            doNewAnimationScroll();
        }
    }
    private void doNewAnimationScroll() {
        if (moveAnimation.getStatus().equals(Animation.Status.RUNNING) ||
                scrollAnimation.getStatus().equals(Animation.Status.RUNNING)) {
            return;
        }

        final double distance = visibleHeight / 2;
        if (isNecessaryToIncreaseBottom(distance)) {
            increaseBottom(distance);
        }
        animationScrollCycle(null);
    }

    private void animationScrollCycle(Future<TextImage> futureTextImage) {
        final double distance = 2*oneLineHeight;
        final double speedPixelsPerSecond = 50; // 50 pixels per second.

        final Future<TextImage> newFutureTextImage;

        if (futureTextImage == null) {
            newFutureTextImage = getBottomAsync(visibleHeight);
        } else if (isNecessaryToIncreaseBottom(distance)
                && futureTextImage.isDone()) {
            try {
                final TextImage textImage = futureTextImage.get();
                increaseBottom(textImage);
            } catch (Exception e) {
                ErrorHandler.handle(e);
            }
            newFutureTextImage = getBottomAsync(visibleHeight);
        } else {
            newFutureTextImage = futureTextImage;
        }

        double posY = textImagesContainer.getTranslateY();
        double posTo = posY + checkBoundaryConditions(- distance);
        if (posTo == posY) {
            return;
        }

        scrollAnimation.setFromY(posY);
        scrollAnimation.setToY(posTo);

        double scrollDuration = distance/speedPixelsPerSecond/ scrollSpeed;
        scrollAnimation.setDuration(Duration.seconds(scrollDuration));
        scrollAnimation.setOnFinished(a -> animationScrollCycle(newFutureTextImage));
        scrollAnimation.play();
    }
    private void doAnimationMove(double value, double seconds){
        if (moveAnimation.getStatus().equals(Animation.Status.RUNNING))
            return;

        // Continue scrolling after move animation.
        boolean isScrolling = false;
        if (scrollAnimation.getStatus() == Animation.Status.RUNNING) {
            isScrolling = true;
        }
        scrollAnimation.stop();

        if (value > 0) { // animation Up/PgUp
            increaseTopIfNecessary(value);
        } else if (isNecessaryToIncreaseBottom(-value)) {// animation Down/PgDn
            increaseBottom(-value);
        }

        value = checkBoundaryConditions(value);
        double posY = textImagesContainer.getTranslateY();
        moveAnimation.setFromY(posY);
        moveAnimation.setToY(posY + value);
        moveAnimation.setDuration(Duration.seconds(seconds));

        if (isScrolling) {
            moveAnimation.setOnFinished(ev -> doNewAnimationScroll());
        } else {
            moveAnimation.setOnFinished(ev -> {});
        }

        moveAnimation.play();
    }

    /**
     * @param distance check whether top/bottom is reached after moving to this distance
     * from the current position; distance may be positive and negative.
     * @return returns minimum of two values: the given distance and
     * the distance between current position and the bottom.
     */
    private double checkBoundaryConditions(double distance){
        if (distance == 0)
            return 0;

        double newValue = 0;
        if (distance > 0){
            // Image is moving bottom so check only top.
            if (textImagesContainer.getStartOffset() != 0)
                return distance; // top is not even processed

            double absPosY = Math.abs(textImagesContainer.getTranslateY());
            newValue = Math.min(absPosY, distance);
        }
        else{
            // Check only bottom.
            distance = Math.abs(distance);
            double absPosY = Math.abs(textImagesContainer.getTranslateY());

            double difference = textImagesContainer.getContainerHeight()
                    - absPosY - visibleHeight;

            if (difference > 0) { // Bottom not reached.
                newValue = difference > distance ? -distance : -difference;
            }
       }

        return newValue;
    }

    private void moveToBegin(){
        redraw(MIN_OFFSET);
    }
    private void moveToEnd(){
        TextImageParametersBuilder params = new TextImageParametersBuilder();
        params.setDirection(TextImageParametersBuilder.Direction.END)
                .setLineSpacing(lineSpacing)
                .setWidth(visibleWidth)
                .setHeight(visibleHeight);

        TextImage newImage = textImageSupplier.apply(params);
        if (newImage != null) {
            textImagesContainer.clear();
            textImagesContainer.addBottomImage(newImage);
            double diff = textImagesContainer.getContainerHeight() - visibleHeight;
            textImagesContainer.setTranslateY(-diff);

            updateTextPositionAction();
        }
    }

    private void increaseTopIfNecessary(final double addHeight){
        final Integer startOffset = textImagesContainer.getStartOffset();
        if (startOffset != null) {
            final double containerTranslate = textImagesContainer.getTranslateY();
            if (containerTranslate > 0 || - containerTranslate < addHeight) {
                increaseTop(startOffset, addHeight);
            }
        }
    }
    /**
     * Add elements on top.
     * @param addHeight minimum height of the added images above visible area.
     * returns the size of added images.
     */
    private void increaseTop(final int fromOffset, final double addHeight){
        final TextImageParametersBuilder params = new TextImageParametersBuilder();
        params.setDirection(TextImageParametersBuilder.Direction.PREVIOUS)
                .setLineSpacing(lineSpacing)
                .setWidth(visibleWidth)
                .setHeight(addHeight)
                .setStartOffset(fromOffset);

        TextImage newImage = textImageSupplier.apply(params);
        if (newImage != null) {
            textImagesContainer.addTopImage(newImage);

            // Remove excessive parts.
            adjustBottom(oneLineHeight);
            updateTextPositionAction();
        }
    }
    /**
     * Remove excessive top elements.
     * @param addY a minimum positive length of additional elements
     * which is allowed on leave them on top. E.g. if addY = 0 than in ideal situation
     * there will be to top part.
     */
    private void adjustTop(double addY){
        while(true){
            if (textImagesContainer.getChildren().size() < 2) // only 1 or 0 elements in container
                return;

            double posY = textImagesContainer.getTranslateY();
            double topImageHeight = textImagesContainer.getTopImageHeight();
            if (posY < - addY - topImageHeight){
                textImagesContainer.removeTopImage();
            }
            else
                return;
        }
    }

    private boolean isNecessaryToIncreaseBottom(final double addHeight){
        boolean result = false;
        final Integer endOffset = textImagesContainer.getEndOffset();
        if (endOffset != null) {
            final double containerHeight = textImagesContainer.getContainerHeight();
            final double containerTranslate = textImagesContainer.getTranslateY();

            if (containerHeight + containerTranslate <= visibleHeight + addHeight) {
                result = true;
            }
        }
        return result;
    }
    private void increaseBottom(final double addHeight){
        increaseBottom(textImagesContainer.getEndOffset(), addHeight);
    }
    /**
     * Add elements on bottom.
     * @param addHeight minimum height of the added images below visible area.
     */
    private void increaseBottom(final Integer fromOffset, final double addHeight){
        final TextImageParametersBuilder params = new TextImageParametersBuilder();
        params.setDirection(TextImageParametersBuilder.Direction.NEXT)
                .setLineSpacing(lineSpacing)
                .setWidth(visibleWidth)
                .setHeight(addHeight)
                .setStartOffset(fromOffset);

        TextImage newImage = textImageSupplier.apply(params);
        increaseBottom(newImage);
    }
    private void increaseBottom(final TextImage newImage){
        if (newImage != null) {
            textImagesContainer.addBottomImage(newImage);

            // Remove excessive parts.
            adjustTop(oneLineHeight);
            updateTextPositionAction();
        }
    }
    private Future<TextImage> getBottomAsync(final double addHeight){
        final TextImageParametersBuilder params = new TextImageParametersBuilder();
        final Integer endOffset = textImagesContainer.getEndOffset();
        params.setDirection(TextImageParametersBuilder.Direction.NEXT)
                .setLineSpacing(lineSpacing)
                .setWidth(visibleWidth)
                .setHeight(addHeight)
                .setStartOffset(endOffset != null ? endOffset : 0);

        return textImageAsyncSupplier.apply(params);
    }
    /**
     * Remove excessive bottom elements.
     * @param addY a minimum positive length of additional elements
     * which is allowed on leave them on bottom. E.g. if addY = 0 than in ideal situation
     * there will be no bottom parts except in the visible range (in visibleHeight).
     */
    private void adjustBottom(double addY){
        while(true){
            if (textImagesContainer.getChildren().size() < 2) { // only 1 or 0 elements in vbox
                break;
            }
            final double bottomImageHeight = textImagesContainer.getBottomImageHeight();
            final double boxHeight = textImagesContainer.getContainerHeight();
            if (textImagesContainer.getTranslateY() + boxHeight > visibleHeight + addY + bottomImageHeight){
                textImagesContainer.removeBottomImage();
            }
            else {
                break;
            }
        }
    }
    /**
     * This function calls handler to accept new text position.
     * Can be used, for example, to allow other parts of the program know
     * about text position changes.
     * It is not precise and position may differ from what it is actually on the screen.
     */
    private void updateTextPositionAction() {
        final Integer startOffset = textImagesContainer.getStartOffset();
        if (textPositionUpdateHandler != null && startOffset != null){
            final Integer calculatedPos;

            if (startOffset == MIN_OFFSET) {
                calculatedPos = MIN_OFFSET;
            } else {
                calculatedPos = textImagesContainer.getVisibleStartOffset();
            }

            if (calculatedPos != null) {
                textPositionUpdateHandler.accept(calculatedPos);
            }
        }
    }
    /**
     * This method should be launched wherever view settings (size, borders, etc.)
     * are changed to apply them.
     */
    private void updateViewChanges() {
        final double horizontal = horizontalIndentation * getWidth();
        final double vertical = vertIndentation * getHeight();

        visibleWidth = getWidth() - 2 * horizontal;
        visibleHeight = getHeight() - 2 * vertical;

        setPadding(new Insets(
                vertical, horizontal,   // top, right
                vertical, horizontal)); // bottom, left

        // Set clip to hide anything outside rectangle region.
        Rectangle clip = new Rectangle(getWidth(), visibleHeight);
        clip.setLayoutX(0);
        clip.setLayoutY(vertical);
        setClip(clip);
    }

}
