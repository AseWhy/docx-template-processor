package io.github.asewhy.base;

import io.github.asewhy.support.exceptions.ProcessorException;
import org.jetbrains.annotations.NotNull;
import io.github.asewhy.support.interfaces.iMacrosProcessor;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class ZipFileProcessor {
    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param path путь до файла который необходимо обработать
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected ByteArrayOutputStream process(@NotNull Path path, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        var output = new ByteArrayOutputStream();
        process(path, processor, output);
        return output;
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param path путь до файла который необходимо обработать
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected ByteArrayOutputStream process(String path, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        var output = new ByteArrayOutputStream();
        process(path, processor, output);
        return output;
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param source файл который необходимо обработать
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected ByteArrayOutputStream process(File source, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        var output = new ByteArrayOutputStream();
        process(source, processor, output);
        return output;
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param source источник (поток чтения zip файла)
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected ByteArrayOutputStream process(InputStream source, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        var output = new ByteArrayOutputStream();
        process(source, processor, output);
        return output;
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param path путь до файла который необходимо обработать
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected void process(@NotNull Path path, @NotNull iMacrosProcessor processor, OutputStream output) throws ProcessorException, IOException {
        process(path.toFile(), processor, output);
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param path путь до файла который необходимо обработать
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected void process(@NotNull String path, @NotNull iMacrosProcessor processor, OutputStream output) throws ProcessorException, IOException {
        process(new File(path), processor, output);
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param source файл который необходимо обработать
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected void process(@NotNull File source, @NotNull iMacrosProcessor processor, OutputStream output) throws ProcessorException, IOException {
        process(new FileInputStream(source), processor, output);
    }

    /**
     * Выполнить обработку документа в zip формате на валидность
     *
     * @param source источник (поток чтения zip файла)
     * @param processor обработчик
     * @throws ProcessorException если при обработке файла произошли какие-либо ошибки
     */
    protected void process(InputStream source, @NotNull iMacrosProcessor processor, OutputStream output) throws ProcessorException, IOException {
        var filesToRestore = new HashMap<String, byte[]>();
        var zipFileReader = new ByteArrayOutputStream();
        var entry = (ZipEntry) null;

        try(var zipInputStream = new ZipInputStream(source)) {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                var name = entry.getName();

                zipFileReader.reset();

                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    zipFileReader.write(c);
                }

                var buffer = zipFileReader.toByteArray();

                if (processor.canProcess(name, buffer)) {
                    zipFileReader.reset();

                    processor.doProcess(zipFileReader, buffer);

                    buffer = zipFileReader.toByteArray();
                }

                filesToRestore.put(name, buffer);
                zipInputStream.closeEntry();
            }
        }

        try(var zipOutputStream = new ZipOutputStream(output)) {
            for(var current: filesToRestore.entrySet()) {
                var currentFile = new ZipEntry(current.getKey());
                zipOutputStream.putNextEntry(currentFile);
                zipOutputStream.write(current.getValue());
                zipOutputStream.closeEntry();
            }
        }
    }

    /**
     * Выполнить проверку документа в zip формате на валидность
     *
     * @param path путь до проверяемого файла
     * @param processor обработчик
     * @return список тегов, найденных в проверяемом шаблоне, если все ок
     * @throws ProcessorException если валидация не прошла успешно
     */
    protected List<String> validate(@NotNull Path path, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        return validate(path.toFile(), processor);
    }

    /**
     * Выполнить проверку документа в zip формате на валидность
     *
     * @param path путь до проверяемого файла
     * @param processor обработчик
     * @return список тегов, найденных в проверяемом шаблоне, если все ок
     * @throws ProcessorException если валидация не прошла успешно
     */
    protected List<String> validate(@NotNull String path, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        return validate(new File(path), processor);
    }

    /**
     * Выполнить проверку документа в zip формате на валидность
     *
     * @param source проверяемый файл
     * @param processor обработчик
     * @return список тегов, найденных в проверяемом шаблоне, если все ок
     * @throws ProcessorException если валидация не прошла успешно
     */
    protected List<String> validate(@NotNull File source, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        return validate(new FileInputStream(source), processor);
    }

    /**
     * Выполнить проверку документа в zip формате на валидность
     *
     * @param source источник (поток чтения zip файла)
     * @param processor обработчик
     * @return список тегов, найденных в проверяемом шаблоне, если все ок
     * @throws ProcessorException если валидация не прошла успешно
     */
    protected List<String> validate(InputStream source, @NotNull iMacrosProcessor processor) throws ProcessorException, IOException {
        var zipFileReader = new ByteArrayOutputStream();
        var foundTags = new ArrayList<String>();
        var entry = (ZipEntry) null;

        try(var zipInputStream = new ZipInputStream(source)) {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                var name = entry.getName();

                zipFileReader.reset();

                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    zipFileReader.write(c);
                }

                var buffer = zipFileReader.toByteArray();

                if (processor.canProcess(name, buffer)) {
                    zipFileReader.reset();

                    foundTags.addAll(processor.doValidate(buffer));
                }

                zipInputStream.closeEntry();
            }
        }

        return foundTags;
    }
}
