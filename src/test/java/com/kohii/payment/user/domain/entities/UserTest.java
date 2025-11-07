package com.kohii.payment.user.domain.entities;

import com.kohii.payment.user.application.exceptions.InvalidAttributeException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    public void givenNullId_whenCreatingUser_thenThrowInvalidAttributeException() {
        assertThrows(InvalidAttributeException.class, () ->
            new User(null, "Teste", "teste@teste.com", "12345678")
        );
    }

    @Test
    public void givenNullEmail_whenCreatingUser_thenThrowInvalidAttributeException() {
        assertThrows(InvalidAttributeException.class, () ->
            new User(UUID.randomUUID(), "Teste", null, "12345678")
        );
    }

    @Test
    public void givenBlankEmail_whenCreatingUser_thenThrowInvalidAttributeException() {
        assertThrows(InvalidAttributeException.class, () ->
            new User(UUID.randomUUID(), "Teste", "", "12345678")
        );
    }

    @Test
    public void givenInvalidEmail_whenCreatingUser_thenThrowInvalidAttributeException() {
        assertThrows(InvalidAttributeException.class, () ->
            new User(UUID.randomUUID(), "Teste", "testetestecom", "12345678")
        );
    }

    @Test
    public void givenWeakPassword_whenCreatingUser_thenThrowInvalidAttributeException() {
        assertThrows(InvalidAttributeException.class, () ->
            new User(UUID.randomUUID(), "Teste", "teste@teste.com", "12345678")
        );
    }

    @Test
    public void givenBlankPassword_whenCreatingUser_thenThrowInvalidAttributeException() {
        assertThrows(InvalidAttributeException.class, () ->
            new User(UUID.randomUUID(), "Teste", "teste@teste.com", "")
        );
    }

    @Test
    public void givenStrongPassword_whenCreatingUser_thenNotShouldThrowException() {
        new User(UUID.randomUUID(), "Teste", "teste@teste.com", "Abc@1234");
    }

}
