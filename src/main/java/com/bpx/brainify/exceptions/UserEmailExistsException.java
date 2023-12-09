package com.bpx.brainify.exceptions;

public class UserEmailExistsException extends  RuntimeException{
    public UserEmailExistsException(String message) {
        super(message);
    }
}
