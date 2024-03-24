package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "city")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class City implements BaseEntity {
    @GeneratedValue
    @Id
    private Long id;

    @NonNull
    @Column(name = "verbose_name")
    private String verbose;
}
