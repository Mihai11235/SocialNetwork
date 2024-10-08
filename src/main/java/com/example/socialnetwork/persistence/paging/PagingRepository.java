package com.example.socialnetwork.persistence.paging;


import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.persistence.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
    Page<E> findAllOnPage(Pageable pageable, Long user);
}
