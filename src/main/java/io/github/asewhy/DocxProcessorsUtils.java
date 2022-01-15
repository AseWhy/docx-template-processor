package io.github.asewhy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class DocxProcessorsUtils {
    /**
     * Получить первый generic параметр у поля
     *
     * @param from поле, generic значение которого нужно получить
     * @return generic тип или nell
     */
    public static @Nullable Class<?> findXGeneric(@NotNull Field from) {
        var genericsParams = from.getGenericType();

        if(genericsParams instanceof ParameterizedType pt) {
            var generics = pt.getActualTypeArguments();

            if(generics.length > 0) {
                var generic = generics[0];

                if(generic instanceof Class<?> clazz) {
                    return clazz;
                } else if (generic instanceof ParameterizedType c) {
                    var type = c.getRawType();

                    if (type instanceof Class<?> clazz) {
                        return clazz;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Получить первый generic параметр у поля
     *
     * @param from поле, generic значение которого нужно получить
     * @return generic тип или nell
     */
    public static @Nullable Class<?> findXGeneric(@NotNull Method from) {
        var genericsParams = from.getGenericReturnType();

        if(genericsParams instanceof ParameterizedType pt) {
            var generics = pt.getActualTypeArguments();

            if(generics.length > 0) {
                var generic = generics[0];

                if(generic instanceof Class<?> clazz) {
                    return clazz;
                } else if (generic instanceof ParameterizedType c) {
                    var type = c.getRawType();

                    if (type instanceof Class<?> clazz) {
                        return clazz;
                    }
                }
            }
        }

        return null;
    }

}
