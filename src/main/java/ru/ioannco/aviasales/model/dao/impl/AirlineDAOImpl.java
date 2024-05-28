package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.Column;
import org.springframework.stereotype.Controller;
import ru.ioannco.aviasales.model.dao.AirlineDAO;
import ru.ioannco.aviasales.model.dao.BaseDAO;
import ru.ioannco.aviasales.model.entity.Airline;

@Controller
public class AirlineDAOImpl extends BaseDAOImpl<Airline> implements AirlineDAO {
}
