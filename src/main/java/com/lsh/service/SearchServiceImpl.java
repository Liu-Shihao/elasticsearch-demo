package com.lsh.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.lsh.model.FAQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService{

    private static final String indexName = "faq";
    @Autowired
    ElasticsearchClient client;

    @Override
    public Map<String, Object> search(String question) throws IOException {
        HashMap<String, Object> data = new HashMap<>();
        SearchResponse<FAQ> response = client.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                .match(t -> t
                                        .field("question")
                                        .query(question)
                                )

                        )
                        .highlight(h ->h
                                .fields("question",
                                        hf ->hf
                                                .preTags("<em>")
                                                .postTags("</em>")))
                , FAQ.class
        );
        log.info("search result :{}",response);
        TotalHits total = response.hits().total();
        List<Hit<FAQ>> hits = response.hits().hits();
        ArrayList<FAQ> faqs = new ArrayList<>();
        for (Hit<FAQ> hit: hits) {
            FAQ source = hit.source();
            source.setScore(hit.score());
            source.setHighlight(hit.highlight());
            faqs.add(source);
        }
        data.put("data",faqs);
        data.put("total",total.value());
        return data;
    }



}
