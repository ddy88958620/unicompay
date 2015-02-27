package com.unicom.sms.exception;

public class ServiceException extends Exception {
    private String errCode;
    
    private Object object;
    
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public String getErrCode() {
        return errCode;
    }

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message,String errorCode) {
        super(message);
        this.errCode = errorCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String message, Throwable cause,String errorCode) {
        super(message, cause);
        this.errCode = errorCode;
    }
    
    public ServiceException(String message, String errorCode,Object object) {
        super(message);
        this.errCode = errorCode;
        this.object = object;
    }
    
    public ServiceException(String message, Throwable cause,String errorCode,Object object) {
        super(message, cause);
        this.errCode = errorCode;
        this.object = object;
    }

    public ServiceException(Throwable cause,String errorCode) {
        super(cause);
        this.errCode = errorCode;
    }
}
