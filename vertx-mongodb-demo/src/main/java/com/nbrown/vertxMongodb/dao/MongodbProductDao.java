package com.nbrown.vertxMongodb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nbrown.vertxMongodb.model.Product;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * Example of asynchronous access to a mongo database, by using Future wrappers for the response
 */
public class MongodbProductDao {

	private MongoClient mongoClient;

	private static final String DB_NAME = "testdb";
	
	public MongodbProductDao(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	
	/**
	 * Insert a product
	 */
	public Future<Boolean> insert(Product product) {
		Future<Boolean> result = Future.future();
		
		String json = Json.encode(product);
		JsonObject jsonProduct = new JsonObject(json);
		jsonProduct.put("_id", product.getProductId());
		
		mongoClient.save(DB_NAME, jsonProduct, res -> {
		    if (res.succeeded()) {
		        System.out.println("Inserted product with id " + product.getProductId());
		        result.complete(true);
		    } else {
		        result.fail(res.cause());
		    }
		});
		
		return result;
	}

	/**
	 * Find a product by its productId
	 */
	public Future<Product> findById(Integer productId) {
		Future<Product> result = Future.future();

		JsonObject query = new JsonObject()
				  .put("productId", productId);
		
		mongoClient.find(DB_NAME, query, res -> {
			if (res.succeeded()) {
			    if (res.result().size() > 0) {
			    	Product product = Json.decodeValue(res.result().get(0).encode(), Product.class);
			        System.out.println("Found product with id " + productId);
                    result.complete(product);
				} else {
				    result.fail("Product not found");
				}
			} else {
			    result.fail(res.cause());
			}
		});
		

		return result;
	}

	/**
	 * Find all products
	 */
	public Future<Collection<Product>> findAll() {
		Future<Collection<Product>> result = Future.future();

		JsonObject query = new JsonObject();
		
		FindOptions options = new FindOptions().setSort(new JsonObject().put("productId", 1));

		mongoClient.findWithOptions(DB_NAME, query, options, res -> {
            if (res.succeeded()) {
            	List<Product> products = new ArrayList<>();
            	for (JsonObject json : res.result()) {
            		products.add(Json.decodeValue(json.encode(), Product.class));
            	}
		        System.out.println("Retrieved " + products.size() + " products");
                result.complete(products);
            } else {
            	result.fail(res.cause());
            }
        });
	
		return result;
	}

	/**
	 * Remove a product by its productId
	 */
	public Future<Boolean> deleteById(Integer productId) {
		Future<Boolean> result = Future.future();

		JsonObject query = new JsonObject()
				  .put("productId", productId);
		
		mongoClient.removeDocument(DB_NAME, query, res -> {
            if (res.succeeded() && res.result().getRemovedCount() > 0) {
		        System.out.println("Removed " + res.result().getRemovedCount() + " product(s)");
                result.complete(true);
            } else {
            	result.fail("failed to delete product");
            }
        });
		
		return result;
	}

}
