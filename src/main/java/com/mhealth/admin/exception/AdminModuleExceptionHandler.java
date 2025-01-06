package com.mhealth.admin.exception;

public class AdminModuleExceptionHandler extends RuntimeException{

    public AdminModuleExceptionHandler() {
        super("Not found"); // Provide a default error message
    }

    public AdminModuleExceptionHandler(String message) {
        super(message);
    }

}
