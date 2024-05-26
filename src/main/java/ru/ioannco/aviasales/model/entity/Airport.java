package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airport")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Airport {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(name = "code")
    private String code;

    @NonNull
    @Column(name = "verbose_name")
    private String verbose;

    @NonNull
    @ToString.Exclude

    @JoinColumn(name = "city")
    @ManyToOne(fetch = FetchType.EAGER)
    private City city;
}
