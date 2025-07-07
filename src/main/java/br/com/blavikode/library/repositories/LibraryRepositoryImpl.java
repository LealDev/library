package br.com.blavikode.library.repositories;

import br.com.blavikode.library.type.AbstractType;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

public class LibraryRepositoryImpl<T extends AbstractType, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements LibraryRepository<T, ID> {

    private final EntityManager entityManager;

    public LibraryRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                 EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public T refresh(T type) {
        type = (T) entityManager.find(type.getClass(), type.getId());
        entityManager.refresh(type);
        return type;
    }
}