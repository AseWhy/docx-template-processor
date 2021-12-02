package io.github.asewhy.support.exceptions;

public class ProcessorException extends Exception {
    public ProcessorException(Throwable cause) {
        super(cause);
    }

    public ProcessorException(String message) {
        super(message);
    }
}
