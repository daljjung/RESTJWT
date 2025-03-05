package com.example.demo.lectures;

import com.example.demo.userinfo.UserInfo;
import com.example.demo.userinfo.UserInfoSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*; //Object getter,setter등을 annotation을 통해 생성함



import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@EqualsAndHashCode(of="id")
@Entity
public class Lecture {
    @Id //PK로 쓸 필드 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime beginEnrollmentDateTime;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime closeEnrollmentDateTime;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime beginLectureDateTime;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime endLectureDateTime;

    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING) //Enum Class LectureStatus
    private LectureStatus lectureStatus;

    public void update() {
        // Update free
        if (this.basePrice== 0 && this.maxPrice== 0) {
            this.free= true;
        } else {
            this.free= false;
        }
        // Update offline
        if (this.location== null || this.location.isBlank()) {
            this.offline= false;
        } else {
            this.offline= true;
        }
    }

    @ManyToOne
    @JsonSerialize(using = UserInfoSerializer.class)
    private UserInfo userInfo;
}
