package com.nbrown.springwebflux.service;

import com.nbrown.springwebflux.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService
{
    void create(Product e);
     
    Mono<Product> findById(Integer id);

    Flux<Product> findAll();

    Mono<Void> delete(Integer id);
}