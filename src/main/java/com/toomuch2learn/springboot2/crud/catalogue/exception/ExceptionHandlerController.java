package com.toomuch2learn.springboot2.crud.catalogue.exception;

import com.toomuch2learn.springboot2.crud.catalogue.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerController {

    Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse onNoHandlerFound(NoHandlerFoundException exception, WebRequest request) {
        log.error(String.format("Handler %s not found", request.getDescription(false)));

        ErrorResponse response = new ErrorResponse();
        response.getErrors().add(
            new Error(
                ErrorCodes.ERR_HANDLER_NOT_FOUND,
        "Handler not found",
                String.format("Handler %s not found",request.getDescription(false))));

        return response;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse onResourceFound(ResourceNotFoundException exception, WebRequest request) {
        log.error(String.format("No resource found exception occurred: %s ", exception.getMessage()));

        ErrorResponse response = new ErrorResponse();
        response.getErrors().add(
            new Error(
                ErrorCodes.ERR_RESOURCE_NOT_FOUND,
                "Resource not found",
                exception.getMessage()));

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        ErrorResponse error = new ErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getErrors().add(
                new Error(
                    ErrorCodes.ERR_REQUEST_PARAMS_BODY_VALIDATION_FAILED,
                    fieldError.getField(),
                    fieldError.getDefaultMessage()));
        }
        log.error(String.format("Validation exception occurred: %s", CommonUtils.convertObjectToJsonString(error)));
        return error;
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onInvalidRequest(HttpMessageConversionException e) {
        log.error("Invalid request received", e);

        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
            new Error(
                ErrorCodes.ERR_REQUEST_PARAMS_BODY_VALIDATION_FAILED,
                "Invalid Request",
                "Invalid request body. Please verify the request and try again !!"
            )
        );

        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        log.error("Constraint validation exception occurred", e);

        ErrorResponse error = new ErrorResponse();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getErrors().add(
                new Error(
                    ErrorCodes.ERR_CONSTRAINT_CHECK_FAILED,
                    violation.getPropertyPath().toString(),
                    violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse onFileStorageException(FileStorageException e) {
        log.error("Error occurred while uploading file", e);

        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
                new Error(
                    ErrorCodes.ERR_RUNTIME,
                        "Error occurred while uploading the file",
                        e.getMessage()
                )
        );
        return error;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse onRuntimeException(RuntimeException e) {

        log.error("Error occurred while handling request", e);

        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
            new Error(
                ErrorCodes.ERR_RUNTIME,
                "Internal Server Error",
                "Error occurred while processing your request. Please try again !!"
            )
        );
        return error;
    }
}
