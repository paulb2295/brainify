package com.bpx.brainify.exceptions;

public class ModuleNotFoundException extends RuntimeException{
    public ModuleNotFoundException(String message) {
        super(message);
    }
}
