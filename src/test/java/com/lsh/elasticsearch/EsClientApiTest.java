package com.lsh.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.lsh.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/usage.html
 */
@Slf4j
@SpringBootTest
public class EsClientApiTest {


    @Autowired
    ElasticsearchClient client;

    @Autowired
    ElasticsearchAsyncClient esAsyncClient;


    /**
     * // Create the "products" index
     */
    @Test
    void createIndex() throws IOException {

        CreateIndexResponse createResponse1 = client.indices().create(
                new CreateIndexRequest.Builder()
                        .index("my-index")
                        .aliases("foo",
                                new Alias.Builder().isWriteIndex(true).build()
                        )
                        .build()
        );

        //lambda写法
//        client.indices().create(c -> c.index("products"));
        CreateIndexResponse createResponse2 = client.indices()
                .create(c -> c
                        .index("my-index")
                        .aliases("foo", a -> a
                                .isWriteIndex(true)
                        )
                );
    }

    @Test
    void indexingSingleDocument() throws IOException {
        Product product = new Product("bk-1", "City bike", 123.0);

        IndexResponse response = client.index(i -> i
                .index("products")
                .id(product.getSku())
                .document(product)
        );
        System.out.println("Indexed with version " + response.version());

        // Using the asynchronous client
        Product product2 = new Product("bk-2", "City bike", 124.0);
        esAsyncClient.index(i -> i
                .index("products")
                .id(product.getSku())
                .document(product2)
        ).whenComplete((asyncResponse, exception) -> {
            if (exception != null) {
                log.error("Failed to index", exception);
            } else {
                System.out.println("Indexed with version " + asyncResponse.version());
            }
        });

        System.in.read();
    }

    @Test
    void indexingMultipleDocument() throws IOException {

        List<Product> products = Arrays.asList(new Product("bk-3", "City bike", 1231.0),
                new Product("bk-4", "City bike", 1232.0),
                new Product("bk-5", "City bike", 1233.0));

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
        BulkResponse result = client.bulk(br.build());
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

    @Test
    void readingDocumentById() throws IOException {
        GetResponse<Product> response = client.get(g -> g
                        .index("products")
                        .id("bk-1"),
                Product.class
        );
        if (response.found()) {
            Product product = response.source();
            log.info("Product Info :" + product);
        } else {
            log.info ("Product not found");
        }
    }

    @Test
    void simpleSearchQuery() throws IOException {
        String searchText = "bike";

        SearchResponse<Product> response = client.search(s -> s
                        .index("products")
                        .query(q -> q
                                .match(t -> t
                                        .field("name")
                                        .query(searchText)
                                )
                        ),
                Product.class
        );

        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            log.info("There are " + total.value() + " results");
        } else {
            log.info("There are more than " + total.value() + " results");
        }

        List<Hit<Product>> hits = response.hits().hits();
        for (Hit<Product> hit: hits) {
            Product product = hit.source();
            log.info("Found product " + product.getSku() + ", score " + hit.score());
        }
    }

    @Test
    void simpleAggregationQuery() throws IOException {
        String searchText = "bike";

        Query query = MatchQuery.of(m -> m
                .field("name")
                .query(searchText)
        )._toQuery();

        SearchResponse<Void> response = client.search(b -> b
                        .index("products")
                        .size(0)
                        .query(query)
                        .aggregations("price-histogram", a -> a
                                .histogram(h -> h
                                        .field("price")
                                        .interval(50.0)
                                )
                        ),
                Void.class
        );
        List<HistogramBucket> buckets = response.aggregations()
                .get("price-histogram")
                .histogram()
                .buckets().array();

        for (HistogramBucket bucket: buckets) {
            log.info("There are " + bucket.docCount() +
                    " bikes under " + bucket.key());
        }
    }

    @Test
    void deleteIndex() throws IOException {

    }



}
