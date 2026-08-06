package com.example.it_iap.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.it_iap.entity.enums.ForumPostType;
import tools.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForumPost extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ForumPostType postType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON", nullable = false)
    JsonNode sharedData;

    @Column(name = "is_visible", nullable = false)
    boolean visible = true;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    List<PostReaction> reactions = new ArrayList<>();
}
