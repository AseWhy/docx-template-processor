package io.github.asewhy.processors;

import io.github.asewhy.ProcessorArgumentResolver;
import io.github.asewhy.ProcessorDataProvider;
import io.github.asewhy.ProcessorTypeProvider;
import io.github.asewhy.base.BaseSequenceMacrosProcessor;
import io.github.asewhy.interfaces.iDataResolver;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unused", "FieldCanBeLocal"})
public final class SequenceResolverMacrosProcessor extends BaseSequenceMacrosProcessor {
    private final ProcessorArgumentResolver resolver;
    private final ProcessorTypeProvider typeProvider;
    private final ProcessorDataProvider dataProvider;

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param dataProvider поставщик данных для {@link ProcessorArgumentResolver}
     * @param typeProvider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverMacrosProcessor(ProcessorDataProvider dataProvider, ProcessorTypeProvider typeProvider) {
        this(new ProcessorArgumentResolver(typeProvider, dataProvider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param dataProvider поставщик данных для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverMacrosProcessor(ProcessorDataProvider dataProvider) {
        this(new ProcessorArgumentResolver(dataProvider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param typeProvider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverMacrosProcessor(ProcessorTypeProvider typeProvider) {
        this(new ProcessorArgumentResolver(typeProvider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param resolver источник значений макросов
     */
    public SequenceResolverMacrosProcessor(ProcessorArgumentResolver resolver) {
        this.resolver = resolver;
        this.typeProvider = resolver.getTypeProvider();
        this.dataProvider = resolver.getDataProvider();
    }

    /**
     * Поставлять динамические данных
     *
     * @param clazz класс бинд для которого происходит
     * @param resolver поставщик данных для этого класса
     * @param <T> тип данных, к которому должен принадлежать и класс и обработчик
     * @return текущий поставщик данных
     */
    public <T> SequenceResolverMacrosProcessor resolve(Class<T> clazz, iDataResolver<T> resolver) {
        this.resolver.resolve(clazz, resolver); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param object объект для добавления
     * @return себя
     */
    public SequenceResolverMacrosProcessor resolve(Object object) {
        this.resolver.resolve(object); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param resolver другой набор данных
     * @return себя
     */
    public SequenceResolverMacrosProcessor resolve(ProcessorArgumentResolver resolver) {
        this.resolver.resolve(resolver); return this;
    }

    /**
     * Получить поддерживаемый обработчиком набор тегов
     *
     * @return поддерживаемый обработчиком набор тегов
     */
    @Override
    protected List<String> getSupportTagNames(String subspace) {
        return this.typeProvider.getSupportTagNames(subspace);
    }

    /**
     * Преобразовать поученное значение в строку
     *
     * @param value полученное значение
     * @return преобразованное значение
     */
    public String toString(Object value) {
        if(value == null) {
            return "";
        }

        if(value instanceof Number number) {
            if(value instanceof Float fl) {
                return String.format("%.2f", fl);
            } else if(value instanceof Double dbl) {
                return String.format("%.2f", dbl);
            } else {
                return String.valueOf(number);
            }
        }

        return value.toString();
    }

    /**
     * Получить значение макроса по ключу
     *
     * Примерно так это выглядит: key[index].subKey
     *
     * @param key ключ для получения корневого макроса
     * @param index индекс получения подмакроса
     * @param subKey поле получения подмакроса
     * @return значение макроса по ключу
     */
    @Override
    protected String getMacros(String key, Integer index, String subKey) {
        try {
            return toString(this.resolver.getIndexedValueOf(key, index, subKey));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить значение макроса по ключу
     *
     * @param key ключ для получения макроса
     * @return значение макроса по ключу
     */
    @Override
    protected String getMacros(String key)  {
        try {
            return toString(resolver.resolve(key));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Если по запрошенному адресу находится массив, то получает длину этого массива
     *
     * @param key ключ по которому нужно получить число строк в таблице
     * @return длину массива по ключу или 0, если полученный объект не массив, то -1
     */
    @Override
    protected Integer getTableRowCount(String key) {
        try {
            if(!typeProvider.isCollection(key)) {
                return -1;
            }

            var value = resolver.resolve(key);

            if(value instanceof Collection<?> collection) {
                return collection.size();
            } else {
                return 0;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
