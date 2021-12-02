package io.github.asewhy;

import io.github.asewhy.base.ZipFileProcessor;
import io.github.asewhy.support.ProcessorArgumentResolver;
import io.github.asewhy.support.ProcessorTypeProvider;
import io.github.asewhy.support.processors.RegexpResolverMacrosProcessor;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class RegExpResolverDocxProcessor extends ZipFileProcessor {
    private final RegexpResolverMacrosProcessor processor;

    /**
     * Обработчик docx файлов в соответствии со своим {@link RegExpResolverDocxProcessor#processor} аргументом
     *
     * @param provider поставщик типов для обработчика
     * @param regexp шаблон для обработчика
     */
    public RegExpResolverDocxProcessor(Pattern regexp, ProcessorTypeProvider provider) {
        this.processor = new RegexpResolverMacrosProcessor(regexp, provider);
    }

    /**
     * Обработчик docx файлов в соответствии со своим {@link RegExpResolverDocxProcessor#processor} аргументом
     *
     * @param provider поставщик типов для обработчика
     * @param regexp шаблон для обработчика
     */
    public RegExpResolverDocxProcessor(@RegExp String regexp, ProcessorTypeProvider provider) {
        this.processor = new RegexpResolverMacrosProcessor(Pattern.compile(regexp), provider);
    }

    /**
     * Обработчик docx файлов в соответствии со своим {@link RegExpResolverDocxProcessor#processor} аргументом
     *
     * @param processor обработчик
     */
    public RegExpResolverDocxProcessor(RegexpResolverMacrosProcessor processor) {
        this.processor = processor;
    }

    /**
     * Режим вывода в консось сколько выполнялась задача по преобразованию шаблона
     *
     * @param loggable true если необходимо выводить время выполнения
     */
    public void setLoggable(Boolean loggable) {
        this.processor.setLoggable(loggable);
    }

    /**
     * Обрабатывает docx файл, заменяет теги и макросы в нем в соответствии с биндами в {@link ProcessorTypeProvider}
     *
     * @param path путь до docx файла
     * @return выходной поток, с обработанным docx файлом
     * @throws Exception в процессе обработки могут возникнуть оказии
     */
    public ByteArrayOutputStream process(@NotNull Path path) throws Exception {
        return super.process(path, processor);
    }

    /**
     * Обрабатывает docx файл, заменяет теги и макросы в нем в соответствии с биндами в {@link ProcessorTypeProvider}
     *
     * @param source файл для обработки
     * @return выходной поток, с обработанным docx файлом
     * @throws Exception в процессе обработки могут возникнуть оказии
     */
    public ByteArrayOutputStream process(File source) throws Exception {
        return super.process(source, processor);
    }

    /**
     * Обрабатывает docx файл, заменяет теги и макросы в нем в соответствии с биндами в {@link ProcessorTypeProvider}
     *
     * @param path путь до docx файла
     * @return выходной поток, с обработанным docx файлом
     * @throws Exception в процессе обработки могут возникнуть оказии
     */
    public ByteArrayOutputStream process(String path) throws Exception {
        return super.process(path, processor);
    }

    /**
     * Добавить данные в набор данных обработчика
     *
     * @param object какой-либо объект, если он его тип был ранее забинжен в {@link ProcessorTypeProvider} то
     *               обработчик автоматически про него узнает.
     * @return себя
     */
    public RegExpResolverDocxProcessor resolve(Object object) {
        this.processor.resolve(object); return this;
    }

    /**
     * Добавить данные в набор данных обработчика
     *
     * @param resolver другой обработчик, данные в этом случае будут объеденены
     * @return себя
     */
    public RegExpResolverDocxProcessor resolve(ProcessorArgumentResolver resolver) {
        this.processor.resolve(resolver); return this;
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param path путь до файла
     * @param output поток для вывода
     * @throws Exception если в процессе обработки произошли ошибки
     */
    public void process(@NotNull Path path, OutputStream output) throws Exception {
        super.process(path, processor, output);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param source файл для обработки
     * @param output поток для вывода
     * @throws Exception если в процессе обработки произошли ошибки
     */
    public void process(File source, OutputStream output) throws Exception {
        super.process(source, processor, output);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param path путь до файла
     * @param output поток для вывода
     * @throws Exception если в процессе обработки произошли ошибки
     */
    public void process(String path, OutputStream output) throws Exception {
        super.process(path, processor, output);
    }
}
