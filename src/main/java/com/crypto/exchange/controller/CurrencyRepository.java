package com.crypto.exchange.controller;

import org.springframework.data.repository.CrudRepository;

import com.crypto.exchange.core.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {

}
