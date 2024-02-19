package com.qiuzhitech.onlineshopping_03.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ElasticsearchService {
    @Resource
    RestHighLevelClient client;

    //Create commodity
    public void addCommodityToEs(OnlineShoppingCommodity commodity) {
        try {
            //Create Index if not created
            String indexName = "commodity";
            boolean indexExists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);

            if(!indexExists){
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject()
                        .startObject("dynamic_templates")
                        .startObject("strings")
                        .field("match_mapping_type", "string")
                        .startObject("mapping")
                        .field("type", "text")
                        .field("analyzer", "ik_smart")
                        .endObject()
                        .endObject()
                        .endObject()
                        .endObject();
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                request.source(builder);
                CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

                if (!response.isAcknowledged()) {
                    log.error("Failed to Create ES Index: commodity");
                    return;
                }
            }

            // Create Document into Commodity Index
            String record = JSON.toJSONString(commodity);
            IndexRequest request = new IndexRequest("commodity").source(record, XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            log.info("Add commodity to ES, commodity:{}, result:{}", record, response);
            response.status();
        } catch (Exception e) {
            log.error("SearchService addCommodityToEs error", e);
        }
    }


    //Search commodity
    public List<OnlineShoppingCommodity> searchCommodities(String keyword, int from, int size) {
        try {
            SearchRequest searchRequest = new SearchRequest("commodity");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "commodityName", "commodityDesc");
            searchSourceBuilder.query(queryBuilder);
            searchSourceBuilder.from(from);
            searchSourceBuilder.size(size);
            searchSourceBuilder.sort("price", SortOrder.DESC);
            searchRequest.source(searchSourceBuilder);

            //Get search response
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(searchResponse));

            //Get hits
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();

            long totalNum = searchHits.getTotalHits().value;
            log.info("Number of search results: {}", totalNum);

            List<OnlineShoppingCommodity> searchResults = new ArrayList<>();
            for(SearchHit hit : hits){
               String sourceAsString = hit.getSourceAsString();
                OnlineShoppingCommodity commodity = JSON.parseObject(sourceAsString, OnlineShoppingCommodity.class);
                searchResults.add(commodity);
            }
            log.info("Search result {}", JSON.toJSONString(searchResults));
            return searchResults;
        } catch (Exception e) {
            log.error("SearchService searchCommodities error", e);
            return null;
        }
    }


}
