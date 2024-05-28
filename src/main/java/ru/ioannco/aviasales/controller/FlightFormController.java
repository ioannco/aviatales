package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ioannco.aviasales.model.dao.AirlineDAO;
import ru.ioannco.aviasales.model.dao.AirportDAO;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.Flight;
import ru.ioannco.aviasales.model.entity.Airline;
import ru.ioannco.aviasales.model.entity.Airport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/flights")
public class FlightFormController {

    @Autowired
    private FlightDAO flightDAO;

    @Autowired
    private AirlineDAO airlineDAO;

    @Autowired
    private AirportDAO airportDAO;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";

    @GetMapping("/add")
    public String showAddFlightForm(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("airlines", airlineDAO.getAll());
        model.addAttribute("airports", airportDAO.getAll());
        model.addAttribute("dateTimePattern", DATE_TIME_PATTERN);
        return "flight-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditFlightForm(@PathVariable Long id, Model model) {
        Optional<Flight> flightOptional = flightDAO.getByID(id);
        if (flightOptional.isPresent()) {
            model.addAttribute("flight", flightOptional.get());
            model.addAttribute("airlines", airlineDAO.getAll());
            model.addAttribute("airports", airportDAO.getAll());
            model.addAttribute("dateTimePattern", DATE_TIME_PATTERN);
            return "flight-form";
        }
        return "redirect:/flights";
    }

    @PostMapping("/save")
    public String saveFlight(@RequestParam Long id,
                             @RequestParam String number,
                             @RequestParam Long airlineId,
                             @RequestParam Long departureAirportId,
                             @RequestParam Long arrivalAirportId,
                             @RequestParam String departureDateTimeStr,
                             @RequestParam String arrivalDateTimeStr,
                             @RequestParam Float price,
                             @RequestParam Integer passengerLimit,
                             @RequestParam Integer passengerCount) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
        Flight flight;

        try {
            Date departureDateTime = formatter.parse(departureDateTimeStr);
            Date arrivalDateTime = formatter.parse(arrivalDateTimeStr);

            Optional<Airline> airlineOptional = airlineDAO.getByID(airlineId);
            if (!airlineOptional.isPresent()) {
                throw new Exception("Авиакомпания не найдена");
            }

            Optional<Airport> departureAirportOptional = airportDAO.getByID(departureAirportId);
            if (!departureAirportOptional.isPresent()) {
                throw new Exception("Аэропорт вылета не найден");
            }

            Optional<Airport> arrivalAirportOptional = airportDAO.getByID(arrivalAirportId);
            if (!arrivalAirportOptional.isPresent()) {
                throw new Exception("Аэропорт прилета не найден");
            }

            if (id == null || !flightDAO.getByID(id).isPresent()) {
                flight = new Flight();
            } else {
                flight = flightDAO.getByID(id).get();
            }

            flight.setNumber(number);
            flight.setDepartureDateTime(departureDateTime);
            flight.setArrivalDateTime(arrivalDateTime);
            flight.setAirline(airlineOptional.get());
            flight.setDepartureAirport(departureAirportOptional.get());
            flight.setArrivalAirport(arrivalAirportOptional.get());
            flight.setPrice(price);
            flight.setPassengerLimit(passengerLimit);
            flight.setPassengerCount(passengerCount);

            if (id == null || !flightDAO.getByID(id).isPresent()) {
                flightDAO.save(flight);
            } else {
                flightDAO.update(flight);
            }

        } catch (Exception e) {
            return "redirect:/error";
        }

        return "redirect:/flight/" + flight.getId();
    }
}
