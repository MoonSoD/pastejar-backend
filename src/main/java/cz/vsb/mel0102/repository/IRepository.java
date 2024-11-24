package cz.vsb.mel0102.repository;

import cz.vsb.mel0102.entities.IEntity;

import java.util.List;
import java.util.Optional;

public interface IRepository<E extends IEntity> {
    void insert(E entity);
    void update(long id, E entity);
    void delete(long id);
    void deleteAll();
    Optional<E> findById(long id);
    List<E> findAll();
}

