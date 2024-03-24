package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "booking")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Booking {
    @Id
    @GeneratedValue
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client")
    @NonNull
    private Client client;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airline")
    @NonNull
    private Airline airline;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_no")
    @NonNull
    private Flight flight;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "book_dt")
    @NonNull
    private Date bookingDateTime;

    @NonNull
    @Column(name = "is_paid")
    private Boolean isPaid;

    @NonNull
    @Column(name = "price")
    private Float price;

    @NonNull
    @Column(name = "bonus_used")
    private Integer bonusUsed;

    @PrePersist
    protected void onCreate() {
        bookingDateTime = new Date();
    }
}
