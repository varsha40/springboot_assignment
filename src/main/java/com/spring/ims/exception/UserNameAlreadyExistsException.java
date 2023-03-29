package com.spring.ims.exception;

public class UserNameAlreadyExistsException  extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public UserNameAlreadyExistsException(String msg) {
      super(msg);
    }
}
