package br.com.blavikode.library.controllers.person;

import br.com.blavikode.library.exception.LibraryRuntimeException;
import br.com.blavikode.library.model.Person;
import br.com.blavikode.library.services.person.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static br.com.blavikode.library.ApplicationConstants.*;


@RestController
@RequestMapping(PATH_PERSON)
public class PersonController {

    @Autowired
    private IPersonService PersonService;

    @GetMapping(value = PATH_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person findById(@PathVariable(PROP_ID) long id){
        try {
            return PersonService.findById(id);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Person create(@RequestBody Person person){
        try {
            return PersonService.create(person);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = PATH_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> findAll(){
       try {
           return PersonService.findAll();
       } catch (RuntimeException e) {
           throw new LibraryRuntimeException(ERRO_DE_COMUNICACAO_POR_FAVOR_ATUALIZE_SUA_TELA, e);
       }
    }
}
