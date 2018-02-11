/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.commons.items.RegistryElement;

import java.util.HashSet;
import java.util.Set;

/**
 Actions are needed to connect different elements of the program.
 It is possible to listen to action.
 */
public abstract class BaseSimpleAction implements RegistryElement {

    private final Set<Runnable> listenersBefore = new HashSet<>();
    private final Set<Runnable> listenersAfter = new HashSet<>();

    public void fire(){
        listenersBefore.forEach(Runnable::run);
        execute();
        listenersAfter.forEach(Runnable::run);
    }
    public void addListenerBefore(final Runnable func){
        listenersBefore.add(func);
    }
    public void addListenerAfter(final Runnable func){
        listenersAfter.add(func);
    }

    abstract void execute();

}
