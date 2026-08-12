package com.kohii.payment.modules.user.domain.entities;

import com.kohii.payment.modules.user.application.exceptions.InvalidAttributeException;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record User(
        UUID id,
        String name,
        String email,
        String password,
        Boolean enabled
) {

   public User {
       validateId(id);
       validateName(name);
       validateEmail(email);
       validatePassword(password);
       enabled = enabled == null ? true : enabled;
   }

   public User(UUID id, String name, String email, String password) {
       this(id, name, email, password, true);
    }

   private void validateId(UUID id) {
        if (id == null) {
            throw new InvalidAttributeException("user in application: id is invalid");
        }
    }

   private void validateName(String name) {
       if (name == null || name.isBlank()) {
           throw new InvalidAttributeException("user in application: name is invalid");
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

   private void validatePassword(String password) {
       if (password == null || password.isBlank()) {
           throw new InvalidAttributeException("user in application: password id blank");
        }

       if (password.startsWith("$2")) {
           return;
       }

       boolean isStrong = isStrongPassword(password);
       if (!isStrong) {
           throw new InvalidAttributeException("user in application: password is weak");
       }
   }

   private boolean isStrongPassword(String password) {
       String STRONG_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
       Pattern pattern = Pattern.compile(STRONG_PASSWORD_REGEX);
       Matcher matcher = pattern.matcher(password);
       return matcher.matches();
   }
}
