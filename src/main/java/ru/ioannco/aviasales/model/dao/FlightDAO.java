package ru.ioannco.aviasales.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.ioannco.aviasales.model.entity.Flight;

import java.util.List;
import java.util.Date;

public interface FlightDAO extends BaseDAO<Flight> {

    // Метод для получения списка рейсов по фильтру с пагинацией и сортировкой
    List<Flight> getByFilter(Filter filter, int pageSize, int pageNumber, SortOrder sortOrder);

    // Фильтр для поиска рейсов
    @Builder
    @Getter
    class Filter {
        private Date departureDate;
        private Date arrivalDate;
        private String departureCity;
        private String arrivalCity;
        private String departureAirport;
        private String arrivalAirport;
        private String airline;
        private String flightNumber;
        private Boolean purchasable;
        private Boolean availableSeats;
        private Float minPrice;
        private Float maxPrice;
    }

    // Порядок сортировки
    @Getter
    @AllArgsConstructor
    enum SortOrder {
        DEPARTURE_TIME_ASC("departureDateTime", true),
        DEPARTURE_TIME_DESC("departureDateTime", false),
        ARRIVAL_TIME_ASC("arrivalDateTime", true),
        ARRIVAL_TIME_DESC("arrivalDateTime", false),
        PRICE_ASC("price", true),
        PRICE_DESC("price", false),
        AVAILABLE_SEATS_ASC("passengerCount", true),
        AVAILABLE_SEATS_DESC("passengerCount", false);

        private final String field;
        private final boolean ascending;
    }

    // Метод для создания билдера фильтра
    Filter.FilterBuilder getFilterBuilder();
}