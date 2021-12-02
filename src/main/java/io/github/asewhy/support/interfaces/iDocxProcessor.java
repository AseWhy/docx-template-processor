package io.github.asewhy.support.interfaces;

import io.github.asewhy.support.exceptions.ProcessorException;

import java.io.OutputStream;

public interface iDocxProcessor extends iMacrosProcessor {
    @Override
    void doProcess(OutputStream output, byte[] input) throws ProcessorException;

    @Override
    default boolean canProcess(String filename, byte[] input) {
        return isMainPartName(filename);
    }

    //
    // В docx файлах весь контент лежит тут
    //
    default boolean isMainPartName(String with) {
        return "word/document.xml".equals(with);
    }
}
