package com.tubebreakup.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
    logger.error("\n\nGeneric Exception: " + request + "\n", ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(), CommonErrors.UNKNOWN.value(),
        new Date(), ex.getMessage(), request.getDescription(false)));
    return new ResponseEntity<ExceptionResponse>(response, status);
  }
  
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    logger.error("\n\nHttpMessageNotReadableException: " + request + "\n", ex);
    status = HttpStatus.BAD_REQUEST;
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(), CommonErrors.INVALID_PAYLOAD.value(),
        new Date(), ex.getMessage(), request.getDescription(false)));
    return new ResponseEntity<Object>(response, status);
  }

  @ExceptionHandler(HttpServerErrorException.class)
  @ResponseBody
  public final ResponseEntity<ExceptionResponse> handleHttpServerErrorExceptions(HttpServerErrorException ex,
      WebRequest request) {
    logger.error("\n\nHttpServerErrorException: " + request + "\n", ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(), CommonErrors.UNKNOWN.value(),
        new Date(), ex.getMessage(), request.getDescription(false)));
    return new ResponseEntity<ExceptionResponse>(response, status);
  }

  @ExceptionHandler(Throwable.class)
  @ResponseBody
  public final ResponseEntity<ExceptionResponse> handleAllThrowables(Throwable ex, WebRequest request) {
    logger.error("\n\nGeneric Throwable: " + request + "\n", ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(), CommonErrors.UNKNOWN.value(),
        new Date(), ex.getMessage(), request.getDescription(false)));
    return new ResponseEntity<ExceptionResponse>(response, status);
  }

  @ExceptionHandler(ErrorCodedHttpException.class)
  @ResponseBody
  public final ResponseEntity<ExceptionResponse> handleErrorCodedException(ErrorCodedHttpException ex,
      WebRequest request) {
    if (ex.getIsLogged()) {
      logger.error("\n\nErrorCodedHttpException: " + request + "\n", ex);
    }
    String message = ex.getMessage() != null ? ex.getMessage() : ex.getErrorCode().message();
    HttpStatus status = ex.getHttpStatus();
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(), ex.getErrorCode().value(),
        new Date(), message, request.getDescription(false)));
    return new ResponseEntity<ExceptionResponse>(response, status);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseBody
  public final ResponseEntity<ExceptionResponse> handleUserNotFoundExceptions(ResourceNotFoundException ex,
      WebRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(),
        CommonErrors.RESOURCE_NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false)));
    return new ResponseEntity<ExceptionResponse>(response, status);
  }

  @Override
  @ResponseBody
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    logger.error("\n\nMethodArgumentNotValidException: " + request + "\n", ex);
    status = HttpStatus.BAD_REQUEST;
    ExceptionResponse response = new ExceptionResponse(new ExceptionError(status.value(), CommonErrors.UNKNOWN.value(),
        new Date(), "Validation Failed", ex.getBindingResult().toString()));
    return new ResponseEntity<Object>(response.getResponseBody(), status);
  }
}
