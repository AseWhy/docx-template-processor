package io.github.asewhy.support;

import lombok.Getter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Getter
public class TreeResult {
    private final AccessibleObject accessible;
    private final Class<?> clazz;

    public TreeResult(AccessibleObject accessible) {
        this.accessible = accessible;

        if(accessible instanceof Field) {
            this.clazz = ((Field) accessible).getType();
        } else if(accessible instanceof Method) {
            this.clazz = ((Method) accessible).getReturnType();
        } else {
            throw new RuntimeException("Unknown accessible type");
        }
    }
}
