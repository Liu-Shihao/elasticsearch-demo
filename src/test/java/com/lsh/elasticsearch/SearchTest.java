package com.lsh.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.lsh.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootTest
public class SearchTest {

    @Autowired
    ElasticsearchClient esClient;

    @Test
    void getDocById() throws IOException {
        GetResponse<Product> products1 =esClient.get(
                new GetRequest.Builder()
                        .index("products")
                        .id("audi-e-tron-gt")
                        .build(),
                Product.class
        );
        System.out.println(products1);
        //GetResponse: {"_index":"products","found":true,"_id":"audi-e-tron-gt","_primary_term":1,"_seq_no":2,"_source":"Product(sku=audi-e-tron-gt, name=奥迪 e-tron GT 23, price=999800.0)","_version":1}

        GetResponse<Product> products2 = esClient.get(g -> g
                        .index("products")
                        .id("bk-1"),
                Product.class
        );
        System.out.println(products2);
        //GetResponse: {"_index":"products","found":true,"_id":"bk-1","_primary_term":1,"_seq_no":0,"_source":"Product(sku=bk-1, name=City Bike, price=1599.0)","_version":1}

    }


    @Test
    void simpleSearch() throws IOException {

//        String searchText = "audi";
//        SearchResponse<Product> response1 = esClient.search(
//                new SearchRequest.Builder()
//                        .index("products")
//                        .query(new Query.Builder()
//                                .match(new MatchQuery.Builder()
//                                        .field("sku")
//                                        .query(searchText)
//                                        .build())
//                                .build()
//
//                        ).build(),
//                Product.class
//        );
//        System.out.println(response1);


        SearchResponse<Product> response = esClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .match(t -> t
                                        .field("name")
                                        .query("迪奥")
                                )
                        ),
                Product.class
        );
        System.out.println(response);
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
            log.info("Found product " + product.getSku()+ " "+ product.getName() + ", score " + hit.score());
        }
    }


    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/highlighting.html
     * @throws IOException
     */
    @Test
    void highlighting() throws IOException {

//        HashMap<String, HighlightField> hf = new HashMap<>();
//        hf.put("body",new HighlightField.Builder().preTags("<em>").postTags("</em>").build());
//        hf.put("blog.title",new HighlightField.Builder().numberOfFragments(0).build());
//        hf.put("blog.author",new HighlightField.Builder().numberOfFragments(0).build());
//        hf.put("blog.comment",new HighlightField.Builder().numberOfFragments(5).order(HighlighterOrder.Score).build());
//        SearchResponse<Product> response = esClient.search(s -> s
//                        .index("products")
//                        .query(q -> q
//                                .match(t -> t
//                                        .field("name")
//                                        .query("奥迪")
//                                )
//                        // global settings
//                        ).highlight(h ->h
//                                .numberOfFragments(3)
//                                .fragmentSize(150)
//                                .fields(hf)
//                        )
//                ,
//                Product.class
//        );

        //Elasticsearch supports three highlighters: unified, plain, and fvh (fast vector highlighter). You can specify the highlighter type you want to use for each field.
        SearchResponse<Product> response = esClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .match(t -> t
                                        .field("name")
                                        .query("奥迪")
                                )
                        // global settings
                        ).highlight(h ->h
                                .type("unified")
                                .numberOfFragments(3)
                                .fields("name",hf ->hf)
                        )
                ,
                Product.class
        );

        System.out.println(response);
    }
}
