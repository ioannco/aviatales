package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.Flight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class FlightSearchController {

    @Autowired
    private FlightDAO flightDAO;

    @GetMapping("/flights")
    public String searchFlights(@RequestParam(required = false, defaultValue = "") String departureDate,
                                @RequestParam(required = false, defaultValue = "") String arrivalDate,
                                @RequestParam(required = false, defaultValue = "") String departureCity,
                                @RequestParam(required = false, defaultValue = "") String arrivalCity,
                                @RequestParam(required = false, defaultValue = "") String departureAirport,
                                @RequestParam(required = false, defaultValue = "") String arrivalAirport,
                                @RequestParam(required = false, defaultValue = "") String airline,
                                @RequestParam(required = false, defaultValue = "") String flightNumber,
                                @RequestParam(required = false, defaultValue = "false") boolean purchasable,
                                @RequestParam(required = false, defaultValue = "false") boolean availableSeats,
                                @RequestParam(required = false, defaultValue = "0") float minPrice,
                                @RequestParam(required = false, defaultValue = "0") float maxPrice,
                                @RequestParam(required = false, defaultValue = "") String sortField,
                                @RequestParam(required = false, defaultValue = "false") Boolean sortAsc,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date departureDateParsed = null;
        Date arrivalDateParsed = null;
        try {
            if (!departureDate.isEmpty()) {
                departureDateParsed = formatter.parse(departureDate);
            }
            if (!arrivalDate.isEmpty()) {
                arrivalDateParsed = formatter.parse(arrivalDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FlightDAO.Filter.FilterBuilder filterBuilder = flightDAO.getFilterBuilder();

        if (departureDateParsed != null) {
            filterBuilder.departureDate(departureDateParsed);
        }
        if (arrivalDateParsed != null) {
            filterBuilder.arrivalDate(arrivalDateParsed);
        }
        if (!departureCity.isEmpty()) {
            filterBuilder.departureCity(departureCity);
        }
        if (!arrivalCity.isEmpty()) {
            filterBuilder.arrivalCity(arrivalCity);
        }
        if (!departureAirport.isEmpty()) {
            filterBuilder.departureAirport(departureAirport);
        }
        if (!arrivalAirport.isEmpty()) {
            filterBuilder.arrivalAirport(arrivalAirport);
        }
        if (!airline.isEmpty()) {
            filterBuilder.airline(airline);
        }
        if (!flightNumber.isEmpty()) {
            filterBuilder.flightNumber(flightNumber);
        }
        filterBuilder.purchasable(purchasable);
        filterBuilder.availableSeats(availableSeats);
        if (minPrice > 0) {
            filterBuilder.minPrice(minPrice);
        }
        if (maxPrice > 0) {
            filterBuilder.maxPrice(maxPrice);
        }

        FlightDAO.Filter filter = filterBuilder.build();
        FlightDAO.SortOrder sortOrder = getSortOrder(sortField, sortAsc);

        int pageSize = 20;
        List<Flight> flights = flightDAO.getByFilter(filter, pageSize, page, sortOrder);
        long totalFlights = flightDAO.countByFilter(filter);

        model.addAttribute("flights", flights);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (totalFlights + pageSize - 1) / pageSize);
        model.addAttribute("currentDate", new Date()); // Добавляем текущее время в модель

        // Добавляем текущие значения фильтров в модель
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("arrivalDate", arrivalDate);
        model.addAttribute("departureCity", departureCity);
        model.addAttribute("arrivalCity", arrivalCity);
        model.addAttribute("departureAirport", departureAirport);
        model.addAttribute("arrivalAirport", arrivalAirport);
        model.addAttribute("airline", airline);
        model.addAttribute("flightNumber", flightNumber);
        model.addAttribute("purchasable", purchasable);
        model.addAttribute("availableSeats", availableSeats);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortAsc", sortAsc);

        return "flights";
    }

    private FlightDAO.SortOrder getSortOrder(String sortField, Boolean sortAsc) {
        if (sortField == null || sortField.isEmpty()) {
            return FlightDAO.SortOrder.DEPARTURE_TIME_ASC; // Сортировка по умолчанию
        }

        boolean ascending = sortAsc != null ? sortAsc : false;
        switch (sortField) {
            case "departureTime":
                return ascending ? FlightDAO.SortOrder.DEPARTURE_TIME_ASC : FlightDAO.SortOrder.DEPARTURE_TIME_DESC;
            case "arrivalTime":
                return ascending ? FlightDAO.SortOrder.ARRIVAL_TIME_ASC : FlightDAO.SortOrder.ARRIVAL_TIME_DESC;
            case "price":
                return ascending ? FlightDAO.SortOrder.PRICE_ASC : FlightDAO.SortOrder.PRICE_DESC;
            case "availableSeats":
                return ascending ? FlightDAO.SortOrder.AVAILABLE_SEATS_ASC : FlightDAO.SortOrder.AVAILABLE_SEATS_DESC;
            default:
                return FlightDAO.SortOrder.DEPARTURE_TIME_ASC; // Сортировка по умолчанию
        }
    }
}
