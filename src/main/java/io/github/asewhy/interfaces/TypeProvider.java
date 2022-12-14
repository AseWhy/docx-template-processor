package io.github.asewhy.interfaces;

import io.github.asewhy.support.DescriptionEntry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Map;

public interface TypeProvider {
    void provide(
        @NotNull Map<String, Class<?>> classes,
        @NotNull Map<String, Class<?>> resultClasses,
        @NotNull Map<String, List<String>> subspaces,
        @NotNull Map<String, AccessibleObject> binds,
        @NotNull Map<String, Map<String, DescriptionEntry>> descriptions
    );
}
