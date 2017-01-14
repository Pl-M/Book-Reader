/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.gui;


import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import pl.bookjpreader.booksfactory.BookFile;
import pl.bookjpreader.commons.DisplayOptions;
import pl.bookjpreader.commons.ProgramSettings;
import pl.bookjpreader.gui.textwidget.ImagesContainer;
import pl.bookjpreader.gui.textwidget.TextImage;
import pl.bookjpreader.gui.textwidget.TextImageFactory;

/*
 * The main window which shows text.
 */

public class BookView{

    /*
     * @param mainContainer: to change between edit/normal modes.
     * @param textImagesContainer: contains text as textImages.
     * @param editModeContainer: TextArea which contains text to edit.
     * @param moveAnimation: any animations except scrolling.
     * @param scrollAnimation: only scrolling animation.
     * @param visibleWidth: the width of the visible part of the screen.
     * @param oneLineHeight: the height of one rendered line of text, is
     * used for animations;
     * @param editMode: indicates whether the program is in edit mode or not;
     * @param minIndent: some default indentation to text from left and right.
     */
    final private AnchorPane mainContainer;
    final private ImagesContainer textImagesContainer;
    final private Text richText;
    final private VBox editModeContainer;
    final private TextArea editModeText;

    final private TranslateTransition moveAnimation;
    final private TranslateTransition scrollAnimation;

    final public BooleanProperty editMode;

    // Parameters connected with settings.
    final private ProgramSettings settings;
    final private DisplayOptions displayOptions;
    private int visualQuality;

    private BookFile.Reader bookReader;


    final private double minIndent = 20; // minimal indent
    private double visibleWidth;
    private double visibleHeight;
    private double oneLineHeight;

    public BookView(ProgramSettings settings) {

        this.settings = settings;
        displayOptions = settings.displayOptions;
        bookReader = null;

        mainContainer = new AnchorPane();

        textImagesContainer = new ImagesContainer();
        textImagesContainer.setFocusTraversable(true);
        AnchorPane.setTopAnchor(textImagesContainer, 0.0);
        AnchorPane.setBottomAnchor(textImagesContainer, 0.0);
        AnchorPane.setLeftAnchor(textImagesContainer, 0.0);
        AnchorPane.setRightAnchor(textImagesContainer, 0.0);
        // textImagesContainer will be added to mainContainer after new book is initialized.

        editMode = new SimpleBooleanProperty(false);

        editModeText = new TextArea();
        editModeText.setWrapText(true);
        editModeText.setEditable(true);
        editModeContainer = new VBox();
        Button exitEditModeBtn = new Button("Press Esc to exit");
        exitEditModeBtn.setOnAction(ev -> editMode.set(false));

        editModeContainer.getChildren().add(exitEditModeBtn);
        editModeContainer.getChildren().add(editModeText);

        AnchorPane.setTopAnchor(editModeContainer, 0.0);
        AnchorPane.setBottomAnchor(editModeContainer, 0.0);
        AnchorPane.setLeftAnchor(editModeContainer, 0.0);
        AnchorPane.setRightAnchor(editModeContainer, 0.0);

        richText = new Text();

        moveAnimation = new TranslateTransition();
        moveAnimation.setNode(textImagesContainer);
        moveAnimation.setInterpolator(Interpolator.LINEAR);
        // moveAnimation.setInterpolator(Interpolator.DISCRETE);

        scrollAnimation = new TranslateTransition();
        scrollAnimation.setNode(textImagesContainer);
        scrollAnimation.setInterpolator(Interpolator.LINEAR);
        //moveAnimation.setInterpolator(Interpolator.DISCRETE);


        setKeyBindings();
        onChangeDisplaySettings();
        // Show a book if it is available.
        onOpenNewBook();


        // Listen to changes.
        mainContainer.widthProperty().addListener(ev ->{
            onChangeSize();
            if (bookReader != null) // book is initialized
                rebuildScene();
        });
        mainContainer.heightProperty().addListener(ev ->{
            onChangeSize();
            if (bookReader != null) // book is initialized
                rebuildScene();
        });

        settings.displayOptions.property.addListener((obs, oldOpts, newOpts) ->{
            onChangeDisplaySettings();
            rebuildScene();
        });
        settings.minorOptions.scrollSpeedProperty.addListener(ev ->
        onScrollSpeedChanged());

        settings.bookShelf.currentBook.addListener((obs, oldBook, newBook)
                -> onOpenNewBook());
        settings.bookShelf.currentBookPosition.addListener((obs, oldPos, newPos) -> {
            if (newPos != null && bookReader != null){
                int offset = bookReader.getOffsetFromPercent(newPos);
                onCursorPositionChanged(offset);
            }
        });

        editMode.addListener((obs, oldValue, newValue) -> {
            if (newValue)
                doEnterEditMode();
            else
                doExitEditMode();
        });
    }
    public AnchorPane getPane(){
        return mainContainer;
    }
    private void setKeyBindings(){
        // Start/Stop scrolling by pressing mouse.
        textImagesContainer.setOnMouseClicked(ev -> onMouseClicked());

        textImagesContainer.setOnScroll(ev ->{
            if (ev.getDeltaY() > 0)
                doAnimationMove(2*oneLineHeight, 0.1);
            else
                doAnimationMove(-2*oneLineHeight, 0.1);
         });
        textImagesContainer.setOnKeyPressed(keyEvent -> {
            KeyCodeCombination enterEditMode = new KeyCodeCombination(KeyCode.ENTER,
                    KeyCombination.CONTROL_DOWN);
            KeyCodeCombination gotoBegin = new KeyCodeCombination(KeyCode.HOME,
                    KeyCombination.CONTROL_DOWN);
            KeyCodeCombination gotoEnd = new KeyCodeCombination(KeyCode.END,
                    KeyCombination.CONTROL_DOWN);
            if (enterEditMode.match(keyEvent))
                editMode.set(true);
            else if (gotoBegin.match(keyEvent))
                doGotoBegin();
            else if (gotoEnd.match(keyEvent))
                doGotoEnd();
            else if (keyEvent.getCode() == KeyCode.SPACE)
                onMouseClicked();
            else if (keyEvent.getCode() == KeyCode.UP)
                doAnimationMove(2*oneLineHeight, 0.1);
            else if (keyEvent.getCode() == KeyCode.DOWN)
                doAnimationMove(-2*oneLineHeight, 0.1);
            else if (keyEvent.getCode() == KeyCode.PAGE_UP)
                doAnimationMove(visibleHeight, 0.3);
            else if (keyEvent.getCode() == KeyCode.PAGE_DOWN)
                doAnimationMove(-visibleHeight, 0.3);
        });
        editModeText.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE)
                editMode.set(false);
        });
    }
    private void onOpenNewBook(){
        mainContainer.getChildren().clear();

        if (settings.bookShelf.currentBook.get() == null)
            return;
        try {
            bookReader = settings.bookShelf.currentBook.get().getReader();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mainContainer.getChildren().add(textImagesContainer);

        int currentPos = bookReader.getOffsetFromPercent(
                settings.bookShelf.currentBook.get().getPosition());

        buildNewScene(currentPos);

        textImagesContainer.requestFocus();
    }
    private void onCursorPositionChanged(int offset){
        // Check that new position isn't loaded in view.
        int startPos = textImagesContainer.getStartOffset();
        int endPos = textImagesContainer.getEndOffset();

        if (offset < startPos || offset > endPos)
            buildNewScene(offset);
    }
    private void onScrollSpeedChanged(){
        Animation.Status status = scrollAnimation.getStatus();
        if (status == Animation.Status.RUNNING || status == Animation.Status.PAUSED){
            scrollAnimation.stop();
            doAnimationScroll();
        }
    }
    private void onChangeSize() {
        /*
         * Here are initialized parameters connected with size settings like
         * screen width, height, wrapping etc.
         * This method should be launched wherever settings are changed.
         * @param newWidth: new visual width - width of the parent node;
         * @param newHeight: new visual height - height of the parent node;
         */

        double newWidth = mainContainer.getWidth();
        double newHeight = mainContainer.getHeight();

        double horizontalBorder = displayOptions.getHIndentation() * newWidth + minIndent;
        double verticalBorder = displayOptions.getVIndentation() * newHeight;

        visibleWidth = newWidth - 2 * horizontalBorder;
        visibleHeight = newHeight - 2 * verticalBorder;

        mainContainer.setPadding(new Insets(
                       verticalBorder, horizontalBorder,   // top, right
                       verticalBorder, horizontalBorder)); // bottom, left

        // Set width of the text.
        richText.setWrappingWidth(visibleWidth);

        // Set clip to hide anything outside rectangle region.
        Rectangle clip = new Rectangle(mainContainer.getWidth(), visibleHeight);
        clip.setLayoutX(0);
        clip.setLayoutY(verticalBorder);
        mainContainer.setClip(clip);
    }

    private void onChangeDisplaySettings() {
        /*
         * Here are initialized parameters connected settings which demand reloading
         * the scene like fonts, colors etc.
         * This method should be launched wherever settings are changed.
         */

        // Set visual text settings.
        richText.setFill(displayOptions.getTextColor());
        richText.setFontSmoothingType(FontSmoothingType.GRAY); // to smooth font
        richText.setFont(displayOptions.getTextFont());
        richText.setTextAlignment(TextAlignment.JUSTIFY);
        //richText.setSmooth(true);

        richText.setText(" ");
        oneLineHeight = richText.getLayoutBounds().getHeight();

        // Set spacing.
        richText.setLineSpacing(oneLineHeight * displayOptions.getLineSpacing());
        textImagesContainer.setLineSpacing(oneLineHeight * displayOptions.getLineSpacing());

        // Set edit mode settings.
        editModeText.setFont(displayOptions.getTextFont());

        // Set quality.
        visualQuality = displayOptions.getQuality();

        // Update sizes to apply indentations.
        onChangeSize();
     }
    private void onMouseClicked(){
        textImagesContainer.requestFocus();
        Animation.Status status = scrollAnimation.getStatus();
        if (status == Animation.Status.RUNNING)
            scrollAnimation.pause();
        else if (status == Animation.Status.PAUSED)
            scrollAnimation.play();
        else if (status == Animation.Status.STOPPED)
            doAnimationScroll();
    }

    private void doEnterEditMode(){
        moveAnimation.stop();
        scrollAnimation.stop();

        //Add TextArea to the main container.
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(editModeContainer);
        editModeText.setText(textImagesContainer.getText());

        // Hack: Add a transition to move scroll to the given point, otherwise
        // it doesn't move.
        PauseTransition idle = new PauseTransition(Duration.seconds(0.01));
        idle.setOnFinished(ev ->
            editModeText.setScrollTop(- textImagesContainer.getTranslateY()));
        idle.play();

        editModeText.requestFocus();
    }

    private void doExitEditMode(){
        editModeText.clear();
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(textImagesContainer);
        textImagesContainer.requestFocus();
    }

    private void doGotoBegin(){
        buildNewScene(0);
    }
    private void doGotoEnd(){
        buildNewScene(bookReader.textLength);
    }

    private void doAnimationScroll(){
        /* @var scrollHeight: how many pixels move in one iteration;
         * @var scrollSpeed: how much time does it take to perform iteration;
         * e.g. scrollspeed = 1 means 50 pixels will be moved for 1 second,
         * scrollspeed = 0.2 means 5 seconds.
         */
        if (moveAnimation.getStatus().equals(Animation.Status.RUNNING) ||
            scrollAnimation.getStatus().equals(Animation.Status.RUNNING))
            return;
        double scrollHeight = 2*oneLineHeight;
        double scrollPixPerSecond = 50; // 50 pixels per second.

        doIncreaseBottom(scrollHeight);

        double posY = textImagesContainer.getTranslateY();
        double posTo = posY + checkBoundaryConditions(- scrollHeight);
        if (posTo == posY)
            return;

        scrollAnimation.setFromY(posY);
        scrollAnimation.setToY(posTo);

        double speedMultiplier = settings.minorOptions.getScrollSpeed();
        double scrollDuration = scrollHeight/scrollPixPerSecond/speedMultiplier;
        scrollAnimation.setDuration(Duration.seconds(scrollDuration));
        scrollAnimation.setOnFinished(a -> {
            doAnimationScroll();
        });
        scrollAnimation.play();
    }
    private void doAnimationMove(double value, double seconds){
        if (moveAnimation.getStatus().equals(Animation.Status.RUNNING))
            return;

        // Continue scrolling after move animation.
        boolean isScrolling = false;
        if (scrollAnimation.getStatus() == Animation.Status.RUNNING)
            isScrolling = true;
        scrollAnimation.stop();

        if (value > 0) // animation Up/PgUp
            doIncreaseTop(value);
        else           // animation Down/PgDn
            doIncreaseBottom(-value);

        value = checkBoundaryConditions(value);
        double posY = textImagesContainer.getTranslateY();
        moveAnimation.setFromY(posY);
        moveAnimation.setToY(posY + value);
        moveAnimation.setDuration(Duration.seconds(seconds));
        if (isScrolling)
            moveAnimation.setOnFinished(ev -> doAnimationScroll());
        else
            moveAnimation.setOnFinished(ev -> {});

        moveAnimation.play();
    }

    private double checkBoundaryConditions(double distance){
        /*
         * @param distance: check whether top/bottom is reached after moving to this distance
         * from the current position; distance may be positive and negative.
         * @return: returns minimum of two values: the given distance and
         * the distance between current position and the bottom.
         */

        if (distance == 0)
            return 0;

        double newValue;
        if (distance > 0){
            // Image is moving bottom so check only top.
            if (textImagesContainer.getStartOffset() != 0)
                return distance; // top is not even processed

            double absPosY = Math.abs(textImagesContainer.getTranslateY());
            newValue = absPosY - distance < 0 ? absPosY : distance;
        }
        else{
            // Check only bottom.
            if (textImagesContainer.getEndOffset() != bookReader.textLength)
                return distance; // bottom is not even processed

            distance = Math.abs(distance);
            double absPosY = Math.abs(textImagesContainer.getTranslateY());

            double difference = textImagesContainer.getContainerHeight()
                    - absPosY
                    - visibleHeight;
            newValue = difference > distance ? - distance : - difference;
       }

        return newValue;
    }

    private void doIncreaseTop(double addHeight){
        /* Add elements on top.
         * @param addHeight: minimum height of the added images above visible area.
         */
        double imageHeight = visibleHeight/4;

        while (textImagesContainer.getTranslateY() > - addHeight){
            int firstPos = textImagesContainer.getStartOffset();
            if (firstPos == -1) // no images in the container
                return;
            if (firstPos == 0) // it is already top
                break;

            TextImage newImage = getPreviousPageImage(firstPos, imageHeight);
            textImagesContainer.addTopImage(newImage);
        }

        // Remove excessive parts.
        doAdjustBottom(oneLineHeight);

        updateCursor();
    }
    private void doAdjustTop(double addY){
        /*
         * Remove excessive top elements.
         * @param addY: a minimum positive length of additional elements
         * which is allowed on leave them on top. E.g. if addY = 0 than in ideal situation
         * there will be to top part.
         */
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
    private void doIncreaseBottom(double addHeight){
        /* Add elements on top.
         * @param addHeight: minimum height of the added images below visible area.
         */
        double containerY = textImagesContainer.getTranslateY();
        double maxHeight = visibleHeight + addHeight;
        double imageHeight = visibleHeight/4;

        while(containerY + textImagesContainer.getContainerHeight() < maxHeight){
            // Add elements on bottom.
            int lastPos = textImagesContainer.getEndOffset();
            if (lastPos == -1) // no images in the container
                return;
            if (lastPos >= bookReader.textLength) // it is already bottom
                break;
            textImagesContainer.addBottomImage(getNextPageImage(lastPos, imageHeight));
        }

        // Remove excessive parts.
        doAdjustTop(oneLineHeight);
        updateCursor();
    }
    private void doAdjustBottom(double addY){
        /*
         * Remove excessive bottom elements.
         * @param addY: a minimum positive length of additional elements
         * which is allowed on leave them on bottom. E.g. if addY = 0 than in ideal situation
         * there will be no bottom parts except in the visible range (in visibleHeight).
         */
        while(true){
            if (textImagesContainer.getChildren().size() < 2) // only 1 or 0 elements in vbox
                return;

            double bottomImageHeight = textImagesContainer.getBottomImageHeight();
            double boxHeight = textImagesContainer.getContainerHeight();
            if (textImagesContainer.getTranslateY() + boxHeight > visibleHeight + addY + bottomImageHeight){
                textImagesContainer.removeBottomImage();
            }
            else
                return;
        }
    }

    private TextImage getNextPageImage(int startPos, double imageHeight){
        /*
         * @param startPos: starting position of the to be image,
         * @param imageHeight: minimum height of the resulting TextImage,
         * @return: TextImage or null.
         */
        richText.setText("");
        String s = "";
        int newPos = startPos;

        while(true){
            newPos += s.length(); // it is starting position
            s = bookReader.getNextBlock(newPos);

            if (s == null) // text is finished
                break;
            richText.setText(richText.getText() + s);
            if (richText.getLayoutBounds().getHeight() >= imageHeight)
                break;
        }

        if (richText.getText().isEmpty())
            return null;
        return TextImageFactory.build(richText, startPos, visualQuality);
    }
    private TextImage getPreviousPageImage(int endPos, double imageHeight){
        /*
         * @param endPos: ending position of the to be image,
         * @param imageHeight: minimum height of the returned image
         * @return: TextImage or null.
         */
        richText.setText("");
        String s = "";
        while(true){
            s = bookReader.getPreviousBlock(endPos);
            if (s == null) // text is finished
                break;
            endPos -= s.length();
            richText.setText(s + richText.getText());
            if (richText.getLayoutBounds().getHeight() >= imageHeight)
                break;
        }
        if (richText.getText().isEmpty())
            return null;
        return TextImageFactory.build(richText, endPos, visualQuality);
    }
    private void updateCursor(){
        /*
         * This function updates the position of the cursor.
         * It is not precise and position may differ from what is seen on the screen.
         */
        int firstPos = textImagesContainer.getStartOffset();
        int lastPos = textImagesContainer.getEndOffset();
        int newPos = -1;

        if (lastPos == bookReader.textLength)
            newPos = lastPos;
        else if (firstPos == 0)
            newPos = 0;
        else
            newPos = textImagesContainer.getVisibleStartOffset();

        if (newPos != -1){
            settings.bookShelf.setOffsetPosition(newPos);
        }
    }
    private void rebuildScene(){
        /* Rebuilds currently visible scene.
         * This function can be used if size of the window is changed.
         */
        buildNewScene(textImagesContainer.getVisibleStartOffset());
    }
    private void buildNewScene(int offset){
        /* Build completely new scene at the given offset position.
         * @param offset: start position to show new image.
         */
        if (bookReader == null)
            return;

        moveAnimation.stop();
        scrollAnimation.stop();
        textImagesContainer.clear();

        if (offset < 0)
            textImagesContainer.addBottomImage(getNextPageImage(0, visibleHeight));
        else if (offset >= bookReader.textLength - visibleHeight){
            textImagesContainer.addBottomImage(
                    getPreviousPageImage(bookReader.textLength, visibleHeight));
            double diff = textImagesContainer.getContainerHeight() - visibleHeight;

            if (diff > 0)
                textImagesContainer.setTranslateY(-diff);
        }
        else
            textImagesContainer.addBottomImage(getNextPageImage(offset, visibleHeight));

        updateCursor();
    }
}
