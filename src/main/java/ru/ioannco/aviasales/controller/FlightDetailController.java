package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.Flight;

import java.util.Optional;
import java.util.Date;

@Controller
public class FlightDetailController {

    @Autowired
    private FlightDAO flightDAO;

    @GetMapping("/flight/{id}")
    public String getFlightDetails(@PathVariable Long id, Model model) {
        Optional<Flight> flightOptional = flightDAO.getByID(id);
        if (flightOptional.isPresent()) {
            Flight flight = flightOptional.get();
            model.addAttribute("flight", flight);
            model.addAttribute("currentDate", new Date());
            return "flight-details";
        } else {
            return "redirect:/error";
        }
    }
}
