package com.undoschool.elasticsearch.course_search.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Document(indexName = "courses")
public class CourseDocument {
    @Id
    private String id;

    private String title;          // text
    private String description;    // text
    private String category;       // keyword
    private String type;           // keyword (ONE_TIME, COURSE, CLUB)
    private String gradeRange;     // keyword
    private Integer minAge;        // integer
    private Integer maxAge;        // integer
    private Double price;          // double
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private String nextSessionDate;
}