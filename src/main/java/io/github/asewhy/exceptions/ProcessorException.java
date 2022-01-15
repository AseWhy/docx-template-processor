package io.github.asewhy.exceptions;

public class ProcessorException extends Exception {
    public ProcessorException(Throwable cause) {
        super(cause);
    }

    public ProcessorException(String message) {
        super(message);
    }
}
