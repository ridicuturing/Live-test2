package com.github.ridicuturing.guard.common.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

//@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 异常处理方法
    /*@ExceptionHandler(Exception.class)
    public Mono<ServerResponse> handleException(Exception e) {
        return ResponseEntity.
    }*/
    // 异常处理方法
    /*@ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity> handleAccessDeniedException(AccessDeniedException e) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(MapBuilder.create().put("message", HttpStatus.FORBIDDEN.getReasonPhrase()).build()));
        // 具体的异常处理逻辑
    }*/
    /*@ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity> a(ServerWebInputException e) {
        return Mono.just(ResponseEntity.ok().build());
        // 具体的异常处理逻辑
    }*/

}
