package com.bpx.brainify.exceptions;

public class NoQuestionsForChapterException extends RuntimeException{
    public NoQuestionsForChapterException(String message) {
        super(message);
    }
}
