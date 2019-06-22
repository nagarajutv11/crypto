package com.crypto.exchange.controller;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crypto.exchange.core.PathData;

@Repository
public interface PathDataRepository extends CrudRepository<PathData, Long> {

}
