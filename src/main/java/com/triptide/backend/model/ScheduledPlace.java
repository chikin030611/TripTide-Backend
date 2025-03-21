package com.triptide.backend.model;

import java.sql.Time;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scheduled_places")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    private String notes;

    @Column(name = "date", nullable = false)
    private java.util.Date date;

    @ManyToOne
    @JoinColumn(name = "daily_itinerary_id", nullable = false)
    @JsonBackReference
    private DailyItinerary dailyItinerary;
} 