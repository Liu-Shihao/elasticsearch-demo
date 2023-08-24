package com.lsh.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.lsh.model.FAQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService{

    private static final String indexName = "products";
    @Autowired
    ElasticsearchClient client;

    @Override
    public Map<String, Object> search(String question) throws IOException {
        client.search(s ->s
                        .index(indexName)
                        .query(q ->q
                                .match(t ->t
                                        .field("question")
                                        .query(question)
                                )

                        )
                , FAQ.class
        );
        return null;
    }
}
