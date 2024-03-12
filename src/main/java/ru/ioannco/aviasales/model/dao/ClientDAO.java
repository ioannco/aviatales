package ru.ioannco.aviasales.model.dao;

import lombok.Builder;
import lombok.Getter;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO extends BaseDAO<Client> {

    List<Client> getByFilter(Filter filter);
    List<Client> getByFilterPaginated(Filter filter, int pageSize, int pageNumber);

    @Builder
    @Getter
    class Filter {
        String firstName;
        String middleName;
        String lastName;
        String phoneNumber;
        String email;
        String address;
    }
}
