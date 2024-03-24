package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airline")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Airline
{
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(name = "verbose_name")
    private String verbose;
}
