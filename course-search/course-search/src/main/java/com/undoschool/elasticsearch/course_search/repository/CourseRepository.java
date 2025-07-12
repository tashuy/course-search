package com.undoschool.elasticsearch.course_search.repository;

import com.undoschool.elasticsearch.course_search.entity.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
}
