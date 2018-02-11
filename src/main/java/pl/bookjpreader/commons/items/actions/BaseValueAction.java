/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.items.RegistryElement;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 Actions are needed to connect different element of the program.
 It is possible to listen to action.
 */
public abstract class BaseValueAction<T, V> implements RegistryElement {

    private final Set<Consumer<T>> listenersBefore = new HashSet<>();
    private final Set<Consumer<V>> listenersAfter = new HashSet<>();

    public void fire(final T newValue){
        if (newValue == null){
            return;
        }

        listenersBefore.forEach(el -> el.accept(newValue));
        final V result = execute(newValue);
        if (result != null) {
            listenersAfter.forEach(el -> el.accept(result));
        }
    }
    public void addListenerBefore(final Consumer<T> func){
        listenersBefore.add(func);
    }
    public void addListenerAfter(final Consumer<V> func){
        listenersAfter.add(func);
    }

    abstract V execute(final T newValue);

}