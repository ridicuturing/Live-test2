package com.github.ridicuturing.guard.mapper;

import com.github.ridicuturing.guard.model.entity.Action;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionMapper extends R2dbcRepository<Action, Long> {

}