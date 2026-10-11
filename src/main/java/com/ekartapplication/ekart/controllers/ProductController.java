package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.model.SuccessResponse;
import com.ekartapplication.ekart.utils.enums.ProductFilterMode;
import com.ekartapplication.ekart.model.Product;
import com.ekartapplication.ekart.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/product/v1")
public class ProductController {

    @Autowired
    ProductService service;

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> createProduct(@RequestBody Product product) throws IOException {
        String productId = service.createProduct(product);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("productId", productId)), HttpStatus.OK);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getProduct(@RequestParam String productId) {
        Product product = service.getProduct(productId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(product)), HttpStatus.OK);
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateProduct(@RequestBody Product product) {
        String productId = service.updateProduct(product);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("productId", productId)), HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getProducts(@RequestParam ProductFilterMode mode,
                                              @RequestParam(required = false) String categoryId) {
        List<Product> products = service.getProducts(mode, categoryId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singleton(products)), HttpStatus.OK);
    }

    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteProduct(@RequestParam String productId) {
        String isDeleted = service.deleteProduct(productId);
        return new ResponseEntity<>(new SuccessResponse(HttpStatus.OK.value(), Collections.singletonMap("message", isDeleted)), HttpStatus.OK);
    }
}
