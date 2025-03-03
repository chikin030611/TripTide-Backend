package com.triptide.backend.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ElementCollection
    @CollectionTable(name = "trip_tourist_attractions", 
                    joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "tourist_attraction_id")
    private List<String> touristAttractionIds;

    @ElementCollection
    @CollectionTable(name = "trip_restaurants", 
                    joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "restaurant_id")
    private List<String> restaurantIds;

    @ElementCollection
    @CollectionTable(name = "trip_lodgings", 
                    joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "lodging_id")
    private List<String> lodgingIds;

    private String image;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyItinerary> dailyItineraries;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
} 