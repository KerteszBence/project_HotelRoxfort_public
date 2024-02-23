package org.hotel.backend.service;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[!@#$%^&*()-_+=]).{8,}$";

    public static void validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            throw new PasswordNotValidException();
        }
    }
}