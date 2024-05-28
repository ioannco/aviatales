package ru.ioannco.aviasales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.entity.Client;

@Controller
@RequestMapping("/clients")
public class ClientFormController {

    @Autowired
    private ClientDAO clientDAO;

    @GetMapping("/add")
    public String showAddClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "client-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditClientForm(@PathVariable Long id, Model model) {
        Client client = clientDAO.getByID(id).orElse(null);
        if (client == null) {
            return "redirect:/error";
        }
        model.addAttribute("client", client);
        return "client-form";
    }

    @PostMapping("/save")
    public String saveClient(@RequestParam(required = false) Long id,
                             @RequestParam String firstName,
                             @RequestParam(required = false) String middleName,
                             @RequestParam String lastName,
                             @RequestParam String phoneNumber,
                             @RequestParam String email,
                             @RequestParam(required = false) String address) {
        Client client;
        if (id == null) {
            client = new Client();
        } else {
            client = clientDAO.getByID(id).orElse(new Client());
        }

        client.setFirstName(firstName);
        client.setMiddleName(middleName);
        client.setLastName(lastName);
        client.setPhoneNumber(phoneNumber);
        client.setEmail(email);
        client.setAddress(address);

        if (id == null) {
            clientDAO.save(client);
        } else {
            clientDAO.update(client);
        }

        return "redirect:/clients";
    }
}
