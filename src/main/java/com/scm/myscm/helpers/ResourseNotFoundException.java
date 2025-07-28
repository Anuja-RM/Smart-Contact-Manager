package com.scm.myscm.helpers;

public class ResourseNotFoundException extends RuntimeException{

    public ResourseNotFoundException(String message) {
        super(message);
    }

    public ResourseNotFoundException(){
        super("Resource not found");
    }
}
