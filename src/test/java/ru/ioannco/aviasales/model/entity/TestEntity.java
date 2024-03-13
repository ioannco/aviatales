package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name="test")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class TestEntity implements BaseEntity {
    @GeneratedValue
    @Id
    Long id;

    @Column(name = "sample_data")
    @NonNull
    String sampleData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(sampleData, that.sampleData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sampleData);
    }
}
