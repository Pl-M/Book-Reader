/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.gui.toppane;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.bookjpreader.booksfactory.BookHandler;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.CurrentBook;
import pl.bookjpreader.commons.items.MinorOptions;
import pl.bookjpreader.commons.items.actions.OpenNewBookAction;
import pl.bookjpreader.commons.items.actions.SelectNewScrollSpeedAction;
import pl.bookjpreader.commons.items.actions.SelectNewTextPositionAction;
import pl.bookjpreader.commons.items.actions.StopAnimationAction;
import pl.bookjpreader.commons.items.actions.UpdateTextPositionAction;
import pl.bookjpreader.gui.MenuActions;
import pl.bookjpreader.gui.ViewController;

public class TopPaneController implements ViewController<TopPane> {
    private final TopPane topPane;

    public TopPaneController(final Stage stage) {
        topPane = new TopPane(stage);

        // Allow widget to grow and set its position.
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setLeftAnchor(topPane, 0.0);
        AnchorPane.setRightAnchor(topPane, 0.0);

        final ProgramRegistry registry = ProgramRegistry.INSTANCE;
        setInitialValues(registry);
        topPane.setPercentActionHandler(percent ->
            registry.submitAction(SelectNewTextPositionAction.class, percent));
        topPane.setSpeedActionHandler(speed ->
            registry.submitAction(SelectNewScrollSpeedAction.class, speed));
        topPane.setStopAnimationActionHandler(() ->
            registry.submitAction(StopAnimationAction.class));
        topPane.setMenuItems(MenuActions.getMenuItems(stage));

        registry.getForClass(OpenNewBookAction.class)
                .addListenerAfter(this::onOpenNewBook);
        registry.getForClass(UpdateTextPositionAction.class)
                .addListenerAfter(topPane::setPercentValue);
    }

    private void setInitialValues(final ProgramRegistry registry) {
        topPane.setScrollSpeedValue(registry
                .getForClass(MinorOptions.class).getScrollSpeed());
        onOpenNewBook(registry.getForClass(CurrentBook.class).getCurrentBookHandler());
    }

    @Override
    public TopPane getView() {
        return topPane;
    }

    private void onOpenNewBook(final BookHandler bookHandler) {
        if (bookHandler != null) {
            topPane.setPercentValue(bookHandler.getEntity().getPosition());
        }
    }

}
