package com.toy.project.studio.auth.entity;

import com.toy.project.studio.config.jpa.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("사용자 아이디")
    private String username;

    @Comment("비밀번호")
    private String password;
}
