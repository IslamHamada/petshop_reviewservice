package com.islamhamada.petshop.reviewservice.exception;

import com.islamhamada.petshop.contracts.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewException extends ServiceException {
    private HttpStatus httpStatus;

    public ReviewException(String message, String error_code, HttpStatus httpStatus){
        super(message, "REVIEW" + error_code);
        this.httpStatus = httpStatus;
    }
}
