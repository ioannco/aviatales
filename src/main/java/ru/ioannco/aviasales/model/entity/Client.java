package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "client")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class Client {
    @Id
    @GeneratedValue
    public Long id;

    @NonNull
    @Column(name = "first_name")
    String firstName;

    @NonNull
    @Column(name = "middle_name")
    String middleName;

    @NonNull
    @Column(name = "last_name")
    String lastName;

    @NonNull
    @Column(name = "phone_number")
    String phoneNumber;

    @NonNull
    @Column(name = "email")
    String email;

    @NonNull
    @Column(name = "address")
    String address;
}
