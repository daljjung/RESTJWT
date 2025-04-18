package com.example.demo.lectures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class LectureTest {
    @Test
    public void builder(){
        Lecture lecture = Lecture.builder()
                .name("Spring REST API")
                .description("REST API development  with Spring")
                .build();
        assertEquals("Spring REST API",lecture.getName());

        //같을 경우엔 complile 성공, 단순 종료
        //문자 다를 경우 Test 시 실패 오류 발생
    }

    @Test
    public void JavaBean(){
        String name = "Lecture"; //Give
        String description  = "Spring";
        Lecture lecture = new Lecture();  //When
        lecture.setName(name);
        lecture.setDescription(description);
        assertThat(lecture.getName()).isEqualTo("Lecture"); //Then
        assertThat(lecture.getDescription()).isEqualTo("Spring");
    }
}
