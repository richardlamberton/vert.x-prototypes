package com.nbrown.vertxMongodb;

import com.nbrown.vertxMongodb.service.ProductService;
import com.nbrown.vertxMongodb.verticle.ProductVerticle;

import io.vertx.core.Vertx;

public class VertxMongoDBApp {
    public static void main( String[] args ) {
    	Vertx vertx = Vertx.vertx();
    	ProductVerticle productVerticle = new ProductVerticle();
        vertx.deployVerticle(productVerticle);
        
		ProductService productService = new ProductService(vertx);
		productVerticle.setProductService(productService);
    }
}
