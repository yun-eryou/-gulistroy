package com.atguigu.gulimall.product.exception;


import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import jdk.nashorn.internal.ir.ReturnNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;


/**
 * 集中处理所有异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {



    @ResponseBody
    @ExceptionHandler(value = org.springframework.web.bind.MethodArgumentNotValidException.class)
    public R handleValidException(org.springframework.web.bind.MethodArgumentNotValidException e){
        log.error("数据校验出现问题{},异常类型{}",e.getMessage(),e.getClass());
        HashMap<String, String> errorMap = new HashMap<>();
        BindingResult result = e.getBindingResult();

        result.getFieldErrors().forEach((fieldError )->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        } );
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data",errorMap);

    }


    //@ExceptionHandler(value = Throwable.class)
    public R handleException( Throwable throwable){

        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());

    }







}
