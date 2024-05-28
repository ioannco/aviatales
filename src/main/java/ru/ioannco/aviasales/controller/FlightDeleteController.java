package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.Flight;

import java.util.Optional;

@Controller
@RequestMapping("/flights/delete")
public class FlightDeleteController {

    @Autowired
    private FlightDAO flightDAO;

    @GetMapping("/{id}")
    public String deleteFlight(@PathVariable Long id) {
        Optional<Flight> flightOptional = flightDAO.getByID(id);
        if (flightOptional.isPresent()) {
            flightDAO.delete(flightOptional.get());
            return "redirect:/flights";
        } else {
            return "redirect:/error";
        }
    }
}
