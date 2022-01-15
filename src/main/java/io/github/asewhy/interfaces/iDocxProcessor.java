package io.github.asewhy.interfaces;

import io.github.asewhy.exceptions.ProcessorException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.*;
import java.util.Collection;

@SuppressWarnings("unused")
public interface iDocxProcessor {
    /**
     * Обрабатывает шаблон, внося в него корректировки в соответствии с тегами
     *
     * @param template шаблон для обработки
     * @throws ProcessorException если при обработке возникли ошибки (тег не закрыт, или некорректно указан например)
     */
    void doProcess(WordprocessingMLPackage template) throws ProcessorException;

    /**
     * Начать обработку шаблона
     *
     * @param input файл входящего документа
     * @param output поток выхода документа
     * @throws ProcessorException если в процессе обработки документа произошли ошибки
     */
    default void doProcess(File input, OutputStream output) throws ProcessorException {
        try {
            doProcess(new FileInputStream(input), output);
        } catch (FileNotFoundException e) {
            throw new ProcessorException(e);
        }
    }

    /**
     * Начать обработку шаблона
     *
     * @param input поток входящего документа
     * @param output поток выхода документа
     * @throws ProcessorException если в процессе обработки документа произошли ошибки
     */
    default void doProcess(InputStream input, OutputStream output) throws ProcessorException {
        try {
            var template = WordprocessingMLPackage.load(input);

            doProcess(template);

            template.save(output);
        } catch (Docx4JException e) {
            throw new ProcessorException(e);
        }
    }

    /**
     * Проверить шаблон на наличие ошибок, и вернуть теги используемые в нем
     *
     * @param template шаблон
     * @return теги затронутые шаблоном
     * @throws ProcessorException если при проверке возникли ошибки (тег не закрыт, или некорректно указан например)
     */
    Collection<String> doValidate(WordprocessingMLPackage template) throws ProcessorException;

    /**
     * Начать проверку файла шаблона
     *
     * @param input поток входящего документа
     * @throws ProcessorException если в процессе обработки документа произошли ошибки
     */
    default Collection<String> doValidate(File input) throws ProcessorException {
        try {
            return doValidate(new FileInputStream(input));
        } catch (FileNotFoundException e) {
            throw new ProcessorException(e);
        }
    }

    /**
     * Начать проверку шаблона
     *
     * @param input поток входящего документа
     * @throws ProcessorException если в процессе обработки документа произошли ошибки
     */
    default Collection<String> doValidate(InputStream input) throws ProcessorException {
        try {
            return doValidate(WordprocessingMLPackage.load(input));
        } catch (Docx4JException e) {
            throw new ProcessorException(e);
        }
    }
}
