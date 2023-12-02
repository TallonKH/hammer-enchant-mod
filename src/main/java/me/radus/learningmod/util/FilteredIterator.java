package me.radus.learningmod.util;

import com.google.common.collect.AbstractIterator;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class FilteredIterator<T> extends AbstractIterator<T> {
    @FunctionalInterface
    public interface Predicate<T> {
        boolean apply(T item);
    }

    Iterator<T> iterator;
    Predicate<T> predicate;

    public FilteredIterator(Iterator<T> iter, Predicate<T> pred) {
        this.iterator = iter;
        this.predicate = pred;
    }

    @Nullable
    @Override
    protected T computeNext() {
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (predicate.apply(item)) {
                return item;
            }
        }

        return this.endOfData();
    }
}
