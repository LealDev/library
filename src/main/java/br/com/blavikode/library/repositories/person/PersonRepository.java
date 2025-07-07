package br.com.blavikode.library.repositories.person;

import br.com.blavikode.library.model.Person;
import br.com.blavikode.library.repositories.LibraryRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository extends LibraryRepository<Person, Long>, JpaSpecificationExecutor<Person> {


}
