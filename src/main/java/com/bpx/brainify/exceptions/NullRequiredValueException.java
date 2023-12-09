package com.bpx.brainify.exceptions;

public class NullRequiredValueException extends RuntimeException{
    public NullRequiredValueException(String message) {
        super(message);
    }
}
