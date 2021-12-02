package io.github.asewhy.support.processors;

import io.github.asewhy.support.ProcessorArgumentResolver;
import io.github.asewhy.support.ProcessorTypeProvider;
import io.github.asewhy.support.base.BaseRegexpMacrosProcessor;
import org.intellij.lang.annotations.RegExp;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedReturnValue")
public final class RegexpResolverMacrosProcessor extends BaseRegexpMacrosProcessor {
    private final ProcessorArgumentResolver resolver;

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param regexp регулярное выражение для поиска
     * @param provider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public RegexpResolverMacrosProcessor(@RegExp String regexp, ProcessorTypeProvider provider) {
        this(regexp, new ProcessorArgumentResolver(provider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param regexp регулярное выражение для поиска
     * @param provider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public RegexpResolverMacrosProcessor(Pattern regexp, ProcessorTypeProvider provider) {
        this(regexp, new ProcessorArgumentResolver(provider));
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param regexp регулярное выражение для поиска
     * @param resolver источник значений макросов
     */
    public RegexpResolverMacrosProcessor(@RegExp String regexp, ProcessorArgumentResolver resolver) {
        this(Pattern.compile(regexp), resolver);
    }

    /**
     * Regexp обработчик, получает значение макросов из {@link ProcessorArgumentResolver}
     *
     * @param regexp регулярное выражение для поиска
     * @param resolver источник значений макросов
     */
    public RegexpResolverMacrosProcessor(Pattern regexp, ProcessorArgumentResolver resolver) {
        super(regexp);

        this.resolver = resolver;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param object объект для добавления
     * @return себя
     */
    public RegexpResolverMacrosProcessor resolve(Object object) {
        this.resolver.resolve(object); return this;
    }

    /**
     * Добавить данные в набор данных
     *
     * @param resolver другой набор данных
     * @return себя
     */
    public RegexpResolverMacrosProcessor resolve(ProcessorArgumentResolver resolver) {
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
    protected String getMacros(String key) throws IllegalAccessException, InvocationTargetException {
        return resolver.resolve(key);
    }

    /**
     * Получить regexp группу захвата
     *
     * @return группу захвата
     */
    @Override
    protected int getCaptureGroup() {
        return 1;
    }
}
