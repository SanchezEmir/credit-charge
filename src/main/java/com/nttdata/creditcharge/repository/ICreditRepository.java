package com.nttdata.creditcharge.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.nttdata.creditcharge.entity.Credit;

import reactor.core.publisher.Flux;

public interface ICreditRepository extends ReactiveMongoRepository<Credit, String> {
  
  Flux<Credit> findByCreditCardId(String id);

}
