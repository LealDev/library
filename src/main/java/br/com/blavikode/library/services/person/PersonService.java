package br.com.blavikode.library.services.person;

import br.com.blavikode.library.model.Person;
import br.com.blavikode.library.repositories.person.PersonRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PersonService implements IPersonService{

    private static final AtomicLong counter = new AtomicLong();

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> findAll(){
        log.debug("findAll");
        return personRepository.findAll();
    }

    @Override
    public Person findById(long id){
        log.debug("findById: {}", id);
        Optional<Person> personOptional = personRepository.findById(id);
        return personOptional.orElseThrow(() -> new RuntimeException("Person not found with id: "+ id));
    }

    @Override
    public Person create(Person person){
        log.debug(person.toString());
        return personRepository.save(person);
    }
}
