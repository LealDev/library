package br.com.blavikode.library.services;

import br.com.blavikode.library.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private static final AtomicLong counter = new AtomicLong();

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public List<Person> findAll(){
        var person = new ArrayList<Person>();
        for (int i = 0; i < 8; i++) {
            person.add(mockPerson(i));
        }
        return person;
    }

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

//    public Person create(Person person){
//
//    }

    private Person mockPerson(int index){
        Person person = new Person();
        person.setId((long) index);
        person.setFirstName("Frist Name" + index);
        person.setLastName("Last Name" + index);
        person.setAddress("Some address - ES" + index);
        person.setGender("Some Gender");

        return person;
    }
}
