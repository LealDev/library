package br.com.blavikode.library.exception;


import static br.com.blavikode.library.ApplicationConstants.EXCEPTION_PERSON_NOT_FOUND;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Long id) {
        super(String.format(EXCEPTION_PERSON_NOT_FOUND, id));
    }
}
