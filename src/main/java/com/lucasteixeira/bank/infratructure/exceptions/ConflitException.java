package com.lucasteixeira.bank.infratructure.exceptions;

public class ConflitException extends RuntimeException {
    public ConflitException(String message) {
        super(message);
    }
}
