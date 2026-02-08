package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "video_store")
@Getter
@Setter
public class VideoStore extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "video_store_tags", joinColumns = @JoinColumn(name = "video_store_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    private String videoUrl;

    @Column
    private String description;
}
