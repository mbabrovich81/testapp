package com.itrexgroup.turvo.testapp.controller;

import com.itrexgroup.turvo.testapp.exception.EmptyArgumentsException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by maxim.babrovich on 10.04.2019.
 */

@ControllerAdvice(basePackages = "com.itrexgroup.turvo.testapp.controller")
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ResponseEntity<ResponseError> handleBadRequest(MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(buildResponseError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ EmptyArgumentsException.class })
    public ResponseEntity<ResponseError> handleEmptyArgumentException(EmptyArgumentsException ex) {
        log.error("Empty argument error", ex);
        return new ResponseEntity<>(buildResponseError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ SQLException.class, DataAccessException.class })
    public ResponseEntity<ResponseError> handleDatabaseException(Exception ex) {
        log.error("Database error", ex);
        return new ResponseEntity<>(buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ SchedulerException.class })
    public ResponseEntity<ResponseError> handleSchedulerException(SchedulerException ex) {
        log.error("Scheduler error", ex);
        return new ResponseEntity<>(buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ResponseError> handleServerError(Exception ex) {
        log.error("Server error", ex);
        return new ResponseEntity<>(buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseError buildResponseError(HttpStatus httpStatus, String message) {
        ResponseError responseError = ResponseError.builder()
                .errorCode(httpStatus.value())
                .errorStatus(httpStatus.getReasonPhrase())
                .errorMsg(message)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        log.error(responseError.toString());

        return responseError;
    }
}
