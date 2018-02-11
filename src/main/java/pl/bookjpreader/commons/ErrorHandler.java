/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons;

import java.util.function.BiConsumer;

public class ErrorHandler {
    private static BiConsumer<String, Throwable> errorHandler = null;

    public static void handle(final Throwable e) {
        String message = "An error occurred during running the program.";
        handle(message, e);
    }

    public static void setErrorHandler(BiConsumer<String, Throwable> handler) {
        errorHandler = handler;
    }

    public static void handle(final String message, final Throwable e) {
        if (errorHandler != null) {
            errorHandler.accept(message, e);
        } else {
            e.printStackTrace();
        }
    }
}
