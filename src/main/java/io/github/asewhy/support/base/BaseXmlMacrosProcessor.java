package io.github.asewhy.support.base;

import java.util.Arrays;

public abstract class BaseXmlMacrosProcessor extends BaseDocxProcessor {
    protected final static byte xmlTagOpen = '<';
    protected final static byte xmlClose = '>';
    protected final static int bufferCapacityStep = 32;

    /**
     * Убирает xml теги из последовательности байтов (по идее сложность x, хз что IDE ругается)
     *
     * @param input входящая последовательность
     * @param from индекс с которого нужно вырезать кусок
     * @param to индекс по который нужно вырезать кусок
     * @return вырезка байтов без тегов
     */
    protected static byte[] stripTags(byte[] input, int from, int to) {
        byte[] buffer = new byte[] {};
        boolean inTagRem = false;
        int index = Math.max(from, 0) - 1,
            length = Math.min(to, input.length),
            bufferIndex = 0,
            bufferCapacity = 0;

        while(++index < length) {
            var current = input[index];

            if(current == xmlTagOpen) {
                inTagRem = true;
            } else if(current == xmlClose) {
                inTagRem = false;

                continue;
            }

            if(!inTagRem) {
                if(bufferIndex >= bufferCapacity) {
                    buffer = Arrays.copyOf(buffer, bufferCapacity += bufferCapacityStep);
                }

                buffer[bufferIndex++] = current;
            }
        }

        return Arrays.copyOf(buffer, bufferIndex);
    }

    /**
     * Получает индекс последовательности байтов
     *
     * @param bytes входящая последовательность
     * @param search искомая последовательность
     * @param start индекс начала поиска
     * @return индекс найденной искомой последовательности
     */
    protected static int indexOf(byte[] bytes, byte[] search, int start) {
        int index = Math.max(start, 0) - 1,
            coincidenceIdx = 0,
            totalLength = bytes.length,
            searchLength = search.length;

        while(++index < totalLength) {
            var current = bytes[index];

            if(current == search[coincidenceIdx]) {
                coincidenceIdx++;
            } else {
                coincidenceIdx = 0;
            }

            if(coincidenceIdx >= searchLength) {
                return index - coincidenceIdx + 1;
            }
        }

        return -1;
    }
}