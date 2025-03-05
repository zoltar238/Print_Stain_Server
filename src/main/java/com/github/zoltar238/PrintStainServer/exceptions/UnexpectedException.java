package com.github.zoltar238.PrintStainServer.exceptions;

public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message, Throwable exception) {
        super(message, exception);
    }
}
