package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Table(name = "flight")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Check(constraints = "passenger_count <= passenger_limit")
public class Flight {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(name = "no")
    private String number;

    @NonNull
    @JoinColumn(name = "departure_airport")
    @ManyToOne(fetch = FetchType.EAGER)
    private Airport departureAirport;

    @NonNull
    @JoinColumn(name = "arrival_airport")
    @ManyToOne(fetch = FetchType.EAGER)
    private Airport arrivalAirport;

    @NonNull
    @Column(name = "departure_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date departureDateTime;

    @NonNull
    @Column(name = "arrival_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalDateTime;

    @NonNull
    @Column(name = "price")
    private Float price;

    @NonNull
    @Column(name = "passenger_limit")
    private Integer passengerLimit;

    @NonNull
    @Column(name = "passenger_count")
    private Integer passengerCount;

    @NonNull
    @Column(name = "created_dt")
    @Temporal(TemporalType.DATE)
    private Date createdDateTime;

    @PrePersist
    protected void onCreate() {
        createdDateTime = new Date();
    }
}
