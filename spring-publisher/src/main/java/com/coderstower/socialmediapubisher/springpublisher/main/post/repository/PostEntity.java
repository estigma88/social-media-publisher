package com.coderstower.socialmediapubisher.springpublisher.main.post.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {
    @Id
    private String id;
    private String name;
    private String description;
    @ElementCollection
    private List<String> tags;
    private URL url;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastDatePublished;
}
