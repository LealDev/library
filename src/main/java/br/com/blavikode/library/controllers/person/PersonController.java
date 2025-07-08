package br.com.blavikode.library.controllers.person;

import br.com.blavikode.library.exception.LibraryRuntimeException;
import br.com.blavikode.library.model.Person;
import br.com.blavikode.library.services.person.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static br.com.blavikode.library.ApplicationConstants.*;


@RestController
@RequestMapping(PATH_PERSON)
public class PersonController {

    @Autowired
    private IPersonService personService;

    @GetMapping(value = PATH_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person findById(@PathVariable(PROP_ID) long id) {
        return personService.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Person create(@RequestBody Person person) {
        return personService.create(person);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Person update(@RequestBody Person person) {
        return personService.update(person);
    }

    @DeleteMapping(value = PATH_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable(PROP_ID) Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = PATH_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> findAll() {
        return personService.findAll();
    }
}
