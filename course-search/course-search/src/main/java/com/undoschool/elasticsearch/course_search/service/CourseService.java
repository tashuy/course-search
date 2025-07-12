package com.undoschool.elasticsearch.course_search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoschool.elasticsearch.course_search.entity.CourseDocument;
import com.undoschool.elasticsearch.course_search.repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void bulkIndexCourses() {
        try {
            log.info("Indexing courses from sample-courses.json...");
            InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
            List<CourseDocument> courses = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CourseDocument>>() {}
            );
            courseRepository.saveAll(courses);
            log.info("Indexed {} courses into Elasticsearch.", courses.size());
        } catch (Exception e) {
            log.error("Error indexing courses: {}", e.getMessage(), e);
        }
    }
}