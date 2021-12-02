package io.github.asewhy.support.interfaces;

import java.io.OutputStream;

public interface iMacrosProcessor {
    /**
     * Обрабатывает данные которые поданы на вход как массив байтов
     *
     * @param output поток вывода обработчика
     * @param input входной массив байтов
     * @throws Exception если в процессе обработки произошли ошибки
     */
    void doProcess(OutputStream output, byte[] input) throws Exception;

    /**
     * Проверяет, может ли обработчик обработать файл находящийся по пути filename и содержащий контент input.
     *
     * @param filename путь до файла
     * @param input входящий массив байтов
     * @return true если обработка возможна
     */
    boolean canProcess(String filename, byte[] input);
}
