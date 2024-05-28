package ru.ioannco.aviasales.model.dao.impl;

import org.springframework.stereotype.Controller;
import ru.ioannco.aviasales.model.dao.AirportDAO;
import ru.ioannco.aviasales.model.entity.Airport;

@Controller
public class AirportDAOImpl extends BaseDAOImpl<Airport> implements AirportDAO {
}
