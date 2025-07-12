package com.undoschool.elasticsearch.course_search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.undoschool.elasticsearch.course_search.entity.CourseDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSearch {

    private final ElasticsearchClient elasticsearchClient;

    public List<CourseDocument> searchCourses(
            String query, Integer minAge, Integer maxAge,
            String category, String type,
            Double minPrice, Double maxPrice,
            String startDate, String sort, int page, int size) {

        try {
            // 🟢 1. Build filters
            List<Query> filters = new ArrayList<>();

            if (minAge != null || maxAge != null) {
                Query ageRange = RangeQuery.of(r -> r
                        .field("minAge")
                        .gte(minAge != null ? JsonData.of(minAge) : null)
                        .lte(maxAge != null ? JsonData.of(maxAge) : null)
                )._toQuery();
                filters.add(ageRange);
            }

            if (minAge != null || maxAge != null) {
                RangeQuery.Builder ageBuilder = new RangeQuery.Builder().field("minAge");
                if (minAge != null) ageBuilder.gte(JsonData.of(minAge));
                if (maxAge != null) ageBuilder.lte(JsonData.of(maxAge));
                filters.add(ageBuilder.build()._toQuery());
            }

            if (category != null) {
                filters.add(TermQuery.of(t -> t
                        .field("category.keyword")
                        .value(category)
                )._toQuery());
            }

            if (type != null) {
                filters.add(TermQuery.of(t -> t
                        .field("type.keyword")
                        .value(type)
                )._toQuery());
            }

            if (startDate != null) {
                filters.add(RangeQuery.of(r -> r
                        .field("nextSessionDate")
                        .gte(JsonData.of(startDate))
                )._toQuery());
            }

            // 🟢 2. Build main query
            Query mainQuery;
            if (query != null && !query.isEmpty()) {
                mainQuery = BoolQuery.of(b -> b
                        .must(MultiMatchQuery.of(m -> m
                                .fields("title", "description")
                                .query(query)
                        )._toQuery())
                        .filter(filters)
                )._toQuery();
            } else {
                mainQuery = MatchAllQuery.of(m -> m)._toQuery();
            }

// 🟢 3. Sorting
            final String sortField;
            final SortOrder sortOrder;
            if ("priceAsc".equals(sort)) {
                sortField = "price";
                sortOrder = SortOrder.Asc;
            } else if ("priceDesc".equals(sort)) {
                sortField = "price";
                sortOrder = SortOrder.Desc;
            } else {
                sortField = "nextSessionDate";
                sortOrder = SortOrder.Asc;
            }

// 🟢 4. Search request
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("courses")
                    .query(mainQuery)
                    .sort(srt -> srt.field(f -> f
                            .field(sortField)
                            .order(sortOrder)
                    ))
                    .from(page * size)
                    .size(size)
            );

            // 🟢 5. Execute search
            SearchResponse<CourseDocument> response =
                    elasticsearchClient.search(searchRequest, CourseDocument.class);

            List<CourseDocument> courses = new ArrayList<>();
            for (Hit<CourseDocument> hit : response.hits().hits()) {
                courses.add(hit.source());
            }

            return courses;

        } catch (Exception e) {
            log.error("Error executing search: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
