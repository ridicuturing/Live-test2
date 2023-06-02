package com.github.ridicuturing.guard.common.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BussinessException extends RuntimeException {

    private HttpStatus httpStatus;

    private String sourceId;

    public BussinessException(Throwable cause) {
        super(cause);
    }

    public BussinessException(HttpStatus httpStatus, String sourceId) {
        super();
        this.httpStatus = httpStatus;
        this.sourceId = sourceId;
    }
}
