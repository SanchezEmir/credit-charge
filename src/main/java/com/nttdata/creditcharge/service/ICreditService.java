package com.nttdata.creditcharge.service;

import com.nttdata.creditcharge.entity.Credit;
import com.nttdata.creditcharge.entity.dto.CreditCard;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICreditService {
  
  Mono<Credit> findById(String id);
  
  Flux<Credit> findAll();
  
  Mono<Credit> create(Credit t);
  
  Mono<Credit> update(Credit t);
  
  Mono<Boolean> delete(String t);
  
  Mono<Long> findCountCreditCardId(String t);
  
  Mono<Double> findTotalConsumptionCreditCardId(String t);
  
  Mono<CreditCard> findCreditCard(String id);

}
