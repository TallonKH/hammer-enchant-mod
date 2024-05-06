package com.frogedev.hammer_enchant.util;

import com.google.common.collect.AbstractIterator;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class MappedIterator<F, T> extends AbstractIterator<T> {
    @FunctionalInterface
    public interface Mapping<F, T> {
        T map(F input);
    }

    Iterator<F> source;
    Mapping<F, T> mapping;

    public MappedIterator(Iterator<F> source, Mapping<F, T> mapping) {
        this.source = source;
        this.mapping = mapping;
    }

    @Nullable
    @Override
    protected T computeNext() {
        if (this.source.hasNext()) {
            return this.mapping.map(this.source.next());
        } else {
            return this.endOfData();
        }
    }
}
