package com.nttdata.creditcharge.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.creditcharge.entity.Credit;
import com.nttdata.creditcharge.entity.dto.CreditCard;
import com.nttdata.creditcharge.repository.ICreditRepository;
import com.nttdata.creditcharge.service.ICreditService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements ICreditService {

  private final WebClient webClient;
  private final ReactiveCircuitBreaker reactiveCircuitBreaker;

  @Value("${config.base.apigateway}")
  String url;

  public CreditServiceImpl(
      ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
    this.webClient = WebClient.builder().baseUrl(this.url).build();
    this.reactiveCircuitBreaker = circuitBreakerFactory.create("creditcard");
  }

  @Autowired
  ICreditRepository repo;

  @Override
  public Mono<Credit> findById(String id) {
    return repo.findById(id);
  }

  @Override
  public Flux<Credit> findAll() {
    return repo.findAll();
  }

  @Override
  public Mono<Credit> create(Credit t) {
    return repo.save(t);
  }

  @Override
  public Mono<Credit> update(Credit t) {
    return repo.save(t);
  }

  @Override
  public Mono<Boolean> delete(String t) {
    return repo.findById(t)
        .flatMap(credit -> repo.delete(credit).then(Mono.just(Boolean.TRUE)))
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<CreditCard> findCreditCard(String id) {
    return reactiveCircuitBreaker
        .run(webClient.get().uri(this.url + "/creditcard/find/{id}", id)
            .accept(MediaType.APPLICATION_JSON).retrieve()
            .bodyToMono(CreditCard.class), throwable -> {
              return this.getDefaultCreditCard();
            });
  }

  @Override
  public Mono<Long> findCountCreditCardId(String t) {
    return repo.findByCreditCardId(t).count();
  }

  @Override
  public Mono<Double> findTotalConsumptionCreditCardId(String t) {
    return repo.findByCreditCardId(t).collectList().map(
        credit -> credit.stream().mapToDouble(cdt -> cdt.getAmount()).sum());
  }

  public Mono<CreditCard> getDefaultCreditCard() {
    Mono<CreditCard> creditCard = Mono
        .just(new CreditCard("0", null, null, null, null, null));
    return creditCard;
  }

}
