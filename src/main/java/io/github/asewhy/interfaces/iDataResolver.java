package io.github.asewhy.interfaces;

import java.util.function.Supplier;

public interface iDataResolver<T> extends Supplier<T> {
    T get();
}
