package com.hand.push.interceptor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.hand.push.util.ResponseHelper.failure;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/20/13
 * Time: 10:26 AM
 */
@ControllerAdvice
public class ErrorInterceptor {
    /**
     * 拦截异常请求，
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public Map<String, ?> handleRuntimeException(HttpServletRequest request, HttpServletResponse response, RuntimeException re) {

        re.printStackTrace();



//
        return failure(re.getMessage()).result();
    }

}
