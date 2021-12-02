
package io.github.asewhy.support.processors;

import io.github.asewhy.support.base.BaseRegexpMacrosProcessor;
import org.intellij.lang.annotations.RegExp;

import java.util.function.Function;
import java.util.regex.Pattern;

public final class RegexpCallbackMacrosProcessor extends BaseRegexpMacrosProcessor {
    private final Function<String, String> receiver;

    /**
     * Callback обработчик docx файлов, обрабатывает содержимое docx с помощью callback функции, первым аргументом которой
     * передается название макроса, которая возвращает значение макроса.
     *
     * @param regex регулярное выражение для обработки макросов
     * @param receiver функция обработчик
     */
    public RegexpCallbackMacrosProcessor(@RegExp String regex, Function<String, String> receiver) {
        this(Pattern.compile(regex), receiver);
    }

    /**
     * Callback обработчик docx файлов, обрабатывает содержимое docx с помощью callback функции, первым аргументом которой
     * передается название макроса, которая возвращает значение макроса.
     *
     * @param regex регулярное выражение для обработки макросов
     * @param receiver функция обработчик
     */
    public RegexpCallbackMacrosProcessor(Pattern regex, Function<String, String> receiver) {
        super(regex);

        this.receiver = receiver;
    }

    /**
     * Получить значение макроса по ключу
     *
     * @param key ключ для получения макроса
     * @return значение макроса по ключу
     */
    @Override
    protected String getMacros(String key) {
        return receiver.apply(key);
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
