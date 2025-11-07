package com.kohii.payment.user.domain.entities;

import com.kohii.payment.user.application.exceptions.InvalidAttributeException;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record User(
        UUID id,
        String name,
        String email,
        String password
) {

    public User(UUID id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;

        validateId(id);
        validateEmail(email);
        validatePassword(password);
    }

    private void validateId(UUID id){
        if (id == null) {
            throw new InvalidAttributeException("user in application: id is invalid");
        }
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new InvalidAttributeException("user in application: email is null");

        } else if (email.isBlank()) {
            throw new InvalidAttributeException("user in application: email is blank");

        } else if (!(email.contains("@") && email.contains("."))) {
            throw new InvalidAttributeException("user in application: email is invalid");
        }
    }

    private void validatePassword(String password){
        if (password.isBlank()) {
            throw new InvalidAttributeException("user in application: password id blank");
        } else {
            boolean isStrong = isStrongPassword(password);

            if (!isStrong) {
                throw new InvalidAttributeException("user in application: password is weak");
            }
        }
    }

    private boolean isStrongPassword(String password) {
        String STRONG_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(STRONG_PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
