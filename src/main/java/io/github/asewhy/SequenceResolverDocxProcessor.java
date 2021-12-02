package io.github.asewhy;

import io.github.asewhy.base.ZipFileProcessor;
import io.github.asewhy.support.ProcessorArgumentResolver;
import io.github.asewhy.support.ProcessorTypeProvider;
import io.github.asewhy.support.exceptions.ProcessorException;
import io.github.asewhy.support.processors.SequenceResolverMacrosProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
public final class SequenceResolverDocxProcessor extends ZipFileProcessor {
    private final SequenceResolverMacrosProcessor processor;

    /**
     * Обработчик docx файлов в соответствии со своим {@link SequenceResolverDocxProcessor#processor} аргументом
     *
     * @param openTag открывающий тег для поиска
     * @param closeTag закрывающий тег для поиска
     * @param provider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverDocxProcessor(String openTag, String closeTag, ProcessorTypeProvider provider) {
        this.processor = new SequenceResolverMacrosProcessor(openTag, closeTag, provider);
    }

    /**
     * Обработчик docx файлов в соответствии со своим {@link SequenceResolverDocxProcessor#processor} аргументом
     *
     * @param commonData теги для поиска разделенные | прямой чертой, пример: [|]
     * @param provider поставщик типов для {@link ProcessorArgumentResolver}
     */
    public SequenceResolverDocxProcessor(String commonData, ProcessorTypeProvider provider) {
        this.processor = new SequenceResolverMacrosProcessor(commonData, provider);
    }

    /**
     * Обработчик docx файлов в соответствии со своим {@link SequenceResolverDocxProcessor#processor} аргументом
     *
     * @param processor обработчик
     */
    public SequenceResolverDocxProcessor(SequenceResolverMacrosProcessor processor) {
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
     * Добавить данные в набор данных обработчика
     *
     * @param object какой-либо объект, если он его тип был ранее забинжен в {@link ProcessorTypeProvider} то
     *               обработчик автоматически про него узнает.
     * @return себя
     */
    public SequenceResolverDocxProcessor resolve(Object object) {
        this.processor.resolve(object); return this;
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param path путь до файла
     * @return поток с обработанными данными
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public ByteArrayOutputStream process(@NotNull Path path) throws ProcessorException, IOException {
        return super.process(path, processor);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param source файл для обработки
     * @return поток с обработанными данными
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public ByteArrayOutputStream process(File source) throws ProcessorException, IOException {
        return super.process(source, processor);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param path путь до файла
     * @return поток с обработанными данными
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public ByteArrayOutputStream process(String path) throws ProcessorException, IOException {
        return super.process(path, processor);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param source входящий поток для обработки
     * @return поток с обработанными данными
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public ByteArrayOutputStream process(InputStream source) throws ProcessorException, IOException {
        return super.process(source, processor);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param path путь до файла
     * @param output поток для вывода
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public void process(@NotNull Path path, OutputStream output) throws ProcessorException, IOException {
        super.process(path, processor, output);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param source файл для обработки
     * @param output поток для вывода
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public void process(File source, OutputStream output) throws ProcessorException, IOException {
        super.process(source, processor, output);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param path путь до файла
     * @param output поток для вывода
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public void process(String path, OutputStream output) throws ProcessorException, IOException {
        super.process(path, processor, output);
    }

    /**
     * Начинает обработку файла по заданному пути
     *
     * @param source входящий поток для обработки
     * @param output поток для вывода
     * @throws ProcessorException если в процессе обработки произошли ошибки
     */
    public void process(InputStream source, OutputStream output) throws ProcessorException, IOException {
        super.process(source, processor, output);
    }

    /**
     * Начинает проверку файла по заданному пути
     *
     * @param path путь до файла
     * @throws ProcessorException если в процессе проверки произошли ошибки (проверка не прошла успешно)
     * @return список тегов, используемых в проверяемом шаблоне, если все ок
     */
    public List<String> validate(@NotNull Path path) throws ProcessorException, IOException {
        return super.validate(path, processor);
    }

    /**
     * Начинает проверку файла по заданному пути
     *
     * @param source файл для проверки
     * @throws ProcessorException если в процессе проверки произошли ошибки (проверка не прошла успешно)
     * @return список тегов, используемых в проверяемом шаблоне, если все ок
     */
    public List<String> validate(File source) throws ProcessorException, IOException {
        return super.validate(source, processor);
    }

    /**
     * Начинает проверку файла по заданному пути
     *
     * @param path путь до файла
     * @throws ProcessorException если в процессе проверки произошли ошибки (проверка не прошла успешно)
     * @return список тегов, используемых в проверяемом шаблоне, если все ок
     */
    public List<String> validate(String path) throws ProcessorException, IOException {
        return super.validate(path, processor);
    }

    /**
     * Начинает проверку файла по заданному пути
     *
     * @param source входящий поток для проверки
     * @throws ProcessorException если в процессе проверки произошли ошибки (проверка не прошла успешно)
     * @return список тегов, используемых в проверяемом шаблоне, если все ок
     */
    public List<String> validate(InputStream source) throws ProcessorException, IOException {
        return super.validate(source, processor);
    }
}
