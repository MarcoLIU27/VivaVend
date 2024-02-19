package com.qiuzhitech.onlineshopping_03.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    @Bean
    public RestHighLevelClient EsClient(){
        RestClientBuilder client = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        return new RestHighLevelClient(client);
    }

}
