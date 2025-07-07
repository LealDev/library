package br.com.blavikode.library.services.person;

import br.com.blavikode.library.model.Person;

import java.util.List;

public interface IPersonService {
    List<Person> findAll();

    Person findById(long id);

    Person create(Person person);
}
