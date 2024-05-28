package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.dao.BookingDAO;
import ru.ioannco.aviasales.model.dao.BonusCardDAO;
import ru.ioannco.aviasales.model.entity.Client;
import ru.ioannco.aviasales.model.entity.Booking;
import ru.ioannco.aviasales.model.entity.BonusCard;

import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private BonusCardDAO bonusCardDAO;

    @GetMapping
    public String listClients(@RequestParam(required = false, defaultValue = "") String firstName,
                              @RequestParam(required = false, defaultValue = "") String middleName,
                              @RequestParam(required = false, defaultValue = "") String lastName,
                              @RequestParam(required = false, defaultValue = "") String phoneNumber,
                              @RequestParam(required = false, defaultValue = "") String email,
                              @RequestParam(required = false, defaultValue = "") String address,
                              @RequestParam(required = false, defaultValue = "") String flightNo,
                              @RequestParam(required = false, defaultValue = "false") boolean flightPayed,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {

        ClientDAO.Filter.FilterBuilder filterBuilder = clientDAO.getFilterBuilder();

        if (!firstName.isEmpty()) filterBuilder.firstName(firstName);
        if (!middleName.isEmpty()) filterBuilder.middleName(middleName);
        if (!lastName.isEmpty()) filterBuilder.lastName(lastName);
        if (!phoneNumber.isEmpty()) filterBuilder.phoneNumber(phoneNumber);
        if (!email.isEmpty()) filterBuilder.email(email);
        if (!address.isEmpty()) filterBuilder.address(address);
        if (!flightNo.isEmpty()) filterBuilder.flightNo(flightNo);
        filterBuilder.flightPayed(flightPayed);

        List<Client> clients = clientDAO.getByFilterPaginated(filterBuilder.build(), 20, page);
        long totalClients = clientDAO.countByFilter(filterBuilder.build());

        model.addAttribute("clients", clients);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (totalClients + 20 - 1) / 20);

        // Добавляем текущие значения фильтров в модель
        model.addAttribute("firstName", firstName);
        model.addAttribute("middleName", middleName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("flightNo", flightNo);
        model.addAttribute("flightPayed", flightPayed);

        return "clients";
    }

    @GetMapping("/{id}")
    public String viewClient(@PathVariable Long id, Model model) {
        Client client = clientDAO.getByID(id).orElse(null);
        if (client == null) {
            return "redirect:/error";
        }

        List<Booking> bookings = bookingDAO.getByFilter(BookingDAO.Filter.builder().client(client).build(), BookingDAO.SortOrder.BOOKING_DATE_DESC);
        List<BonusCard> bonusCards = bonusCardDAO.getByFilter(BonusCardDAO.Filter.builder().client(client).build());

        model.addAttribute("client", client);
        model.addAttribute("bookings", bookings);
        model.addAttribute("bonusCards", bonusCards);
        return "client-detail";
    }

    @PostMapping("/{id}/addBonusCard")
    public String addBonusCard(@PathVariable Long id,
                               @RequestParam Integer bonusAmount) {
        Client client = clientDAO.getByID(id).orElse(null);
        if (client == null) {
            return "redirect:/error";
        }

        BonusCard bonusCard = new BonusCard();
        bonusCard.setClient(client);
        bonusCard.setBonusAmount(bonusAmount);

        bonusCardDAO.save(bonusCard);

        return "redirect:/clients/" + id;
    }

    @GetMapping("/{clientId}/deleteBonusCard/{cardId}")
    public String deleteBonusCard(@PathVariable Long clientId, @PathVariable Long cardId) {
        BonusCard bonusCard = bonusCardDAO.getByID(cardId).orElse(null);
        if (bonusCard != null) {
            bonusCardDAO.delete(bonusCard);
        }
        return "redirect:/clients/" + clientId;
    }


    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        Client client = clientDAO.getByID(id).orElse(null);
        if (client != null) {
            clientDAO.delete(client);
        }
        return "redirect:/clients";
    }

    @GetMapping("/bookings/{id}")
    public String viewClientBookings(@PathVariable Long id, Model model) {
        Client client = clientDAO.getByID(id).orElse(null);
        if (client == null) {
            return "redirect:/error";
        }
        List<Booking> bookings = bookingDAO.getByFilter(BookingDAO.Filter.builder().client(client).build(), BookingDAO.SortOrder.BOOKING_DATE_DESC);
        model.addAttribute("client", client);
        model.addAttribute("bookings", bookings);
        return "client-bookings";
    }
}
