package br.com.blavikode.library.exception;

public class InvalidPersonDataException extends RuntimeException {
    public InvalidPersonDataException(String message) {
        super(message);
    }
}
