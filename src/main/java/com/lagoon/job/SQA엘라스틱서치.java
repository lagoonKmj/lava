package com.lagoon.job;

import java.util.Calendar;
import java.util.Date;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.lagoon.LagoonApplication;
import com.lagoon.utils.DateUtils;

@Scope("prototype")
@Service
public class SQA엘라스틱서치 {

    @Autowired private ElasticsearchTemplate elasticsearchTemplate;
    public static final String event_logs_index = "event_logs";
    public static final String cswitch_event_log_type = "cswitch_event_log";
    public static final String timatrix_log_type = "timatrix_log";
    
    public void 조회() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_YEAR, -1);
        startCalendar.set(Calendar.HOUR_OF_DAY, 00);
        startCalendar.set(Calendar.MINUTE, 00);
        startCalendar.set(Calendar.SECOND, 0);
          
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        Long from = startCalendar.getTimeInMillis(); 
        Long to = endCalendar.getTimeInMillis() - 1;
        System.out.println(DateUtils.getFormatString("yyyy-MM-dd HH:mm:ss.SSS", startCalendar.getTime()));
        System.out.println(DateUtils.getFormatString("yyyy-MM-dd HH:mm:ss.SSS", endCalendar.getTime()));
        findTopIpMacByTimeBetween(from, to, 10000);
    }
    
    protected void findTopIpMacByTimeBetween(Long from, Long to, int topCount) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("time").gte(from).lte(to));
        queryBuilder.must(QueryBuilders.termQuery("state", "create"));
        //(srcIp = '' or srcMac = '')
        queryBuilder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("srcIp", "")));
        queryBuilder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("srcMac", "")));
        queryBuilder.minimumShouldMatch("1");
        
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withIndices(event_logs_index).withTypes(timatrix_log_type)
        .withQuery(QueryBuilders.filteredQuery(queryBuilder, null))
        .withSearchType(SearchType.COUNT)
        .addAggregation(
                AggregationBuilders.terms("srcIpMac_group").field("srcIpMac").size(topCount).order(Terms.Order.aggregation("_count", false))
         ).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        
        Terms terms = aggregations.get("srcIpMac_group");
        for (Terms.Bucket bucket : terms.getBuckets()){
            // ${srcIp}/${srcMac}-${portId}+${cswitchId}
            String srcIpMac = bucket.getKey();
            String srcIp = srcIpMac.substring(0, srcIpMac.indexOf("/"));
            String srcMac = srcIpMac.substring(srcIpMac.indexOf("/") + 1, srcIpMac.indexOf("-"));
            long docCount = bucket.getDocCount();
            if (srcIp.equals("") && srcMac.equals("")) {
                System.out.println("#1. SRC IP : " + srcIp + ", SRC MAC : " + srcMac + ", COUNT : " + docCount);
            } else if (srcIp.equals("") && !srcMac.equals("")) {
                System.out.println("##2. SRC IP : " + srcIp + ", SRC MAC : " + srcMac + ", COUNT : " + docCount);
            } else if (!srcIp.equals("") && srcMac.equals("")) {
                System.out.println("###3. SRC IP : " + srcIp + ", SRC MAC : " + srcMac + ", COUNT : " + docCount);
            } else {
                System.out.println("####4. SRC IP : " + srcIp + ", SRC MAC : " + srcMac + ", COUNT : " + docCount);
            }
        }
    }
    
    public static void main(String[] args) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, -7);
        startCalendar.set(Calendar.HOUR_OF_DAY, 00);
        startCalendar.set(Calendar.MINUTE, 00);
        startCalendar.set(Calendar.SECOND, 0);
          
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        Long from = startCalendar.getTimeInMillis(); 
        Long to = endCalendar.getTimeInMillis() - 1;
        System.out.println(DateUtils.getFormatString("yyyy-MM-dd HH:mm:ss.SSS", new Date(from)));
        System.out.println(DateUtils.getFormatString("yyyy-MM-dd HH:mm:ss.SSS", new Date(to)));
        
    }
}
