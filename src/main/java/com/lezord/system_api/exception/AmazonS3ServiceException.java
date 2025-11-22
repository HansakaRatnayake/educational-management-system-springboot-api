package com.lezord.system_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AmazonS3ServiceException extends RuntimeException {
    public AmazonS3ServiceException(String message) {
        super(message);
    }
}
