/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons;

import java.util.*;

import pl.bookjpreader.commons.items.RegistryElement;

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

    public void register(RegistryElement item) {
        // TODO: add logging
        RegistryElement oldItem = items.getOrDefault(item.getClass(), null);
        if (oldItem != null) {
            unRegister(oldItem);
            // TODO: add logging
        }
        items.put(item.getClass(), item);
    }

    public void unRegister(RegistryElement item) {
        // TODO: add logging
        items.remove(item.getClass());
    }

    public <T> T getForClass(Class<T> cls) {
        return cls.cast(items.getOrDefault(cls, null));
    }

    /**
     * @return all items extending given cls.
     */
    public <T> List<T> getForSuperClass(Class<T> cls) {
        final List<T> result = new ArrayList<>();
        for (Map.Entry<Class<?>, RegistryElement> item : items.entrySet()){
            if (cls.isAssignableFrom(item.getKey())) {
                result.add(cls.cast(item.getValue()));
            }
        }
        return result;
    }

    public void registerResource(final ResourceBundle resBundle) {
        this.resBundle = resBundle;
    }

    public String getResourceFor(String key) {
        if (resBundle != null) {
            return resBundle.getString(key);
        }
        return key;
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
