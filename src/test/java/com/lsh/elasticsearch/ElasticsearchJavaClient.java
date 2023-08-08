package com.lsh.elasticsearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
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

public class ElasticsearchJavaClient {
    private static String login = "elastic";
    private static String password = "n6ahTGrZ4JBLTN3ntj9T";
    private static String host = "localhost";
    private static int port = 9200;
    private static String fingerprint = "5a1300b9d988ac9840f02a2e074384046c792249d869e36005aa7b00f7d742db";

    private static ElasticsearchClient client;

    /**
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
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }


    /**
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
