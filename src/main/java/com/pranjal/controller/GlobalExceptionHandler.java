package com.pranjal.controller;

import com.pranjal.dtos.ErrorDTO.ErrorResponse;
import com.pranjal.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "INTERNAL_SERVER_ERROR"
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> insufficientBalanceException(InsufficientBalanceException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "CONFLICT"
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientHoldingException.class)
    public ResponseEntity<ErrorResponse> insufficientHoldingException(InsufficientHoldingException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "CONFLICT"
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StockDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> stockDoesNotExistException(StockDoesNotExistException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "NOT_FOUND"
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StockSymbolNotFoundException.class)
    public ResponseEntity<ErrorResponse> stockSymbolNotFoundException(StockSymbolNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "NOT_FOUND"
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<ErrorResponse> transactionFailedException(TransactionFailedException exception) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        LocalDateTime.now(),
                        "INTERNAL_SERVER_ERROR"
                ), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExistsException(UserAlreadyExistException exception){
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "CONFLICT"
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exception){
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "NOT_FOUND"
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AuthenticationException.class, })
    public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException exception){
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "UNAUTHORIZED"
        ), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> usernameNotFoundException(UsernameNotFoundException exception){
        return new ResponseEntity<>(new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now(),
                "NOT_FOUND"
        ), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        return new ResponseEntity<>(new ErrorResponse(
                "An unexpected error occurred.",
                LocalDateTime.now(),
                "INTERNAL_SERVER_ERROR"
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}