//package com.cky.exceptions;
//
//import com.cky.common.R;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.sql.SQLIntegrityConstraintViolationException;
//
///**
// * @ClassName MyExceptions
// * @Description TODO
// * @Author lukcy
// * @Date 2024/6/21 15:51
// * @Version 1.0
// */
//@Slf4j
//@ResponseBody
//@ControllerAdvice(annotations = {RestController.class, Controller.class})
//public class MyExceptions {
//    /**
//     * 异常处理方法
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public R<String> EmpExceptions(SQLIntegrityConstraintViolationException ex) {
//        if (ex.getMessage().contains("")) {
//            String[] split = ex.getMessage().split("");
//            String msg = split[2] + "已存在";
//            return R.error(msg);
//        }return R.error("未知错误");
//        }}
