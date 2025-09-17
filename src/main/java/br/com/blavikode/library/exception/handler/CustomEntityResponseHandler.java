package br.com.blavikode.library.exception.handler;

import br.com.blavikode.library.exception.InvalidPersonDataException;
import br.com.blavikode.library.exception.LibraryRuntimeException;
import br.com.blavikode.library.exception.PersonNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static br.com.blavikode.library.ApplicationConstants.ERRO_DE_COMUNICACAO_POR_FAVOR_ATUALIZE_SUA_TELA;

@ControllerAdvice
@RestController
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<String> handleNotFound(PersonNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPersonDataException.class)
    public ResponseEntity<String> handleInvalidData(InvalidPersonDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(500).body(ERRO_DE_COMUNICACAO_POR_FAVOR_ATUALIZE_SUA_TELA);
    }

    @ExceptionHandler(LibraryRuntimeException.class)
    public ResponseEntity<String> handleCustom(LibraryRuntimeException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

}
