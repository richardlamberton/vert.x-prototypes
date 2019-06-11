package com.nbrown.springwebflux.dao;

import com.nbrown.springwebflux.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, Integer> {
}