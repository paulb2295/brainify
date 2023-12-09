package com.bpx.brainify.exceptions;

public class UserHasNoCoursesException extends RuntimeException{
    public UserHasNoCoursesException(String message) {
        super(message);
    }
}
