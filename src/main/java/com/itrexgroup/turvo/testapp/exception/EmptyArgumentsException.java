package com.itrexgroup.turvo.testapp.exception;

/**
 * Created by maxim.babrovich on 11.04.2019.
 */
public class EmptyArgumentsException extends RuntimeException {

    public EmptyArgumentsException() {
    }

    public EmptyArgumentsException(String message) {
        super(message);
    }

    public EmptyArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyArgumentsException(Throwable cause) {
        super(cause);
    }

    public EmptyArgumentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
