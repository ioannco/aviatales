package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bonus_card")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class BonusCard {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @ToString.Exclude
    @JoinColumn(name = "client")
    @ManyToOne(fetch = FetchType.EAGER)
    private Client client;

    @NonNull
    @ToString.Exclude
    @JoinColumn(name = "airline")
    @ManyToOne(fetch = FetchType.EAGER)
    private Airline airline;

    @NonNull
    @Column(name = "bonus_amount")
    private Integer bonusAmount;
}
