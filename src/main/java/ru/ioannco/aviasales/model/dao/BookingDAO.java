package ru.ioannco.aviasales.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.ioannco.aviasales.model.entity.Booking;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.List;

public interface BookingDAO extends BaseDAO<Booking> {

    // Метод для получения списка бронирований по фильтру с пагинацией и сортировкой
    List<Booking> getByFilter(Filter filter, int pageSize, int pageNumber, SortOrder sortOrder);

    // Фильтр для поиска бронирований
    @Builder
    @Getter
    class Filter {
        private Client client;
    }

    // Порядок сортировки
    @Getter
    @AllArgsConstructor
    enum SortOrder {
        BOOKING_DATE_ASC("bookingDateTime", true),
        BOOKING_DATE_DESC("bookingDateTime", false),
        PRICE_ASC("price", true),
        PRICE_DESC("price", false);

        private final String field;
        private final boolean ascending;
    }

    // Метод для создания билдера фильтра
    Filter.FilterBuilder getFilterBuilder();
}