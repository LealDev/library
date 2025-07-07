package br.com.blavikode.library.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;

public class LibraryRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public LibraryRuntimeException(final String message) {
        super(message);
    }

    public LibraryRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    @JsonIgnore
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @Override
    @JsonIgnore
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
