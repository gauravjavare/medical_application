package com.medical_application.exception;

import com.medical_application.payload.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandlerClass extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(EntityNotFoundException ex, WebRequest webRequest) {
      ErrorDetails errorDetails = new ErrorDetails(new Date(),ex.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BlogApiException.class)
    public ResponseEntity<ErrorDetails>handleBlogApiException(BlogApiException blogApiException,WebRequest webRequest){
        ErrorDetails  errorDetails= new ErrorDetails(new Date(), blogApiException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorDetails>handleBlogApiException(UnauthorizedAccessException ex,WebRequest webRequest){
        ErrorDetails  errorDetails= new ErrorDetails(new Date(), ex.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
    }
}
