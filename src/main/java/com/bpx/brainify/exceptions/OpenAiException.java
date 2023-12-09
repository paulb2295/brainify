package com.bpx.brainify.exceptions;

public class OpenAiException extends RuntimeException{
    public OpenAiException(String message) {
        super(message);
    }
}
