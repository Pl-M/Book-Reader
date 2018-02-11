/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.gui.bookview;


import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pl.bookjpreader.booksfactory.BookHandler;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.ThreadUtils;
import pl.bookjpreader.commons.items.CurrentBook;
import pl.bookjpreader.commons.items.DisplayOptions;
import pl.bookjpreader.commons.items.MinorOptions;
import pl.bookjpreader.commons.items.actions.*;
import pl.bookjpreader.gui.ViewController;
import pl.bookjpreader.gui.bookview.textwidget.TextImage;
import pl.bookjpreader.gui.bookview.textwidget.TextImageFactory;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

public final class BookViewController implements ViewController<BookView> {

    private final BookView bookViewWidget;
    private DisplayOptions displayOptions = null;
    private BookHandler bookHandler = null;

    public BookViewController() {
        final ProgramRegistry registry = ProgramRegistry.INSTANCE;

        final ExecutorService executorService = registry.getForClass(ThreadUtils.class).getExecutorService();
        bookViewWidget = new BookView(createTextImageSupplier(),
                createTextImageAsyncSupplier(executorService));

        configureBookView(registry);
        setInitialValues(registry);
    }
    @Override
    public BookView getView() {
        return bookViewWidget;
    }

    private void setInitialValues(final ProgramRegistry registry) {
        onChangeDisplaySettings(registry.getForClass(DisplayOptions.class));
        onOpenNewBook(registry.getForClass(CurrentBook.class).getCurrentBookHandler());
        bookViewWidget.setScrollSpeed(registry.getForClass(MinorOptions.class).getScrollSpeed());
    }
    private void configureBookView(final ProgramRegistry registry) {

        bookViewWidget.setTextPositionUpdateHandler(newOffsetPos -> {
            final double percentPos = bookHandler.getPercentFromOffset(newOffsetPos);
            registry.getForClass(UpdateTextPositionAction.class).fire(percentPos);
        });
        bookViewWidget.setTextSelectionActionHandler(text -> {
            if (text != null) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(text);
                clipboard.setContent(content);
            }
        });

        registry.getForClass(SelectNewScrollSpeedAction.class)
                .addListenerAfter(bookViewWidget::setScrollSpeed);
        registry.getForClass(OpenNewBookAction.class)
                .addListenerAfter(this::onOpenNewBook);
        registry.getForClass(SelectNewTextPositionAction.class)
                .addListenerAfter(newPercentPos -> {
                    final int offsetPos =
                            bookHandler.getOffsetFromPercent(newPercentPos);
                    bookViewWidget.redraw(offsetPos);
                });
        registry.getForClass(StopAnimationAction.class)
                .addListenerAfter(bookViewWidget::stopAnimation);
        registry.getForClass(UpdateDisplayOptionsAction.class)
                .addListenerAfter(this::onChangeDisplaySettings);
    }
    private void onOpenNewBook(final BookHandler bookHandler) {
        if (bookHandler != null) {
            this.bookHandler = bookHandler;
            final int offsetPos = bookHandler.getOffsetFromPercent(
                    bookHandler.getEntity().getPosition());
            bookViewWidget.redraw(offsetPos);
        }
    }
    private void onChangeDisplaySettings(final DisplayOptions opts) {
        displayOptions = opts;
        bookViewWidget.setBorders(opts.getHIndentation(), opts.getVIndentation());
        bookViewWidget.setLineSpacing(opts.getLineSpacing());

        final Text textNode = createNewText(opts, 100, opts.getLineSpacing());
        bookViewWidget.setSingleLineHeight(textNode.getLayoutBounds().getHeight());

        bookViewWidget.redraw();
    }

    private Text createNewText(final DisplayOptions opts, final double width, final double lineSpacing) {
        final Text richText = new Text();
        richText.setFill(opts.getTextColor());
        richText.setFontSmoothingType(FontSmoothingType.GRAY); // to smooth font
        richText.setFont(opts.getTextFont());
        richText.setTextAlignment(TextAlignment.JUSTIFY);

        final double oneLineHeight = richText.getLayoutBounds().getHeight();
        richText.setLineSpacing(oneLineHeight * lineSpacing);
        //richText.setSmooth(true);

        // Set width of the text.
        richText.setWrappingWidth(width);

        return richText;
    }
    private Function<TextImageParametersBuilder, TextImage> createTextImageSupplier() {
        return (params) -> {
            if (displayOptions == null || bookHandler == null) return null;

            final int offset;
            final double maxHeight = params.getHeight();
            final double lineSpacing = params.getLineSpacing();
            final Text textNode = createNewText(displayOptions, params.getWidth(), lineSpacing);
            TextImage textImage = null;

            if (params.getDirection() == TextImageParametersBuilder.Direction.BEGIN){
                // begin image
                offset = 0;
            } else if (params.getDirection() == TextImageParametersBuilder.Direction.END) {
                // end image
                offset = bookHandler.getTextLength();
            } else {
                offset = params.getStartOffset();
            }

            int newStartOffset = 0;
            if (params.getDirection() == TextImageParametersBuilder.Direction.NEXT
                    || params.getDirection() == TextImageParametersBuilder.Direction.BEGIN) {
                // next image
                if (offset != bookHandler.getTextLength()) { // not end of file
                    String strText = "";
                    int newPos = offset;

                    while (true) {
                        newPos += strText.length(); // it is starting position
                        strText = bookHandler.getNextBlock(newPos);
                        if (strText == null) // text is finished
                            break;
                        textNode.setText(textNode.getText() + strText);
                        if (textNode.getLayoutBounds().getHeight() >= maxHeight)
                            break;
                    }
                    newStartOffset = offset;
                }
            }
            else if (offset != 0) { // not beginning of file
                String strText = "";
                int endPos = offset;

                while (true) {
                    strText = bookHandler.getPreviousBlock(endPos);
                    if (strText == null) // text is finished
                        break;
                    endPos -= strText.length();
                    textNode.setText(strText + textNode.getText());
                    if (textNode.getLayoutBounds().getHeight() >= maxHeight)
                        break;
                }
                newStartOffset = endPos;
            }

            if (!textNode.getText().isEmpty()) {
                textImage = TextImageFactory.build(
                        textNode, newStartOffset, displayOptions.getQuality());
            }
            return textImage;
        };
    }

    private Function<TextImageParametersBuilder, Future<TextImage>> createTextImageAsyncSupplier(
            final ExecutorService executor) {
        return (params) -> executor.submit(() -> createTextImageSupplier().apply(params));
    }
}
