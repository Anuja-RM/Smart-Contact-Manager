package com.scm.myscm.helpers;

public class EmailVerificationLink {

    public static String getLinkForEmailVerification(String emailToken){

        return "http://localhost:8080/auth/verify-email?token="+emailToken;
    }
}
