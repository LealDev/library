package br.com.blavikode.library.services.person;

import br.com.blavikode.library.exception.InvalidPersonDataException;
import br.com.blavikode.library.exception.LibraryRuntimeException;
import br.com.blavikode.library.exception.PersonNotFoundException;
import br.com.blavikode.library.model.Person;
import br.com.blavikode.library.repositories.person.PersonRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static br.com.blavikode.library.ApplicationConstants.EXCEPTION_NULL_ID;

@Service
public class PersonService implements IPersonService{

    private static final AtomicLong counter = new AtomicLong();

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> findAll(){
        return personRepository.findAll();
    }

    @Override
    public Person findById(long id){
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Override
    public Person create(Person person){
        return personRepository.save(person);
    }

    @Override
    public Person update(Person person){
        if (person.getId() == null){
            throw new InvalidPersonDataException(EXCEPTION_NULL_ID);
        }
        Person entity = personRepository.findById(person.getId()).orElseThrow(() -> new PersonNotFoundException(person.getId()));
        entity.setAddress(person.getAddress());
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());
        entity.setAtivo(person.getAtivo());
        return personRepository.save(entity);
    }

    @Override
    public void delete(Long id){
        Person entity = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        personRepository.delete(entity);
    }
}
