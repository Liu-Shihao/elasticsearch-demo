package com.lsh.elasticsearch;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;

@Slf4j
public class ElasticsearchJavaClient {
    private static String login = "elastic";
    private static String password = "n6ahTGrZ4JBLTN3ntj9T";
    private static String host = "localhost";
    private static int port = 9200;
    private static String fingerprint = "5a1300b9d988ac9840f02a2e074384046c792249d869e36005aa7b00f7d742db";

    private static ElasticsearchTransport transport;
    private static ElasticsearchClient client;

    /**
     * 创建Elasticsearch客户端
     * Connecting Elasticsearch
     * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/connecting.html
     * Create the low-level client
     */
    @BeforeAll
    static void createEsJavaClient(){

        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(fingerprint);

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(login, password)
        );
        RestClient restClient = RestClient
                .builder(new HttpHost(host, port, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();

        // Create the transport and the API client
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }

    /**
     * 创建阻塞和异步客户端
     * API clients come in two flavors: blocking and asynchronous. All methods on asynchronous clients return a standard CompletableFuture.
     */
    @Test
    void blockingAndAsynchronousClient() throws IOException {
        // Synchronous blocking client
        ElasticsearchClient client = new ElasticsearchClient(transport);

        if (client.exists(b -> b.index("products").id("foo")).value()) {
            log.info("product exists");
        }


        // Asynchronous non-blocking client
        ElasticsearchAsyncClient asyncClient = new ElasticsearchAsyncClient(transport);
        asyncClient
                .exists(b -> b.index("products").id("foo"))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        log.error("Failed to index", exception);
                    } else {
                        log.info("Product exists");
                    }
                });
    }




    /**
     * 创建index索引
     * Create an index
     * @throws IOException
     */
    @Test
    void createIndex() throws IOException {
        client.indices().create(c -> c
                .index("products")
        );
    }
}
