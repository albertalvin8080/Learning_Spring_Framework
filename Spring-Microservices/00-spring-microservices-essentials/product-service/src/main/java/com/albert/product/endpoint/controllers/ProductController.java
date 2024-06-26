package com.albert.product.endpoint.controllers;

import com.albert.core.model.Product;
import com.albert.product.endpoint.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/product")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController
{
    private final ProductService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Product>> findAllPageable(Pageable pageable) {
        return ResponseEntity.ok(service.findAllPageable(pageable));
    }

}
