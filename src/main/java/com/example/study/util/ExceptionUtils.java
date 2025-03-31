package com.example.study.util;

import com.example.study.exception.CommonExceptionHandler;
import com.example.study.exception.ErrorCode;

public class ExceptionUtils {

    public static void throwIfExists(int count, ErrorCode errorCode){
        if (count > 0){
            throw new CommonExceptionHandler(errorCode);
        }
    }
    public static <T> T throwIfNotFound(T object, ErrorCode errorCode){
        if (object == null){
            throw new CommonExceptionHandler(errorCode);
        }
        return object;
    }

}
