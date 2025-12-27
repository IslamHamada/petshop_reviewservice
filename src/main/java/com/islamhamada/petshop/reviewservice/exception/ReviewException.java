package com.islamhamada.petshop.reviewservice.exception;

import com.islamhamada.petshop.contracts.exception.ServiceException;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ReviewException extends ServiceException {
    private HttpStatus httpStatus;

    public ReviewException(String message, String error_code, HttpStatus httpStatus){
        super(message, "REVIEW" + error_code);
        this.httpStatus = httpStatus;
    }
}
