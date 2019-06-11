package com.nbrown.springwebflux.controller;

import com.nbrown.springwebflux.model.Product;
import com.nbrown.springwebflux.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
 
    @RequestMapping(value = { "/products"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void create(@RequestBody Product product) {
        productService.create(product);
    }

    @RequestMapping(value="/products", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Product> findAll() {
        return productService.findAll();
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Mono<Product>> findById(@PathVariable("id") Integer id) {
        Mono<Product> product = productService.findById(id);
        HttpStatus status = product != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(product, status);
    }
    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") Integer id) {
        productService.delete(id).subscribe();
    }

}
