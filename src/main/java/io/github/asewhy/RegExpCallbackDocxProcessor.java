package io.github.asewhy;

import io.github.asewhy.base.ZipFileProcessor;
import io.github.asewhy.support.exceptions.ProcessorException;
import io.github.asewhy.support.processors.RegexpCallbackMacrosProcessor;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class RegExpCallbackDocxProcessor extends ZipFileProcessor {
    private final RegexpCallbackMacrosProcessor processor;

    /**
     * Обрабатывает содержимое docx файла с помощью callback функции
     *
     * @param regexp регулярное выражение для группировки макросов
     * @param callback функция для обработки
     */
    public RegExpCallbackDocxProcessor(@RegExp String regexp, Function<String, String> callback) {
        this.processor = new RegexpCallbackMacrosProcessor(regexp, callback);
    }

    /**
     * Обрабатывает содержимое docx файла с помощью callback функции
     *
     * @param regexp регулярное выражение для группировки макросов
     * @param callback функция для обработки
     */
    public RegExpCallbackDocxProcessor(Pattern regexp, Function<String, String> callback) {
        this.processor = new RegexpCallbackMacrosProcessor(regexp, callback);
    }

    /**
     * Обрабатывает содержимое docx файла с помощью callback функции
     *
     * @param processor обработчик
     */
    public RegExpCallbackDocxProcessor(RegexpCallbackMacrosProcessor processor) {
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
