package io.github.asewhy.support.base;

import io.github.asewhy.support.exceptions.ProcessorException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseSequenceMacrosProcessor extends BaseXmlMacrosProcessor {
    private final byte[] openGroupMarker;
    private final byte[] closeGroupMarker;
    private final int openGroupMarkerLength;
    private final int closeGroupMarkerLength;

    /**
     * Создает обработчик с заданием стартового шаблона и конечного шаблона
     *
     * @param openGroupMarker стартовый тег для начала шаблона
     * @param closeGroupMarker конечный тег для конца шаблона
     */
    public BaseSequenceMacrosProcessor(String openGroupMarker, String closeGroupMarker) {
        assert openGroupMarker != null;
        assert closeGroupMarker != null;

        this.openGroupMarker = openGroupMarker.getBytes();
        this.closeGroupMarker = closeGroupMarker.getBytes();
        this.openGroupMarkerLength = this.openGroupMarker.length;
        this.closeGroupMarkerLength = this.closeGroupMarker.length;
    }

    /**
     * Создает новый обработчик по общему шаблону
     *
     * @param commonData общий шаблон
     */
    public BaseSequenceMacrosProcessor(String commonData) {
        assert commonData != null;
        var split = commonData.split("\\|");

        if(split.length > 1) {
            this.openGroupMarker = split[0].getBytes();
            this.closeGroupMarker = split[1].getBytes();
            this.openGroupMarkerLength = this.openGroupMarker.length;
            this.closeGroupMarkerLength = this.closeGroupMarker.length;
        } else {
            throw new IllegalArgumentException("The commonData argument must be split | on two parts start and end marker. Ex: {|}");
        }
    }

    /**
     * Создает базовый обработчик
     */
    public BaseSequenceMacrosProcessor() {
        this("%|%");
    }

    protected abstract byte[] getMacros(String key) throws IllegalAccessException, InvocationTargetException;

    protected String supply(byte[] bytes, int start, int end) throws ProcessorException {
        return new String(stripTags(bytes, start, end));
    }

    @Override
    public void doProcessLoggable(OutputStream output, byte[] bytes) throws ProcessorException {
        int length = bytes.length, marker = 0,
            currentStartIdx, currentEndIdx, lastFoundEndIdx = 0;

        try {
            while(marker != -1 && marker < length) {
                currentStartIdx = indexOf(bytes, openGroupMarker, marker);

                if (currentStartIdx != -1) {
                    output.write(bytes, marker, currentStartIdx - marker);

                    marker = currentStartIdx + openGroupMarkerLength;
                    currentEndIdx = indexOf(bytes, closeGroupMarker, marker);

                    if (currentEndIdx != -1) {
                        output.write(getMacros(supply(bytes, marker, currentEndIdx)));

                        lastFoundEndIdx = marker = currentEndIdx + openGroupMarkerLength;
                    } else{
                        throw new ProcessorException("Cannot find close marker for index " + marker + " expected [" + new String(closeGroupMarker) + "] for " + new String(stripTags(bytes, currentStartIdx, length)));
                    }
                } else {
                    marker = -1;
                }
            }

            if(lastFoundEndIdx != 0 ) {
                output.write(bytes, lastFoundEndIdx, length - lastFoundEndIdx);
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new ProcessorException(e);
        }
    }

    @Override
    public List<String> doValidate(byte[] bytes) throws ProcessorException {
        var tags = new ArrayList<String>();

        int length = bytes.length, marker = 0,
            currentStartIdx, currentEndIdx, lastFoundEndIdx = 0;

        while(marker != -1 && marker < length) {
            currentStartIdx = indexOf(bytes, openGroupMarker, marker);

            if (currentStartIdx != -1) {
                marker = currentStartIdx + openGroupMarkerLength;
                currentEndIdx = indexOf(bytes, closeGroupMarker, marker);

                if (currentEndIdx != -1) {
                    tags.add(supply(bytes, marker, currentEndIdx));

                    marker = currentEndIdx + openGroupMarkerLength;
                } else{
                    throw new ProcessorException("Cannot find close marker for index " + marker + " expected [" + new String(closeGroupMarker) + "] for " + new String(stripTags(bytes, currentStartIdx, length)));
                }
            } else {
                marker = -1;
            }
        }

        return tags;
    }
}
