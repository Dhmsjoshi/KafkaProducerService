package dev.dharam.mykafka.services;

import dev.dharam.mykafka.models.CreateProductResModel;

public interface ProductService {

    String createProduct(CreateProductResModel product) throws Exception;
}
