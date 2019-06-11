package com.nbrown.vertxMongodb.verticle;

import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbrown.vertxMongodb.model.Product;
import com.nbrown.vertxMongodb.service.IProductService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Example of using Vert.x to provide asynchronous access to a service/mongoDB layer
 */
public class ProductVerticle extends AbstractVerticle {

	private IProductService productService;

	@Override
	public void start(Future<Void> future) {
		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		router.get("/products").handler(this::getAllProducts);
		router.post("/products").handler(this::createProduct);
		router.get("/products/:productId").handler(this::getProduct);
		router.delete("/products/:productId").handler(this::deleteProduct);

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						future.complete();
					} else {
						future.fail(result.cause());
					}
				});
	}

	/**
	 * Create/replace product
	 */
	public void createProduct(RoutingContext routingContext) {
		JsonObject body = routingContext.getBodyAsJson();
		
		Product product = Json.decodeValue(body.encode(), Product.class);
		
		productService.create(product)
			.setHandler(resultHandler(routingContext, res -> {
				if (res) {
					routingContext.response()
					    .setStatusCode(201)
					    .putHeader("content-type", "text/plain")
					    .end("OK");
				} else {
					serviceUnavailable(routingContext);
				}
			}));
	}

	/**
	 * Get all products
	 */
	public void getAllProducts(RoutingContext routingContext) {
		
		productService.findAll()
			.setHandler(resultHandler(routingContext, res -> {
				if (res.size() >= 0) {
					try {
						routingContext.response()
							.putHeader("content-type", "application/json")
							.end(new ObjectMapper().writeValueAsString(res));
					} catch (JsonProcessingException e) {
						serviceUnavailable(routingContext);
					}
				} else {
					serviceUnavailable(routingContext);
				}
			}));
	}

	/**
	 * Get single product
	 */
	public void getProduct(RoutingContext routingContext) {
		String productId = routingContext.request().getParam("productId");
		if (productId == null) {
			badRequest(routingContext);
			
		} else {
			
			productService.findById(Integer.valueOf(productId))
				.setHandler(resultHandler(routingContext, res -> {
		
					if (res == null) {
						notFound(routingContext);
					} else {
						try {
							routingContext.response()
								.putHeader("content-type", "application/json")
								.end(new ObjectMapper().writeValueAsString(res));
						} catch (JsonProcessingException e) {
							serviceUnavailable(routingContext);
						}
					}
				}));
		}
	}

	/**
	 * Delete product
	 */
	public void deleteProduct(RoutingContext routingContext) {
		String productId = routingContext.request().getParam("productId");
		if (productId == null) {
			badRequest(routingContext);
			
		} else {
			
			productService.delete(Integer.parseInt(productId))
				.setHandler(resultHandler(routingContext, res -> {
		
					if (res == null || !res) {
						notFound(routingContext);
					} else {
						routingContext.response()
							.putHeader("content-type", "text/plain")
							.end("OK");
					}
				}));
		}
	}

	
	
	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void badRequest(RoutingContext context) {
		context.response().setStatusCode(400).end();
	}

	private void notFound(RoutingContext context) {
		context.response().setStatusCode(404).end();
	}

	private void serviceUnavailable(RoutingContext context) {
		context.response().setStatusCode(503).end();
	}

	/**
	 * Wrap the result handler with failure handler
	 */
	private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Consumer<T> consumer) {
	  return res -> {
	      consumer.accept(res.result());
	  };
	}
	
	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

}
