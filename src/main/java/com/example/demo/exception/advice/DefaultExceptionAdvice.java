package com.example.demo.exception.advice;



import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorObject;
import com.example.demo.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionAdvice {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionAdvice.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(BusinessException ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(ex.getHttpStatus().value());
        errorObject.setMessage(ex.getMessage());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(ex.getHttpStatus().value()));
    }

    /*
        Spring6 버전에 추가된 ProblemDetail 객체에 에러정보를 담아서 리턴하는 방법
     */
//    @ExceptionHandler(BusinessException.class)
//    protected ProblemDetail handleException(BusinessException e) {
//        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus());
//        problemDetail.setTitle("Not Found");
//        problemDetail.setDetail(e.getMessage());
//        problemDetail.setProperty("errorCategory", "Generic");
//        problemDetail.setProperty("timestamp", Instant.now());
//        return problemDetail;
//    }

    //숫자타입의 값에 문자열타입의 값을 입력으로 받았을때 발생하는 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    //403 예외 발생
    @ExceptionHandler(value = AccessDeniedException.class)
    public void accessDeniedExceptionHandler(Exception e) {
        throw new AccessDeniedException(e.getMessage());
    }

    //401 예외 발생
    @ExceptionHandler(value = BadCredentialsException.class)
    public void badCredentialExceptionHandler(BadCredentialsException e){
        throw new BadCredentialsException(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorObject> handleException(Exception e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage(e.getMessage());

        log.error(e.getMessage(), e);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(500));
    }
}