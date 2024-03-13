package ru.ioannco.aviasales.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "client")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class Client implements BaseEntity{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(firstName, client.firstName) && Objects.equals(middleName, client.middleName) && Objects.equals(lastName, client.lastName) && Objects.equals(phoneNumber, client.phoneNumber) && Objects.equals(email, client.email) && Objects.equals(address, client.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleName, lastName, phoneNumber, email, address);
    }
}
