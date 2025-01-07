package com.triptide.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;

@Entity
@Table(name = "lodgings")
@Getter
@Setter
public class Lodging extends BasePlace {

    @ManyToMany
    @JoinTable(
        name = "lodging_tags",
        joinColumns = @JoinColumn(name = "lodging_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;
} 