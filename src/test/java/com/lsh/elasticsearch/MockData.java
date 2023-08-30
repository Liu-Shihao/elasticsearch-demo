package com.lsh.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsh.model.FAQ;
import com.lsh.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class MockData {
    @Autowired
    ElasticsearchClient esClient;

    @Test
    void  mockFaqData() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("/Users/liushihao/Data/IdeaProjects/elasticsearch-demo/data/faq.json"));
        String line;
        while ((line = br.readLine()) != null){
            sb.append(line);
        }
        System.out.println(sb);

        ObjectMapper objectMapper = new ObjectMapper();
        FAQ[] faqs = objectMapper.readValue(sb.toString(), FAQ[].class);

        BulkRequest.Builder builder = new BulkRequest.Builder();
        for (FAQ faq : faqs) {
            builder.operations(op -> op
                    .index(idx -> idx
                            .index("faq")
                            .id(faq.getId())
                            .document(faq)
                    )
            );
        }
        BulkResponse result = esClient.bulk(builder.build());
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }
    }

    @Test
    void deleteIndex() throws IOException {
        //delete index
//        DeleteIndexResponse products = esClient.indices().delete(builder -> builder.index("products"));
//        System.out.println(products);

        BooleanResponse products1 = esClient.indices().exists(builder -> builder.index("products"));
        System.out.println(products1.value());

    }


    @Test
    void createIndex() throws IOException {
        BooleanResponse products1 = esClient.indices().exists(builder -> builder.index("products"));
        System.out.println(products1.value());

        CreateIndexResponse products = esClient.indices().create(builder -> builder.index("products"));
        System.out.println(products);

        BooleanResponse products2 = esClient.indices().exists(builder -> builder.index("products"));
        System.out.println(products2.value());
        /**
         * false
         * CreateIndexResponse: {"index":"products","shards_acknowledged":true,"acknowledged":true}
         * true
         */
    }

    @Test
    void insertDoc() throws IOException {
        List<Product> products = mockProducts();
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Product product : products) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("products")
                            .id(product.getSku())
                            .document(product)
                    )
            );
        }

        BulkResponse result = esClient.bulk(br.build());

        // Log errors, if any
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }


    }


    List<Product> mockProducts(){
        return Arrays.asList(
                new Product("bk-1","City Bike",1599.00),
                new Product("yw-u8","仰望U8 豪华版",1098000.00),
                new Product("audi-e-tron-gt","奥迪 e-tron GT 23",999800.00),
                new Product("bmw-i7","BMW i7 xDrive60L 豪华套装",1459000.00),
                new Product("mb-maybach-s580","梅赛德斯-迈巴赫 S 580 e 4MATIC 插电式混合动力轿车",2016000.00),
                new Product("audi-rs6-avant","奥迪 RS6 Avant",1453800.00),
                new Product("audi-r8","奥迪 R8 V10 Coupe Performance",2323800.00),
                new Product("bmw-z4","新BMW Z4敞篷跑车",493900.00),
                new Product("bmw-x6-m","新BMW X6 M 雷霆版",1468900.00),
                new Product("mb-amg-e53","梅赛德斯-AMG E 53 4MATIC +",961600.00),
                new Product("bmw-x4","BMW X4",466900.00)
        );

    }
}
