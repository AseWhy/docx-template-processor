package io.github.asewhy.processors;

import io.github.asewhy.ProcessorArgumentResolver;
import io.github.asewhy.ProcessorDataProvider;
import io.github.asewhy.ProcessorTypeProvider;
import io.github.asewhy.base.BaseSequenceTagProcessor;
import io.github.asewhy.interfaces.iDataResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unused", "FieldCanBeLocal"})
public final class SequenceResolveTagProcessor extends BaseSequenceTagProcessor {
    private final ProcessorArgumentResolver resolver;
    private final ProcessorTypeProvider typeProvider;
    private final ProcessorDataProvider dataProvider;

    /**
     * Regexp обработчик, получает значение тегов из {@link ProcessorArgumentResolver}
     *
     * @param dataProvider поставщик данных для {@link ProcessorArgumentResolver}
     * @param typeProvider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolveTagProcessor(ProcessorDataProvider dataProvider, ProcessorTypeProvider typeProvider) {
        this(new ProcessorArgumentResolver(typeProvider, dataProvider));
    }

    /**
     * Regexp обработчик, получает значение тегов из {@link ProcessorArgumentResolver}
     *
     * @param dataProvider поставщик данных для {@link ProcessorArgumentResolver}
     */
    public SequenceResolveTagProcessor(ProcessorDataProvider dataProvider) {
        this(new ProcessorArgumentResolver(dataProvider));
    }

    /**
     * Regexp обработчик, получает значение тегов из {@link ProcessorArgumentResolver}
     *
     * @param typeProvider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolveTagProcessor(ProcessorTypeProvider typeProvider) {
        this(new ProcessorArgumentResolver(typeProvider));
    }

    /**
     * Regexp обработчик, получает значение тегов из {@link ProcessorArgumentResolver}
     *
     * @param resolver источник значений тегов
     */
    public SequenceResolveTagProcessor(@NotNull ProcessorArgumentResolver resolver) {
        this.resolver = resolver;
        this.typeProvider = resolver.getTypeProvider();
        this.dataProvider = resolver.getDataProvider();
    }

    /**
     * Получить динамические данные из поставщика, для получения данные должны поставлятся поставщиком.
     *
     * @param clazz тип, по которому нужно получить данные из поставщика
     * @param <T> тип данных для получения
     * @return найденные данные, если они ранее не поставлялись то null
     */
    public <T> T resolve(Class<T> clazz) {
        return this.resolver.resolve(clazz);
    }

    /**
     * Поставлять динамические данных
     *
     * @param clazz класс бинд для которого происходит
     * @param resolver поставщик данных для этого класса
     * @param <T> тип данных, к которому должен принадлежать и класс и обработчик
     * @return текущий поставщик данных
     */
    public <T> SequenceResolveTagProcessor provide(Class<T> clazz, iDataResolver<T> resolver) {
        this.resolver.provide(clazz, resolver); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param object объект для добавления
     * @return себя
     */
    public SequenceResolveTagProcessor provide(Object object) {
        this.resolver.provide(object); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param resolver другой набор данных
     * @return себя
     */
    public SequenceResolveTagProcessor provide(ProcessorArgumentResolver resolver) {
        this.resolver.provide(resolver); return this;
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

        if(value instanceof Number) {
            if(value instanceof Float) {
                return String.format("%.2f", value);
            } else if(value instanceof Double) {
                return String.format("%.2f", value);
            } else {
                return String.valueOf(value);
            }
        }

        return value.toString();
    }

    /**
     * Получить значение тега по ключу
     *
     * Примерно так это выглядит: key[index].subKey
     *
     * @param key ключ для получения корневого тега
     * @param index индекс получения подтега
     * @param subKey поле получения подтега
     * @return значение тега по ключу
     */
    @Override
    protected String getTag(String key, Integer index, String subKey) {
        try {
            return toString(this.resolver.getIndexedValueOf(key, index, subKey));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить значение тега по ключу
     *
     * @param key ключ для получения тега
     * @return значение тега по ключу
     */
    @Override
    protected String getTag(String key)  {
        try {
            return toString(resolver.resolve(key));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Проверить, является ли бинд тега итерируемым
     *
     * @param key тег по которому нужно проверить
     * @return true если является
     */
    @Override
    protected Boolean isIterableTag(String key) {
        return typeProvider.isCollection(key);
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
            if(!isIterableTag(key)) {
                return -1;
            }

            var value = resolver.resolve(key);

            if(value instanceof Collection<?>) {
                return ((Collection<?>) value).size();
            } else {
                return 0;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
