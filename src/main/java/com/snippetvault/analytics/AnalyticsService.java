package com.snippetvault.analytics;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;

    public AnalyticsSummary getSummary() {
        return new AnalyticsSummary(
                getTotalPublicSnippets(),
                getTopLanguages(),
                getTopTags(),
                getMostViewedSnippets()
        );
    }

    private long getTotalPublicSnippets() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("isPublic").is(true)),
                Aggregation.count().as("total")
        );

        AggregationResults<CountResult> results = mongoTemplate.aggregate(
                agg, "snippets", CountResult.class);

        CountResult result = results.getUniqueMappedResult();
        return result != null ? result.total() : 0;
    }

    private List<LanguageStat> getTopLanguages() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("isPublic").is(true)),
                Aggregation.group("programmingLanguage").count().as("count"),
                Aggregation.project("count").and("_id").as("language"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(5)
        );

        return mongoTemplate.aggregate(agg, "snippets", LanguageStat.class)
                .getMappedResults();
    }

    private List<TagStat> getTopTags() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("isPublic").is(true)),
                Aggregation.unwind("tags"),
                Aggregation.group("tags").count().as("count"),
                Aggregation.project("count").and("_id").as("tag"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(10)
        );

        return mongoTemplate.aggregate(agg, "snippets", TagStat.class)
                .getMappedResults();
    }

    private List<MostViewedSnippet> getMostViewedSnippets() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("isPublic").is(true)),
                Aggregation.sort(Sort.Direction.DESC, "viewCount"),
                Aggregation.limit(5),
                Aggregation.project("title", "viewCount").and("_id").as("id")
        );

        return mongoTemplate.aggregate(agg, "snippets", MostViewedSnippet.class)
                .getMappedResults();
    }

    private record CountResult(long total) {}
}