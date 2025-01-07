package com.triptide.backend.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tourist_attractions")
@Getter
@Setter
public class TouristAttraction extends BasePlace {
    private String category;
    
    @ManyToMany
    @JoinTable(
        name = "tourist_attraction_tags",
        joinColumns = @JoinColumn(name = "tourist_attraction_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;
} 