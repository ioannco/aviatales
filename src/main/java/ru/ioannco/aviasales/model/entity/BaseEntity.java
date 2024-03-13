package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


public interface BaseEntity {
    Long getId();
    void setId(Long id);
}
