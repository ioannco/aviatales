package ru.ioannco.aviasales.model.dao;

import lombok.Builder;
import lombok.Getter;
import ru.ioannco.aviasales.model.entity.BonusCard;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.List;

public interface BonusCardDAO extends BaseDAO<BonusCard> {

    // Метод для получения списка бонусных карт по фильтру с пагинацией
    List<BonusCard> getByFilter(Filter filter);

    // Фильтр для поиска бонусных карт
    @Builder
    @Getter
    class Filter {
        private Client client;
    }

    // Метод для создания билдера фильтра
    Filter.FilterBuilder getFilterBuilder();
}