package com.bpx.brainify.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }
        return new ResponseEntity<>(objectToString(errors), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String defaultMessage = Objects.requireNonNull(error.getDefaultMessage());
            errors.put(error.getField(), defaultMessage);
        });
        return new ResponseEntity<>(objectToString(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OpenAiException.class)
    public ResponseEntity<String> openAiException(OpenAiException openAiException) {
        return new ResponseEntity<>(objectToString(Map.of("message", openAiException.getMessage())), SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InputOutputException.class)
    public ResponseEntity<String> improperInputException(InputOutputException inputOutputException) {
        return new ResponseEntity<>(objectToString(Map.of("message", inputOutputException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> courseNoFoundException(ResourceNotFoundException resourceNotFoundException) {
        return new ResponseEntity<>(objectToString(Map.of("message", resourceNotFoundException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(UserHasNoCoursesException.class)
    public ResponseEntity<String> userHasNoCourses(UserHasNoCoursesException userHasNoCoursesException) {
        return new ResponseEntity<>(objectToString(Map.of("message", userHasNoCoursesException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    public ResponseEntity<String> moduleNotFound(ModuleNotFoundException moduleNotFoundException) {
        return new ResponseEntity<>(objectToString(Map.of("message", moduleNotFoundException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(NullRequiredValueException.class)
    public ResponseEntity<String> nullRequiredValue(NullRequiredValueException nullRequiredValueException) {
        return new ResponseEntity<>(objectToString(Map.of("message", nullRequiredValueException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(InvalidNumberOfQuestionsException.class)
    public ResponseEntity<String> invalidNumberOfQuestions(InvalidNumberOfQuestionsException invalidNumberOfQuestionsException) {
        return new ResponseEntity<>(objectToString(Map.of("message", invalidNumberOfQuestionsException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(NoQuestionsForChapterException.class)
    public ResponseEntity<String> noQuestionsForChapter(NoQuestionsForChapterException noQuestionsForChapterException) {
        return new ResponseEntity<>(objectToString(Map.of("message", noQuestionsForChapterException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(UserEmailExistsException.class)
    public ResponseEntity<String> userEmailExists(UserEmailExistsException userEmailExistsException) {
        return new ResponseEntity<>(objectToString(Map.of("message", userEmailExistsException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(WrongEmailOrPasswordException.class)
    public ResponseEntity<String> wrongEmailOrPassword (WrongEmailOrPasswordException wrongEmailOrPasswordException) {
        return new ResponseEntity<>(objectToString(Map.of("message", wrongEmailOrPasswordException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(PasswordChangeException.class)
    public ResponseEntity<String> passwordChangeException (PasswordChangeException passwordChangeException) {
        return new ResponseEntity<>(objectToString(Map.of("message", passwordChangeException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<String> invalidRoleException (InvalidRoleException invalidRoleException) {
        return new ResponseEntity<>(objectToString(Map.of("message", invalidRoleException.getMessage())), BAD_REQUEST);
    }
    @ExceptionHandler(InvalidFilterParametersException.class)
    public ResponseEntity<String> invalidFilterParametersException (InvalidFilterParametersException invalidFilterParametersException) {
        return new ResponseEntity<>(objectToString(Map.of("message", invalidFilterParametersException.getMessage())), BAD_REQUEST);
    }

    private String objectToString(Object response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException exception) {
            return "Internal error";
        }
    }
}
