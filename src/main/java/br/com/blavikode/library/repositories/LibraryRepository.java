package br.com.blavikode.library.repositories;

import br.com.blavikode.library.type.AbstractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface LibraryRepository<T extends AbstractType, ID extends Serializable> extends JpaRepository<T, ID> {

    T refresh(T entity);
}