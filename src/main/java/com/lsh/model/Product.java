package com.lsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/indexing.html
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String sku;

    private String name;

    private double price;
}
