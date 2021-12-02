package io.github.asewhy.support.interfaces;

import io.github.asewhy.support.exceptions.ProcessorException;

import java.io.OutputStream;
import java.util.List;

public interface iMacrosProcessor {
    /**
     * Обрабатывает данные которые поданы на вход как массив байтов
     *
     * @param output поток вывода обработчика
     * @param input входной массив байтов
     * @throws Exception если в процессе обработки произошли ошибки
     */
    void doProcess(OutputStream output, byte[] input) throws ProcessorException;

    /**
     * Проверяет, может ли обработчик обработать файл находящийся по пути filename и содержащий контент input.
     *
     * @param filename путь до файла
     * @param input входящий массив байтов
     * @return true если обработка возможна
     */
    boolean canProcess(String filename, byte[] input);

    /**
     * Проверяет шаблон на предмет ошибок
     *
     * @param input входящий массив байтов
     * @return список тегов, найденных в проверяемом шаблоне, если все ок
     */
    default List<String> doValidate(byte[] input) throws ProcessorException {
        return List.of();
    }
}
