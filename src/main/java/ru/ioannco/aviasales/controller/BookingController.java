package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ioannco.aviasales.model.dao.BookingDAO;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.Booking;
import ru.ioannco.aviasales.model.entity.Client;
import ru.ioannco.aviasales.model.entity.Flight;

import java.util.Optional;

@Controller
@RequestMapping("/book")
public class BookingController {

    @Autowired
    private FlightDAO flightDAO;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @GetMapping("/{flightId}")
    public String showBookingForm(@PathVariable Long flightId, Model model) {
        Optional<Flight> flightOptional = flightDAO.getByID(flightId);
        if (flightOptional.isPresent()) {
            model.addAttribute("flight", flightOptional.get());
            return "booking-form";
        } else {
            return "redirect:/error";
        }
    }

    @PostMapping("/submit")
    public String submitBooking(@RequestParam Long flightId,
                                @RequestParam String email,
                                Model model) {
        Optional<Flight> flightOptional = flightDAO.getByID(flightId);
        if (!flightOptional.isPresent()) {
            return "redirect:/error";
        }
        Flight flight = flightOptional.get();

        Optional<Client> clientOptional = clientDAO.getByFilter(ClientDAO.Filter.builder().email(email).build()).stream().findFirst();
        Client client;
        if (clientOptional.isPresent())
            client = clientOptional.get();
        else {
            client = new Client("", "", "", "", email, "");
            clientDAO.save(client);
        }

        Booking booking = new Booking();
        booking.setClient(client);
        booking.setFlight(flight);
        booking.setAirline(flight.getAirline());
        booking.setPrice(flight.getPrice());
        booking.setIsPaid(false);
        booking.setBonusUsed(0);

        bookingDAO.save(booking);

        return "redirect:/payment/" + booking.getId();
    }
}
