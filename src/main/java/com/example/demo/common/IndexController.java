package com.example.demo.common;

import com.example.demo.lectures.LectureController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel index() {

        log.trace("trace message");
        log.debug("debug message");
        log.info("info message");
        log.warn("warn message");
        log.error("error message");

        var index = new RepresentationModel ();
        index.add(linkTo(LectureController.class).withRel("lectures"));
        return index;
    }
}
