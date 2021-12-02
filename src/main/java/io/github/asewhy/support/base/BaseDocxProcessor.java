package io.github.asewhy.support.base;

import io.github.asewhy.support.exceptions.ProcessorException;
import io.github.asewhy.support.interfaces.iDocxProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.OutputStream;
import java.util.List;

@Log4j2
@Getter
@Setter
public abstract class BaseDocxProcessor implements iDocxProcessor {
    protected boolean loggable = false;

    @Override
    public void doProcess(OutputStream output, byte[] input) throws ProcessorException {
        long start = System.currentTimeMillis();

        doProcessLoggable(output, input);

        if(loggable) {
            log.info("Template processing complete. Took " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

    @Override
    public List<String> doValidate(byte[] input) throws ProcessorException {
        long start = System.currentTimeMillis();

        var result = doValidateLoggable(input);

        if(loggable) {
            log.info("Template processing complete. Took " + (System.currentTimeMillis() - start) + "ms.");
        }

        return result;
    }

    /**
     * Если нужно, чтобы по завершении обработки выводилось количество времени затраченное на обработку,
     * то нужно переопределить этот метод
     *
     * @param output поток вывода
     * @param input входящий набор байтов
     * @throws ProcessorException если при обработке произошла какая-то хрень
     */
    protected void doProcessLoggable(OutputStream output, byte[] input) throws ProcessorException {

    }

    /**
     * Если нужно, чтобы по завершении проверки выводилось количество времени затраченное на проверку,
     * то нужно переопределить этот метод
     *
     * @param input входящий набор байтов
     * @throws ProcessorException если при обработке произошла какая-то хрень
     */
    protected List<String> doValidateLoggable(byte[] input) throws ProcessorException {
        return List.of();
    }
}
