package com.mhealth.admin.exception;

public class PatientUserExceptionHandler extends RuntimeException{
    public PatientUserExceptionHandler() {
        super("Not found"); // Provide a default error message
    }

    public PatientUserExceptionHandler(String message) {
        super(message);
    }

}
