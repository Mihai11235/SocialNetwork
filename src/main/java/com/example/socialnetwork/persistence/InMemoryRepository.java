package com.example.socialnetwork.persistence;

import com.example.socialnetwork.domain.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    Map<ID,E> entities;

    public InMemoryRepository() {
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> getAll() {
        return entities.values();
    }

    @Override
    public Optional<E> add(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");


        if(entities.containsKey(entity.getId())) {
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
