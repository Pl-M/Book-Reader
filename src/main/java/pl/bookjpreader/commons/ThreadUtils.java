/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons;

import javafx.application.Platform;
import pl.bookjpreader.commons.items.RegistryElement;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Main class for multithreading programming.
 */
public class ThreadUtils implements Disposable, RegistryElement {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * This method can run in other threads.
     * @param run function to perform in FX Thread,
     */
    public static void submitToFXThreadAndWait(Runnable run){
        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                run.run();
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                ErrorHandler.handle(e);
            }
        }
    }

    public ExecutorService getExecutorService() {
        return executor;
    }

    @Override
    public void dispose() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ErrorHandler.handle(e);
        }
    }
}
