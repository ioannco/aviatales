package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="test")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TestEntity implements BaseEntity {
    @GeneratedValue
    @Id
    Long id;

    @Column(name = "sample_data")
    @NonNull
    String sampleData;
}
