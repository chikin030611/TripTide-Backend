package com.triptide.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lodging")
@Getter
@Setter
public class Lodging extends BasePlace {
} 