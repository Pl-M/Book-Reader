/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import pl.bookjpreader.commons.items.RegistryElement;
import pl.bookjpreader.commons.items.actions.BaseSimpleAction;
import pl.bookjpreader.commons.items.actions.BaseValueAction;

/**
 * This class is a common repository for the program.
 * It is singleton.
 * To register a new element go to {@link ProgramConfigurator}.
 * Other parts of the program can work with settings via this class.
 */
public enum ProgramRegistry implements Disposable{
    INSTANCE;

    private final Map<Class<?>, RegistryElement> items = new HashMap<>();
    private ResourceBundle resBundle;

    public void register(final RegistryElement item) {
        // TODO: add logging
        RegistryElement oldItem = items.getOrDefault(item.getClass(), null);
        if (oldItem != null) {
            unRegister(oldItem);
            // TODO: add logging
        }
        items.put(item.getClass(), item);
    }

    public void unRegister(final RegistryElement item) {
        // TODO: add logging
        items.remove(item.getClass());
        if (item instanceof Disposable) {
            ((Disposable) item).dispose();
        }
    }

    public <T> T getForClass(final Class<T> cls) {
        return cls.cast(items.getOrDefault(cls, null));
    }

    /**
     * @return all items extending given cls.
     */
    public <T> List<T> getForSuperClass(final Class<T> cls) {
        final List<T> result = new ArrayList<>();
        items.forEach((clazz, element) -> {
            if (cls.isAssignableFrom(clazz)) {
                result.add(cls.cast(element));
            }
        });
        return result;
    }

    public String getResourceFor(final String key) {
        if (resBundle != null) {
            return resBundle.getString(key);
        }
        return key;
    }

    public <T extends BaseSimpleAction> void submitAction(final Class<T> clazz) {
        T action = getForClass(clazz);
        if (action != null) {
            action.fire();
        } else {
            ErrorHandler.handle("No action found.", new NullPointerException());
        }
    }
    public <T extends BaseValueAction<V, ?>, V> void submitAction(
            final Class<T> clazz, final V newValue) {
        T action = getForClass(clazz);
        if (action != null) {
            action.fire(newValue);
        } else {
            ErrorHandler.handle("No action found.", new NullPointerException());
        }
    }

    public void registerResource(final ResourceBundle resBundle) {
        this.resBundle = resBundle;
    }

    @Override
    public void dispose() {
        items.values().forEach(el -> {
            if (el instanceof Disposable) {
                ((Disposable) el).dispose();
            }
        });
    }

}
