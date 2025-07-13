package com.undoschool.elasticsearch.course_search.Controller;

import com.undoschool.elasticsearch.course_search.entity.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CourseController {

    private final com.undoschool.elasticsearch.course_search.service.CourseSearch courseSearch;

    // 🔥 Autocomplete endpoint
    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam("q") String query) {
        return courseSearch.suggest(query);  // 🟢 FIXED THIS LINE
    }

    @GetMapping
    public List<CourseDocument> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return courseSearch.searchCourses(
                q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size
        );
    }
}
