package io.github.asewhy.support.processors;

import io.github.asewhy.support.ProcessorArgumentResolver;
import io.github.asewhy.support.ProcessorTypeProvider;
import io.github.asewhy.support.base.BaseSequenceMacrosProcessor;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("UnusedReturnValue")
public final class SequenceResolverMacrosProcessor extends BaseSequenceMacrosProcessor {
    private final ProcessorArgumentResolver resolver;

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param openTag открывающий тег для поиска
     * @param closeTag закрывающий тег для поиска
     * @param provider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverMacrosProcessor(String openTag, String closeTag, ProcessorTypeProvider provider) {
        this(openTag, closeTag, new ProcessorArgumentResolver(provider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param commonData теги для поиска разделенные | прямой чертой, пример: [|]
     * @param provider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverMacrosProcessor(String commonData, ProcessorTypeProvider provider) {
        this(commonData, new ProcessorArgumentResolver(provider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param openTag открывающий тег для поиска
     * @param closeTag закрывающий тег для поиска
     * @param resolver источник значений макросов
     */
    public SequenceResolverMacrosProcessor(String openTag, String closeTag, ProcessorArgumentResolver resolver) {
        super(openTag, closeTag);

        this.resolver = resolver;
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param commonData теги для поиска разделенные | прямой чертой, пример: [|]
     * @param resolver источник значений макросов
     */
    public SequenceResolverMacrosProcessor(String commonData, ProcessorArgumentResolver resolver) {
        super(commonData);

        this.resolver = resolver;
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
     * Получить значение макроса по ключу
     *
     * @param key ключ для получения макроса
     * @return значение макроса по ключу
     * @throws IllegalAccessException если случалось что-то
     * @throws InvocationTargetException если случалось что-то
     */
    @Override
    protected byte[] getMacros(String key) throws IllegalAccessException, InvocationTargetException {
        var result = resolver.resolve(key);

        if(result != null) {
            return result.getBytes();
        } else {
            return new byte[] {};
        }
    }
}
