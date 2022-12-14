package io.github.asewhy;

import io.github.asewhy.interfaces.DataResolver;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class ProcessorArgumentResolver {
    private final Map<String, Object> datacache;

    @Getter
    private final ProcessorDataProvider dataProvider;

    @Getter
    private final ProcessorTypeProvider typeProvider;

    /**
     * Автоматически разрешает пути до полей, на которых был повешен бинд в {@link ProcessorTypeProvider}
     *
     * @param dataProvider поставщик данныз, набор с классами, и функциями, по которым можно получить значение этого класса
     *                     позволяет динамически запрашивать данные
     * @param typeProvider поставщик типов, набор с полями для автоматического понимания на какой тег
     *                 какое поле нужно получить
     */
    public ProcessorArgumentResolver(ProcessorTypeProvider typeProvider, ProcessorDataProvider dataProvider) {
        this.typeProvider = typeProvider;
        this.dataProvider = dataProvider;
        this.datacache = new HashMap<>();
    }

    /**
     * Автоматически разрешает пути до полей, на которых был повешен бинд в {@link ProcessorTypeProvider}
     *
     * @param typeProvider поставщик типов, набор с полями для автоматического понимания на какой тег
     *                 какое поле нужно получить
     */
    public ProcessorArgumentResolver(ProcessorTypeProvider typeProvider) {
        this(typeProvider, new ProcessorDataProvider());
    }

    /**
     * Автоматически разрешает пути до полей, на которых был повешен бинд в {@link ProcessorTypeProvider}
     *
     * @param dataProvider поставщик данныз, набор с классами, и функциями, по которым можно получить значение этого класса
     *                     позволяет динамически запрашивать данные
     */
    public ProcessorArgumentResolver(ProcessorDataProvider dataProvider) {
        this(new ProcessorTypeProvider(), dataProvider);
    }

    /**
     * Получить динамические данные из поставщика, для получения данные должны поставлятся поставщиком.
     *
     * @param clazz тип, по которому нужно получить данные из поставщика
     * @param <T> тип данных для получения
     * @return найденные данные, если они ранее не поставлялись то null
     */
    public <T> T resolve(Class<T> clazz) {
        return dataProvider.resolve(clazz);
    }

    /**
     * Поставлять динамические данных
     *
     * @param clazz класс бинд для которого происходит
     * @param resolver поставщик данных для этого класса
     * @param <T> тип данных, к которому должен принадлежать и класс и обработчик
     * @return текущий поставщик данных
     */
    public <T> ProcessorArgumentResolver provide(Class<T> clazz, DataResolver<T> resolver) {
        dataProvider.provide(clazz, resolver); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param object объект для добавления
     * @return себя
     */
    public ProcessorArgumentResolver provide(Object object) {
        dataProvider.provide(ReflectionUtils.skipAnonClasses(object.getClass()), object); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param resolver другой набор данных
     * @return себя
     */
    public ProcessorArgumentResolver provide(@NotNull ProcessorArgumentResolver resolver) {
        dataProvider.provide(resolver.dataProvider); return this;
    }

    /**
     * Получить свойство от объекта object
     *
     * @param data данные, из которых нужно получить свойство
     * @param cast поле, которое нужно получить от объекта data
     * @return значение поля
     */
    private @Nullable Object getPropertyOfObject(Object data, @NotNull Field cast) {
        try {
            var access = cast.canAccess(data);

            if(!access) {
                cast.setAccessible(true);
            }

            var result = cast.get(data);

            cast.setAccessible(access);

            return result;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Получить значение выполненного метода от объекта
     *
     * @param data объект для получения значения
     * @param cast метод, вызовя который можно будет получить значение
     * @return значение выполненного метода
     */
    private @Nullable Object getMethodResultOfObject(Object data, @NotNull Method cast) {
        try {
            var access = cast.canAccess(data);

            if(!access) {
                cast.setAccessible(true);
            }

            var result = cast.invoke(data);

            cast.setAccessible(access);

            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error when call " + cast.getName());
        }
    }

    /**
     * Получить данные из поля или функции
     *
     * @param field название получаемого значения
     * @param data данные
     * @param found объект поля или функции
     * @return значение результата выполнения функции или значение поля
     * @throws IllegalAccessException в случае ошибки
     */
    private Object getDataOfAccessible(String field, Object data, AccessibleObject found) throws IllegalAccessException {
        if(found instanceof Field) {
            var cast = (Field) found;

            if(!Modifier.isStatic(cast.getModifiers())) {
                if (data != null) {
                    var result = getPropertyOfObject(data, cast);

                    datacache.put(field, result);

                    return result;
                }
            } else {
                throw new IllegalAccessException("Field '" + field + "' is static and cannot be computed.");
            }

            throw new IllegalAccessException("Cannot find resolved dataset for field '" + field + "' for field '" + cast.getName() + "' in datasets " + this.dataProvider.getDebugDumpData());
        } else if(found instanceof Method) {
            var cast = (Method) found;

            if(!Modifier.isStatic(cast.getModifiers())) {
                if (data != null) {
                    var result = getMethodResultOfObject(data, cast);

                    datacache.put(field, result);

                    return result;
                }
            } else {
                throw new IllegalAccessException("Field '" + field + "' is static and cannot be called.");
            }

            throw new IllegalAccessException("Cannot find resolved dataset for field '" + field + "' for method '" + cast.getName() + "' in datasets " + this.dataProvider.getDebugDumpData());
        }

        throw new IllegalAccessException("Cannot find provided type for field '" + field + "'");
    }

    /**
     * Получить под значение индексируемого поля
     *
     * @param rootKey название поля корневого объекта
     * @param index индекс для получения
     * @param subKey название поля объекта полученного по индексу
     * @return знание поля subKey объекта
     */
    public Object getIndexedValueOf(String rootKey, Integer index, String subKey) throws InvocationTargetException, IllegalAccessException {
        //
        // Значение индекса списка (т.к. значение по индексу можно получить только из списка)
        //
        var listKey = rootKey + "#list";
        //
        // Значение кеша (если к свойству надо будет получить доступ ещё раз в ближайшее время)
        //
        var cacheKey = listKey + "#" + index + "#" + subKey;

        //
        // Возвращаю значение из кеша если есть
        //
        if(datacache.containsKey(cacheKey)) {
            return datacache.get(cacheKey);
        }

        var root = resolve(rootKey);

        //
        // Нельзя получить значение по индексу от null)
        //
        if(root == null) {
            throw new IllegalAccessException("Cannot find provided type for field '" + cacheKey + "'");
        }

        if(root instanceof Collection<?>) {
            if(!datacache.containsKey(listKey)) {
                datacache.put(listKey, new ArrayList<>((Collection<?>) root));
            }
        }

        var found = this.typeProvider.getBind(getSubPropertyIndex(rootKey, subKey));
        var list = (List<?>) datacache.get(listKey);

        if(list != null && list.size() > index && found != null) {
            return getDataOfAccessible(cacheKey, list.get(index), found);
        }

        throw new IllegalAccessException("Cannot find provided type for field '" + cacheKey + "'");
    }

    /**
     * Получить значение поля по именной ссылке
     *
     * @param field название поля
     * @return значение поля если найдено
     * @throws IllegalAccessException если не найдено
     */
    public Object resolve(String field) throws IllegalAccessException, InvocationTargetException {
        if(datacache.containsKey(field)) {
            return datacache.get(field);
        }

        var found = this.typeProvider.getBind(field);
        var clazz = this.typeProvider.getClassForBind(field);

        if(found != null && clazz != null) {
            return getDataOfAccessible(field, dataProvider.resolve(clazz), found);
        }

        throw new IllegalAccessException("Cannot find provided type for field '" + field + "'");
    }

    /**
     * Получить индекс под свойства
     *
     * @param rootKey название корневого свойства
     * @param subKey название дочернего свойство
     * @return их общий индекс
     */
    @Contract(pure = true)
    public static @NotNull String getSubPropertyIndex(String rootKey, String subKey) {
        return rootKey + "#" + subKey;
    }
}
