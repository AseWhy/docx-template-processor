package io.github.asewhy.support.base;

import io.github.asewhy.support.interfaces.iDocxProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.OutputStream;

@Log4j2
@Getter
@Setter
public abstract class BaseDocxProcessor implements iDocxProcessor {
    protected boolean loggable = false;

    @Override
    public void doProcess(OutputStream output, byte[] input) throws Exception {
        long start = System.currentTimeMillis();

        doProcessLoggable(output, input);

        if(loggable) {
            log.info("Template processing complete. Took " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

    /**
     * Если должна быть возможно вывода, то нужно переопределять этот метод
     *
     * @param output поток вывода
     * @param input входящий набор байтов
     * @throws Exception если при обработке произошла какая-то хрень
     */
    protected void doProcessLoggable(OutputStream output, byte[] input) throws Exception {

    }
}
