package com.example.demo.userinfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
//@Table(name="123") 지정시 이 name으로 아니면 class name 으로 중간 대문자 _치환으로 name생성3
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //@Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    //@Column(nullable = false)
    @ColumnDefault("'ROLE_USER'")
    private String roles;
}
