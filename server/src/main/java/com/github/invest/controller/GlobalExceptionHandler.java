package com.github.invest.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public HttpEntity<String> exceptionHandler(IOException e, HttpServletRequest request) {
        if (!ExceptionUtils.getRootCauseMessage(e).toLowerCase().contains("broken pipe"))
            return new HttpEntity<>(e.getMessage());
        else return null;
    }

}
