package io.github.asewhy.support;

import io.github.asewhy.json.JsonGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class ProcessorArgumentResolver {
    private final Map<Class<?>, Object> datasets;
    private final ProcessorTypeProvider provider;

    /**
     * Автоматически разрешает пути до полей, на которых был повешен бинд в {@link ProcessorTypeProvider}
     *
     * @param provider провайдер типов, набор с полями для автоматического понимания на какой макрос
     *                 какое поле нужно получить
     */
    public ProcessorArgumentResolver(ProcessorTypeProvider provider) {
        this.provider = provider;
        this.datasets = new HashMap<>();
    }

    /**
     * Добавить данные в набор данных
     *
     * @param object объект для добавления
     * @return себя
     */
    public ProcessorArgumentResolver resolve(Object object) {
        datasets.put(object.getClass(), object); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param resolver другой набор данных
     * @return себя
     */
    public ProcessorArgumentResolver resolve(ProcessorArgumentResolver resolver) {
        this.datasets.putAll(resolver.datasets); return this;
    }

    /**
     * Получить значение поля по именной ссылке
     *
     * @param field название поля
     * @return значение поля если найдено
     * @throws IllegalAccessException если не найдено
     */
    public String resolve(String field) throws IllegalAccessException, InvocationTargetException {
        var found = this.provider.getBind(field);
        var clazz = this.provider.getClassForBind(field);

        if(found != null && clazz != null) {
            if(found instanceof Field) {
                var cast = (Field) found;

                if(!Modifier.isStatic(cast.getModifiers())) {
                    var data = datasets.get(clazz);

                    if (data != null) {
                        var access = cast.canAccess(data);

                        if(!access) {
                            cast.setAccessible(true);
                        }

                        var result = cast.get(data);

                        cast.setAccessible(access);

                        if (result != null) {
                            return result.toString();
                        } else {
                            return "";
                        }
                    }
                } else {
                    throw new IllegalAccessException("Field '" + field + "' is static and cannot be computed.");
                }

                throw new IllegalAccessException("Cannot find resolved dataset for field '" + field + "' for field '" + cast.getName() + "' in datasets " + getDebugDumpData());
            } else if(found instanceof Method) {
                var cast = (Method) found;
                var data = datasets.get(clazz);

                if(!Modifier.isStatic(cast.getModifiers())) {
                    if (data != null) {
                        var access = cast.canAccess(data);

                        if(!access) {
                            cast.setAccessible(true);
                        }

                        var result = cast.invoke(data);

                        cast.setAccessible(access);

                        if (result != null) {
                            return result.toString();
                        } else {
                            return "";
                        }
                    }
                } else {
                    throw new IllegalAccessException("Field '" + field + "' is static and cannot be called.");
                }

                throw new IllegalAccessException("Cannot find resolved dataset for field '" + field + "' for method '" + cast.getName() + "' in datasets " + getDebugDumpData());
            }
        }

        throw new IllegalAccessException("Cannot find provided type for field " + field);
    }

    /**
     * Создает json строку сообщение с данными в текущем наборе данных.
     */
    private String getDebugDumpData() {
        var gen = JsonGenerator.common().writeStartObject();

        for(var current: datasets.entrySet()) {
            var target = current.getKey();
            var superclass = target.getSuperclass();
            var interfaces = target.getInterfaces();
            var provided = current.getValue();
            var providedClass = provided.getClass();
            var fields = providedClass.getDeclaredFields();
            var methods = providedClass.getDeclaredMethods();

            gen.writeStartObject(target.getSimpleName());
            gen.writeField("class", target.getCanonicalName());

            if(superclass != null) {
                gen.writeField("extends", superclass.getCanonicalName());
            }

            if(interfaces.length > 0) {
                gen.writeStartArray("interfaces");

                for(var interf: interfaces) {
                    gen.write(interf.getCanonicalName());
                }

                gen.writeEndArray();
            }

            for(var field: fields) {
                try {
                    if(!Modifier.isStatic(field.getModifiers())) {
                        var accessible = field.canAccess(provided);
                        field.setAccessible(true);
                        gen.writeField(field.getName(), field.get(provided));
                        field.setAccessible(accessible);
                    }
                } catch (IllegalAccessException e) {
                    gen.writeField(field.getName(), "<cannot resolve value> ");
                }
            }

            for(var method: methods) {
                var args = method.getParameterTypes();
                var name = method.getName() + "(" + Arrays.stream(args).map(Class::getSimpleName).collect(Collectors.joining(", ")) + ")";
                gen.writeField(name, "{ /* Some Code Here */ }");
            }

            gen.writeEndObject();
        }

        return gen.writeEndObject().toString();
    }
}
