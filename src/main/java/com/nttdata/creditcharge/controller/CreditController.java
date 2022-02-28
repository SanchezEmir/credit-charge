package com.nttdata.creditcharge.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.creditcharge.entity.Credit;
import com.nttdata.creditcharge.service.ICreditService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/creditCharge")
public class CreditController {

  @Autowired
  ICreditService service;

  @GetMapping("list")
  public Flux<Credit> findAll() {
    return service.findAll();
  }

  @GetMapping("/find/{id}")
  public Mono<Credit> findById(@PathVariable String id) {
    return service.findById(id);
  }

  @PostMapping("/create")
  public Mono<ResponseEntity<Credit>> create(@RequestBody Credit credit) {
    return service.findCreditCard(credit.getCreditCard().getId()).flatMap(
        cc -> service.findCountCreditCardId(cc.getId()).filter(count -> {
          // VERIFICAR CANTIDAD DE CREDITOS PERMITIDOS
          switch (cc.getCustomer().getTypeCustomer().getValue()) {
            case PERSONAL :
              return count < 1;
            case EMPRESARIAL :
              return true;
            default :
              return false;
          }
        }) // VERIFICAR SI LA TARJETA DE CREDITO TIENE SALDO
            .flatMap(count -> service
                .findTotalConsumptionCreditCardId(cc.getId())
                .filter(totalConsumption -> cc
                    .getLimitCredit() >= totalConsumption + credit.getAmount())
                .flatMap(totalConsumption -> {
                  credit.setCreditCard(cc);
                  credit.setCreatedAt(LocalDateTime.now());
                  return service.create(credit);
                })))
        .map(c -> new ResponseEntity<>(c, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PutMapping("/update")
  public Mono<ResponseEntity<Credit>> update(@RequestBody Credit credit) {
    return service.findById(credit.getId()) // VERIFICO SI EL CREDITO EXISTE
        .flatMap(ccDB -> service.findCreditCard(credit.getCreditCard().getId())
            .flatMap(cc -> service.findTotalConsumptionCreditCardId(cc.getId())
                .filter(
                    totalConsumption -> cc.getLimitCredit() >= totalConsumption
                        - ccDB.getAmount() + credit.getAmount())
                .flatMap(totalConsumption -> {
                  credit.setCreditCard(cc);
                  credit.setCreatedAt(LocalDateTime.now());
                  return service.create(credit);
                })))
        .map(c -> new ResponseEntity<>(c, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @DeleteMapping("/delete/{id}")
  public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
    return service.delete(id).filter(deleteCustomer -> deleteCustomer)
        .map(deleteCustomer -> new ResponseEntity<>("Credit Deleted",
            HttpStatus.ACCEPTED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
