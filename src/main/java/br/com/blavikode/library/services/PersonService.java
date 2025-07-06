package br.com.blavikode.library.services;

import br.com.blavikode.library.model.Person;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private static final AtomicLong counter = new AtomicLong();

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public Person findById(String id){
        logger.info("Finding one Person!");
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Emanuel");
        person.setLastName("Leal");
        person.setAddress("Serra - ES");
        person.setGender("MALE");

        return person;
    }
}
