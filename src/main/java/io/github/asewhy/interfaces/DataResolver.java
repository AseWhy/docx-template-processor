package io.github.asewhy.interfaces;

import java.util.function.Supplier;

public interface DataResolver<T> extends Supplier<T> {
    T get();
}
