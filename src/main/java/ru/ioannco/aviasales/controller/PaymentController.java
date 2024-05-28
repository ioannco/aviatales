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
import ru.ioannco.aviasales.model.dao.BonusCardDAO;
import ru.ioannco.aviasales.model.entity.Booking;
import ru.ioannco.aviasales.model.entity.BonusCard;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private BonusCardDAO bonusCardDAO;

    @GetMapping("/{bookingId}")
    public String showPaymentForm(@PathVariable Long bookingId, Model model) {
        Optional<Booking> bookingOptional = bookingDAO.getByID(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            List<BonusCard> bonusCards = bonusCardDAO.getByFilter(BonusCardDAO.Filter.builder().client(booking.getClient()).build());
            model.addAttribute("booking", booking);
            model.addAttribute("bonusCards", bonusCards);
            return "payment-form";
        } else {
            return "redirect:/error";
        }
    }

    @PostMapping("/submit")
    public String submitPayment(@RequestParam Long bookingId,
                                @RequestParam(required = false) Long bonusCardId,
                                @RequestParam(required = false) Integer bonusUsed,
                                Model model) {
        Optional<Booking> bookingOptional = bookingDAO.getByID(bookingId);
        if (!bookingOptional.isPresent()) {
            return "redirect:/error";
        }
        Booking booking = bookingOptional.get();

        if (bonusCardId != null && bonusUsed != null) {
            Optional<BonusCard> bonusCardOptional = bonusCardDAO.getByID(bonusCardId);
            if (!bonusCardOptional.isPresent()) {
                return "redirect:/error";
            }
            BonusCard bonusCard = bonusCardOptional.get();

            if (bonusCard.getBonusAmount() < bonusUsed) {
                return "redirect:/error";
            }

            booking.setBonusUsed(bonusUsed);
            bonusCard.setBonusAmount(bonusCard.getBonusAmount() - bonusUsed);
            bonusCardDAO.update(bonusCard);
        }

        // Обработка оплаты
        booking.setIsPaid(true);
        bookingDAO.update(booking);

        model.addAttribute("booking", booking);
        return "payment-success";
    }
}
