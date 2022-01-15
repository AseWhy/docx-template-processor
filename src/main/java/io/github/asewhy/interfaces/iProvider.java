package io.github.asewhy.interfaces;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Map;

public interface iProvider {
    void provide(
        Map<String, Class<?>> classes,
        Map<String, Class<?>> resultClasses,
        Map<String, List<String>> subspaces,
        @NotNull Map<String, AccessibleObject> binds
    );
}
