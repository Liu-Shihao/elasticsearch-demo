package com.lsh.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.lsh.properties.ElasticsearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * Spring Boot 3.1.x Elasticsearch 8.9.0  Bug
 * https://github.com/spring-projects/spring-boot/issues/36669
 */
@Configuration
@Slf4j
public class ElasticsearchJavaClientConfig  {

    @Autowired
    ElasticsearchProperties elasticSearchProperties;


    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport elasticsearchTransport) throws IOException {
        ElasticsearchClient client = new ElasticsearchClient(elasticsearchTransport);
        log.info("ElasticsearchClient init success.");
        return client;
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(){
        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(elasticSearchProperties.getFingerprint());

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(elasticSearchProperties.getUsername(),
                        elasticSearchProperties.getPassword())
        );

        RestClient restClient = RestClient
                .builder(new HttpHost(elasticSearchProperties.getHostname(),
                        elasticSearchProperties.getPort(), "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();
        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return transport;
    }



}
