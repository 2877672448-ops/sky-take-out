package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex 业务异常
     * @return 统一返回结果
     */
    @ExceptionHandler
    public Result<?> exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    /**
     * 处理SQL约束冲突异常
     * @param ex SQL约束异常
     * @return 统一返回结果
     */
    @ExceptionHandler
    public Result<?> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if(ex.getMessage().contains("Duplicate entry")){

           String[] split = ex.getMessage().split(" ");
           String username = split[2];
           String msg = username + MessageConstant.ALREADY_EXISTS;
           return Result.error(msg);

        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }

}
