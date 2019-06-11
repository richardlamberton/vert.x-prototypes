package com.nbrown.vertxMongodb.service;

import java.util.Collection;

import com.nbrown.vertxMongodb.model.Product;

import io.vertx.core.Future;

public interface IProductService {
    Future<Boolean> create(Product e);
     
    Future<Product> findById(Integer id);

    Future<Collection<Product>> findAll();

    Future<Boolean> delete(Integer id);
}